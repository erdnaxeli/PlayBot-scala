package playbot.adapters

import playbot.Settings
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Site
import playbot.domain.entities.Tag
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.Duration
import javax.security.auth.login.FailedLoginException
import scala.util.Failure
import scala.util.Random
import scala.util.Try

class ContentRepositoryMsql(settings: Settings) extends ContentRepository:
  private val url =
    s"jdbc:mariadb://${settings.db_host}/${settings.db_name}"

  def addTags(contentId: Int, tags: List[Tag]): Try[Unit] =
    val conn = get_connection
    val insert_tags_stmt = conn.prepareStatement(
      """
      INSERT INTO playbot_tags (id, tag)
      VALUES (?, ?)
      ON DUPLICATE KEY UPDATE
        tag = tag
      """
    )

    tags match
      case Nil => Try {}
      case _ =>
        insert_tags_stmt.clearBatch()
        insert_tags_stmt.clearParameters()

        tags.map(tag =>
          insert_tags_stmt.setInt(1, contentId)
          insert_tags_stmt.setString(2, tag.name)
          insert_tags_stmt.addBatch()
        )

        Try {
          insert_tags_stmt.executeBatch()
          conn.commit()
        }.recover(e =>
          conn.rollback()
          Failure(e)
        )

  def save(content: UrlContent, user: User, channel: Channel): Try[Content] =
    val conn = get_connection
    val insert_content_stmt = conn.prepareStatement(
      """
      INSERT INTO playbot (type, url, external_id, sender, title, duration, playlist)
      VALUES (?, ?, ?, ?, ?, ?, 0)
      ON DUPLICATE KEY UPDATE
        sender = VALUE(sender),
        title = VALUE(title),
        duration = VALUE(duration),
        eatshit = rand()
      """,
      java.sql.Statement.RETURN_GENERATED_KEYS
    )
    val insert_chan_stmt = conn.prepareStatement(
      """
      INSERT INTO playbot_chan (content, chan, sender_irc)
      VALUES (?, ?, ?)
      """
    )

    Try {
      insert_content_stmt.clearParameters()
      insert_content_stmt.setString(1, content.site.toString.toLowerCase)
      insert_content_stmt.setString(2, content.url)
      insert_content_stmt.setString(3, content.externalId)
      insert_content_stmt.setString(4, content.author)
      insert_content_stmt.setString(5, content.title)
      insert_content_stmt.setLong(6, content.duration.toSeconds)
      insert_content_stmt.executeUpdate()

      val rs = insert_content_stmt.getGeneratedKeys()
      if rs.next() then
        val contentId = rs.getLong("GENERATED_KEY");

        insert_chan_stmt.setLong(1, contentId)
        insert_chan_stmt.setString(2, channel.name)
        insert_chan_stmt.setString(3, user.name)
        insert_chan_stmt.executeUpdate

        contentId
      else throw Exception("No generated keys found")
    }.map(contentId =>
      conn.commit()
      Content(
        content.author,
        content.duration,
        externalId = content.externalId,
        id = contentId.toInt,
        site = content.site,
        title = content.title,
        url = content.url
      )
    ).recoverWith(e =>
      conn.rollback()
      Failure(e)
    )

  def getById(id: Int): Option[Content] =
    val conn = get_connection
    val get_content_stmt = conn.prepareStatement(
      """
      SELECT type, url, external_id, sender, title, duration, tag
      FROM playbot p
      LEFT OUTER JOIN playbot_tags pt ON p.id = pt.id
      WHERE p.id = ?
      """
    )
    get_content_stmt.setInt(1, id)
    val rs = get_content_stmt.executeQuery

    if rs.next() then
      val content = Content(
        author = rs.getString("sender"),
        duration = Duration.ofSeconds(rs.getInt("duration")),
        externalId = rs.getString("external_id"),
        id = id,
        site = Site.valueOf(rs.getString("type").capitalize),
        title = rs.getString("title"),
        url = rs.getString("url")
      )

      var tags = List[Tag]()
      val tag = rs.getString("tag")
      if tag != null then tags = Tag(tag) :: tags

      while rs.next() do
        val tag = rs.getString("tag")
        if tag != null then tags = Tag(tag) :: tags

      Some(content.copy(tags = tags))
    else None

  def search(query: String): Option[Content] = ???

  private def get_connection: Connection =
    val conn =
      DriverManager.getConnection(url, settings.db_user, settings.db_password)
    conn.setAutoCommit(false)
    conn
