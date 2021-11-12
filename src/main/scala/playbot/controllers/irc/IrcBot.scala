package playbot.controllers.irc

import org.jibble.pircbot.PircBot
import playbot.Settings
import playbot.domain.entities.Url
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
    extractUrl(msg).flatMap(Url(_)).map(SaveContent(_).perform())

  private def extractUrl(msg: String): Option[String] =
    msg
      .split(raw"\s+")
      .iterator
      .map(p => Try(UrlParser(p)).map(_.toString).toOption)
      .flatten
      .nextOption
