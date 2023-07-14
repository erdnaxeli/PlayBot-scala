package playbot.adapters.fetchers

import playbot.Executable
import playbot.Settings
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.ports.UrlContentFetcher

class UrlContentFetcherDispatcher extends UrlContentFetcher:
  private val youtube = YoutubeFetcher()
  private val soundcloud = SoundcloudFetcher()
  override def get(url: Url): Executable[Option[UrlContent]] =
    url match
      case r: Url.Soundcloud => soundcloud.get(r)
      case r: Url.Youtube    => youtube.get(r)
