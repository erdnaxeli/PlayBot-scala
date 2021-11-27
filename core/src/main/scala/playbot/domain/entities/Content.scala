package playbot.domain.entities

import java.time.Duration

case class Content(
    author: String,
    duration: Duration,
    externalId: String,
    id: Int,
    site: Site,
    title: String,
    url: String,
    tags: List[Tag] = List[Tag]()
)

case class UrlContent(
    author: String,
    duration: Duration,
    externalId: String,
    site: Site,
    title: String,
    url: String
)
