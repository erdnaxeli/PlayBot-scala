package playbot.domain.usecases

import playbot.Settings
import playbot.UnitSpec
import playbot.defaultContent
import playbot.defaultUrlContent
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Site
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher
import playbot.domain.usecases.SaveContent

import java.time.Duration
import scala.collection.mutable.ArrayBuffer
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class SaveContentSpec extends UnitSpec:
  val urlContent = defaultUrlContent

  given Settings(List[String](), true, "", "", "")

  given urlContentFetcher: UrlContentFetcher with
    var calledWith = ArrayBuffer[Url]()

    def get(url: Url)(using Settings): Option[UrlContent] =
      calledWith += url
      Some(
        urlContent
      )

  given successContentRepository: ContentRepository with
    val calledWith = ArrayBuffer[(UrlContent, User, Channel)]()

    def save(content: UrlContent, user: User, channel: Channel): Try[Content] =
      calledWith += ((content, user, channel))
      Success(defaultContent)

    def getById(id: Int) = None
    def search(query: String) = None

  given failureContentRepository: ContentRepository with
    def save(content: UrlContent, user: User, channel: Channel): Try[Content] =
      Failure(Exception())

    def getById(id: Int) = None
    def search(query: String) = None

  behavior of "SaveContent"

  it should "fetch content and return it" in {
    val url = Url.Soundcloud("id")
    val channel = Channel("#chan")
    val user = User("user")

    val result =
      SaveContent(url, user, channel)(using successContentRepository).perform()

    urlContentFetcher.calledWith shouldEqual ArrayBuffer(url)
    successContentRepository.calledWith shouldEqual ArrayBuffer(
      (urlContent, user, channel)
    )
    result.value shouldEqual defaultContent
  }

  it should "return None on failure" in {
    val url = Url.Soundcloud("id")
    val channel = Channel("#chan")
    val user = User("user")

    val result =
      SaveContent(url, user, channel)(using failureContentRepository).perform()

    result shouldEqual None
  }
