import playbot.Settings
import playbot.controllers.irc.IrcBot
import playbot.domain.entities.Site
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher
import playbot.adapters.fetchers.{given UrlContentFetcher}

import java.time.Duration

@main def hello: Unit =
  given settings: Settings = Settings()

  given ContentRepository with
    def getById(id: Int) = ???
    def save(content: UrlContent): Unit = println(content)
    def search(query: String) = ???

  val bot = IrcBot(settings.irc_name)
  bot.setVerbose(settings.irc_debug)
  bot.connect(settings.irc_host)
  settings.irc_channels.map(bot.joinChannel(_))
