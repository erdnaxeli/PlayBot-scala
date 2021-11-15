package playbot.domain.ports

import playbot.Executable
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Url
import playbot.domain.entities.User
import playbot.domain.usecases.SaveContentImpl

import java.net.{URL => UrlParser}
import scala.util.Try

case class TextMessage[T](
    channel: Channel,
    sender: User,
    value: String
)

class TextBot[T]:
  def processMessage(
      message: TextMessage[T]
  ): Executable[Option[TextMessage[T]]] =
    val a: Option[TextMessage[T]] = extractUrl(message.value)
      .flatMap(Url(_))
      .flatMap(
        SaveContentImpl(_, message.sender, message.channel).perform()
      )
      .map(content => message.copy(value = contentToString(content)))

    println("end")
    a

  private def extractUrl(msg: String): Option[String] =
    msg
      .split(raw"\s+")
      .iterator
      .map(p => Try(UrlParser(p)).map(_.toString).toOption)
      .flatten
      .nextOption

  private def contentToString(content: Content): String =
    val duration =
      val seconds = content.duration.getSeconds()
      if seconds >= 3600 then
        f"${(seconds / 3600)}%02d:${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"
      else f"${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"

    s"[${content.id}] ${content.title} | ${content.author} ($duration)"
