import playbot.ExecutionContext
import playbot.Settings
import playbot.adapters.ContentRepositoryMsql
import playbot.adapters.fetchers.UrlContentFetcherDispatcher
import playbot.controllers.http.RpcServer

@main def playbot_http: Unit =
  val settings = Settings()
  given ExecutionContext(
    contentRepository = ContentRepositoryMsql(settings),
    settings = settings,
    urlContentFetcher = UrlContentFetcherDispatcher()
  )

  RpcServer.run()
