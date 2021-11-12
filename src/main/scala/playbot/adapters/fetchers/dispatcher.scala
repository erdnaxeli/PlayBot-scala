package playbot.adapters.fetchers

import playbot.Settings
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.ports.UrlContentFetcher

given UrlContentFetcher with
  override def get(url: Url)(using Settings): Option[UrlContent] =
    val youtube = YoutubeFetcher()
    url match
      case r: Url.Soundcloud => ???
      case r: Url.Youtube    => youtube.get(r)
