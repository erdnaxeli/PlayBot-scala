package playbot.domain.ports

import playbot.domain.entities.Content
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent

trait ContentRepository:
  def save(content: UrlContent): Unit
  def getById(id: Int): Option[Content]
  def search(query: String): Option[Content]
