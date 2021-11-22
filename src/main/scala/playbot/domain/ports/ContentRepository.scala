package playbot.domain.ports

import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.Tag
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import playbot.domain.entities.User

import scala.util.Try

trait ContentRepository:
  def addTags(contentId: Int, tags: List[Tag]): Try[Unit]
  def save(content: UrlContent, user: User, channel: Channel): Try[Content]
  def getById(id: Int): Option[Content]
  def search(query: String): Option[Content]
