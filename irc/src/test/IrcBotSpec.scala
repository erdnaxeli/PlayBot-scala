package playbot.controllers

import org.jibble.pircbot.PircBot
import playbot.Settings
import playbot.UnitSpec
import playbot.controllers.irc.IrcBot
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Site
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher

import java.time.Duration
import scala.collection.mutable.ArrayBuffer
import scala.util.Failure
import scala.util.Try

class IrcBotSpec extends UnitSpec:
  val urlContent = UrlContent(
    "George Abitbol",
    Duration.ofSeconds(150),
    "42",
    Site.Soundcloud,
    "La classe Américaine",
    "https://soundcloud.com/abitbol/classe"
  )

  given Settings(List[String](), true, "", "", "")

  given UrlContentFetcher with
    def get(url: Url)(using Settings): Option[UrlContent] =
      Some(
        UrlContent(
          author = "Ophidian",
          duration = Duration.ofSeconds(150),
          externalId = "djophidian/ophidian-love-is-digital",
          site = Site.Soundcloud,
          title = "Love is Digital",
          url = "https://soundcloud.com/djophidian/ophidian-love-is-digital"
        )
      )

  given contentRepository: ContentRepository with
    val calledWith = ArrayBuffer[(UrlContent, User, Channel)]()

    def save(content: UrlContent, user: User, channel: Channel): Try[Content] =
      Failure(Exception())

    def getById(id: Int) = None
    def search(query: String) = None

  behavior of "IrcBot"

  it should "save content" in {
    IrcBot("test").onMessage(
      "#test",
      "moise",
      "moise@host",
      "host",
      "test https://soundcloud.com/djophidian/ophidian-love-is-digital"
    )
  }
