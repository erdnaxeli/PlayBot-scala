package playbot.domain.usecases

import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Tag

import scala.util.Failure
import scala.util.Success

trait AddTags:
  def perform(contentId: Int, tags: List[Tag]): Executable[Unit]

class AddTagsImpl extends AddTags:
  def perform(contentId: Int, tags: List[Tag]): Executable[Unit] =
    val ctx = summon[ExecutionContext]

    ctx.contentRepository.addTags(contentId, tags) match
      case Success(_) => ()
      case Failure(e) => println(s"Error while saving tags: $e")
