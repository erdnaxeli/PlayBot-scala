package playbot.domain.entities

enum Url(externalId: String):
  case Soundcloud(externalId: String) extends Url(externalId)
  case Youtube(externalId: String) extends Url(externalId)

object Url:
  def apply(url: String): Option[Url] =
    val soundcloud =
      raw"(?:^|[^!])https?://(?:www\.)?soundcloud.com/([a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+)(?:\?.+)?".r
    val youtube =
      raw"(?:^|[^!])https?://(?:(?:www|music).youtube.com/watch\?[a-zA-Z0-9_=&-]*v=|youtu.be/)([a-zA-Z0-9_-]+)".r

    url match
      case soundcloud(externalId) => Some(Soundcloud(externalId))
      case youtube(externalId)    => Some(Youtube(externalId))
      case _                      => None
