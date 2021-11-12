package playbot.adapters

import com.mysql.jdbc.Statement
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import javax.security.auth.login.FailedLoginException
import scala.util.Failure
import scala.util.Success
import scala.util.Try

given contentRepositoryMysql: ContentRepository with {
  Class.forName("com.mysql.jdbc.Driver")
  private val url =
    "jdbc:mysql://localhost/assoce_nightiies"
  private val connection =
    DriverManager.getConnection(url, "assoce_nightiies", "")
  connection.setAutoCommit(false)
  private val insert_content_stmt = connection.prepareStatement(
    """
    INSERT INTO playbot (type, url, external_id, sender, title, duration, playlist)
    VALUES (?, ?, ?, ?, ?, ?, 0)
    ON DUPLICATE KEY UPDATE
      sender = VALUE(sender),
      title = VALUE(title),
      duration = VALUE(duration)
    """,
    java.sql.Statement.RETURN_GENERATED_KEYS
  )
  private val insert_chan_stmt = connection.prepareStatement(
    """
    INSERT INTO playbot_chan (content, chan, sender_irc)
    VALUES (?, ?, ?)
    """
  )

  def save(content: UrlContent, user: User, channel: Channel): Try[Content] =
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

        insert_chan_stmt.setLong(1, 1)
        insert_chan_stmt.setString(2, channel.name)
        insert_chan_stmt.setString(3, user.name)
        insert_chan_stmt.executeUpdate

        connection.commit()
        contentId
      else throw Exception()
    } match
      case Success(contentId) =>
        connection.commit()
        Success(
          Content(
            content.author,
            content.duration,
            externalId = content.externalId,
            id = contentId.toInt,
            site = content.site,
            title = content.title,
            url = content.url
          )
        )
      case Failure(e) =>
        connection.rollback
        Failure(e)

  def getById(id: Int): Option[Content] = ???

  def search(query: String): Option[Content] = ???
}
