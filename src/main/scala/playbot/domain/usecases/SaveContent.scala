package playbot.domain.usecases

import playbot.Settings
import playbot.domain.entities.Url
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher

class SaveContent[A <: Url](url: A)(using
    repo: ContentRepository,
    urlFetcher: UrlContentFetcher
)(using Settings):
  def perform(): Unit =
    urlFetcher.get(url) match
      case Some(content) => repo.save(content)
      case None          => ()
