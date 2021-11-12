package playbot.domain.ports

import playbot.Settings
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent

trait UrlContentFetcher:
  def get(url: Url)(using Settings): Option[UrlContent]
