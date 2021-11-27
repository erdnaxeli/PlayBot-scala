package playbot.domain.usecases

import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Content

trait GetContent:
  def perform(id: Int): Executable[Option[Content]]

class GetContentImpl extends GetContent:
  def perform(id: Int): Executable[Option[Content]] =
    val ctx = summon[ExecutionContext]
    ctx.contentRepository.getById(id)
