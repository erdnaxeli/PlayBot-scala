package playbot.domain.usecases

import playbot.Settings
import playbot.UnitSpec
import playbot.domain.entities.Site
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher
import playbot.domain.usecases.SaveContent

import java.time.Duration
import scala.collection.mutable.ArrayBuffer

class SaveContentSpec extends UnitSpec:
  val urlContent = UrlContent(
    "George Abitbol",
    Duration.ofSeconds(150),
    "42",
    Site.Soundcloud,
    "La classe Américaine"
  )

  given Settings(List[String](), true, "", "", "")

  given urlContentFetcher: UrlContentFetcher with
    var calledWith = ArrayBuffer[Url]()

    def get(url: Url)(using Settings): Option[UrlContent] =
      calledWith += url
      Some(
        urlContent
      )

  given contentRepository: ContentRepository with
    val calledWith = ArrayBuffer[UrlContent]()

    def save(content: UrlContent): Unit =
      calledWith += content

    def getById(id: Int) = None
    def search(query: String) = None

  behavior of "SaveContent"

  it should "fetch content and save it" in {
    val url = Url.Soundcloud("id")

    SaveContent(url).perform()

    urlContentFetcher.calledWith shouldEqual ArrayBuffer(url)
    contentRepository.calledWith shouldEqual ArrayBuffer(urlContent)
  }
