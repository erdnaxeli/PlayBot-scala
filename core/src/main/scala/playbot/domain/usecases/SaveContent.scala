package playbot.domain.usecases

import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Url
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.UrlContentFetcher

import scala.util.Failure
import scala.util.Success
import scala.util.Try

trait SaveContent(url: Url, user: User, channel: Channel):
  def perform(): Executable[Option[Content]]

class SaveContentImpl(url: Url, user: User, channel: Channel)
    extends SaveContent(url, user, channel):
  def perform(): Executable[Option[Content]] =
    val ctx = summon[ExecutionContext]
    ctx.urlContentFetcher.get(url) match
      case Some(content) =>
        ctx.contentRepository.save(content, user, channel) match
          case Success(v) => Some(v)
          case Failure(e) =>
            e.printStackTrace()
            None
      case None => None
