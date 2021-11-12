package playbot.controllers.irc

import org.jibble.pircbot.PircBot
import playbot.Settings
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Url
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher
import playbot.domain.usecases.SaveContent

import java.net.{URL => UrlParser}
import scala.util.Try

class IrcBot(name: String)(using
    r: ContentRepository,
    f: UrlContentFetcher
)(using Settings)
    extends PircBot:
  setName(name)

  override def onMessage(
      channel: String,
      sender: String,
      login: String,
      hostname: String,
      msg: String
  ): Unit =
    extractUrl(msg)
      .flatMap(Url(_))
      .flatMap(
        SaveContent(_, User(sender), Channel(channel)).perform()
      )
      .map(sendContent(_, channel))

  private def extractUrl(msg: String): Option[String] =
    msg
      .split(raw"\s+")
      .iterator
      .map(p => Try(UrlParser(p)).map(_.toString).toOption)
      .flatten
      .nextOption

  private def sendContent(content: Content, channel: String): Unit =
    val msg =
      val duration =
        val seconds = content.duration.getSeconds()
        if seconds >= 3600 then
          f"${(seconds / 3600)}%02d:${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"
        else f"${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"

      s"[${content.id}] ${content.title} | ${content.author} ($duration)"
    sendMessage(channel, msg)
