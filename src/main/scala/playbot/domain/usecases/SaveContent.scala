package playbot.domain.usecases

import playbot.Settings
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Url
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher

import scala.util.Failure
import scala.util.Success
import scala.util.Try

class SaveContent(url: Url, user: User, channel: Channel)(using
    repo: ContentRepository,
    urlFetcher: UrlContentFetcher
)(using Settings):
  def perform(): Option[Content] =
    urlFetcher.get(url) match
      case Some(content) =>
        repo.save(content, user, channel) match
          case Success(v) => Some(v)
          case Failure(e) =>
            println(e)
            None
      case None => None
