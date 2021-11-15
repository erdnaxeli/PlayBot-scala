package playbot.domain.ports

import playbot.Executable
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent

trait UrlContentFetcher:
  def get(url: Url): Executable[Option[UrlContent]]
