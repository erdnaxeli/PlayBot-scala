package playbot.controllers.irc

import org.jibble.pircbot.Colors
import org.jibble.pircbot.PircBot
import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.TextBot
import playbot.domain.ports.TextMessage
import playbot.domain.ports.UrlContentFetcher

class IrcBot(ctx: ExecutionContext) extends PircBot with TextBot:
  setName(ctx.settings.irc_name)

  override def onMessage(
      channel: String,
      sender: String,
      login: String,
      hostname: String,
      msg: String
  ): Unit =
    processMessage(
      TextMessage(
        Channel(channel),
        User(sender),
        msg
      )
    )(using ctx).map(message =>
      sendMessage(message.channel.name, message.value)
    )

  def contentToString(content: Content): String =
    val duration =
      val seconds = content.duration.getSeconds()
      if seconds >= 3600 then
        f"${(seconds / 3600)}%02d:${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"
      else f"${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"

    val tags = content.tags.map(_.name).mkString("#", " #", "")
    s"${Colors.RED}[${content.id}] ${Colors.DARK_GREEN}${content.title} | ${content.author} ${Colors.BLUE}($duration) ${Colors.OLIVE}$tags"
