import playbot.Settings
import playbot.controllers.irc.IrcBot
import playbot.domain.entities.UrlContent
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher
import playbot.adapters.fetchers.{given UrlContentFetcher}
import playbot.adapters.contentRepositoryMysql

import java.time.Duration

@main def playbot_main: Unit =
  given settings: Settings = Settings()

  val bot = IrcBot(settings.irc_name)
  bot.setVerbose(settings.irc_debug)
  bot.connect(settings.irc_host)
  settings.irc_channels.map(bot.joinChannel(_))
