package playbot.domain.ports

import playbot.Settings
import playbot.UnitSpec
import playbot.defaultContent
import playbot.defaultSettings
import playbot.defaultUrlContent
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User

import scala.util.Success
import scala.util.Try

class TextBotSpec extends UnitSpec:
  given Settings = defaultSettings

  given ContentRepository with
    def save(content: UrlContent, user: User, channel: Channel): Try[Content] =
      Success(defaultContent)

    def getById(id: Int) = None
    def search(query: String) = None

  behavior of "TextBot"

// it should "match urls, save content and return a TextMessage" in {
//   TextBot().processMessage(
//     TextMessage(
//       Channel("#channel"),
//       User("user"),
//       "hello http://youtube.fr/watch?v=123456"
//     )
//   )
// }
