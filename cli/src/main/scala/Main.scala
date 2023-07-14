import playbot.Executable
import playbot.ExecutionContext
import playbot.Settings
import playbot.adapters.fetchers.UrlContentFetcherDispatcher
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Tag
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.TextBot
import playbot.domain.ports.TextMessage
import playbot.domain.usecases.SaveContentImpl

import java.net.{URL => UrlParser}
import scala.util.Try

def contentToString(content: Content): String =
  val duration =
    val seconds = content.duration.getSeconds()
    if seconds >= 3600 then
      f"${(seconds / 3600)}%02d:${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"
    else f"${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"

  val tags = content.tags.map(t => s"#${t.name}").mkString(" ")
  s"[${content.id}] ${content.title} | ${content.author} ($duration) $tags"

@main def cli(url: String) =
  val settings = Settings()
  given ExecutionContext(
    contentRepository = new ContentRepository {
      def addTags(contentId: Int, tags: List[Tag]): Try[Unit] =
        Try {}

      def save(
          content: UrlContent,
          user: User,
          channel: Channel
      ): Try[Content] =
        Try {
          Content(
            author = content.author,
            duration = content.duration,
            externalId = content.externalId,
            id = 0,
            site = content.site,
            title = content.title,
            url = content.url,
            tags = List[Tag]()
          )
        }

      def getById(id: Int): Option[Content] =
        None

      def search(query: String): Option[Content] =
        None
    },
    settings = settings,
    urlContentFetcher = UrlContentFetcherDispatcher()
  )

  for
    url <- Url(url)
    content <- SaveContentImpl(url, User(""), Channel("")).perform()
  yield println(contentToString(content))
