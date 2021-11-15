import playbot.ExecutionContext
import playbot.Settings
import playbot.adapters.ContentRepositoryMsql
import playbot.adapters.fetchers.UrlContentFetcherDispatcher
import playbot.controllers.irc.IrcBot
import playbot.domain.entities.UrlContent
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher

@main def playbot_irc: Unit =
  val settings = Settings()
  val ctx: ExecutionContext = ExecutionContext(
    contentRepository = ContentRepositoryMsql(settings),
    settings = settings,
    urlContentFetcher = UrlContentFetcherDispatcher()
  )

  val bot = IrcBot(ctx)
  bot.setVerbose(ctx.settings.irc_debug)
  bot.connect(ctx.settings.irc_host)
  ctx.settings.irc_channels.map(bot.joinChannel(_))
