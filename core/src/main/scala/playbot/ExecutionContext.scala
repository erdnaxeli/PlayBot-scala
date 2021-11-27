package playbot

import playbot.Settings
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.TextBot
import playbot.domain.ports.UrlContentFetcher

case class ExecutionContext(
    contentRepository: playbot.domain.ports.ContentRepository,
    settings: Settings,
    urlContentFetcher: UrlContentFetcher
)

type Executable[T] = ExecutionContext ?=> T
