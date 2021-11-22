package playbot.domain.ports

import playbot.Executable
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Tag
import playbot.domain.entities.Url
import playbot.domain.entities.User
import playbot.domain.usecases.AddTagsImpl
import playbot.domain.usecases.GetContentImpl
import playbot.domain.usecases.SaveContentImpl

import java.net.{URL => UrlParser}
import scala.util.Try

case class TextMessage(
    channel: Channel,
    sender: User,
    value: String
)

trait TextBot:
  def processMessage(
      message: TextMessage
  ): Executable[Option[TextMessage]] =
    extractUrl(message.value)
      .flatMap(Url(_))
      .flatMap(
        SaveContentImpl(_, message.sender, message.channel).perform()
      )
      .flatMap(content =>
        AddTagsImpl()
          .perform(content.id, extractTags(message.value).map(Tag(_)))
        GetContentImpl()
          .perform(content.id)
          .map(content => message.copy(value = contentToString(content)))
      )

  private def extractTags(msg: String): List[String] =
    val regex = "#([a-zA-Z0-9_]+)".r
    msg
      .split(raw"\s+")
      .flatMap(_ match
        case regex(tag) => Some(tag)
        case _          => None
      )
      .toList

  private def extractUrl(msg: String): Option[String] =
    msg
      .split(raw"\s+")
      .iterator
      .map(p => Try(UrlParser(p)).map(_.toString).toOption)
      .flatten
      .nextOption

  def contentToString(content: Content): String
