package playbot.adapters.fetchers

import playbot.Executable
import playbot.Settings
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.ports.UrlContentFetcher

class UrlContentFetcherDispatcher extends UrlContentFetcher:
  override def get(url: Url): Executable[Option[UrlContent]] =
    val youtube = YoutubeFetcher()
    url match
      case r: Url.Soundcloud => ???
      case r: Url.Youtube    => youtube.get(r)
