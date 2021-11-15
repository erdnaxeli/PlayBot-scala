package playbot.controllers.irc

import org.jibble.pircbot.PircBot
import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Channel
import playbot.domain.entities.User
import playbot.domain.ports.ContentRepository
import playbot.domain.ports.TextBot
import playbot.domain.ports.TextMessage
import playbot.domain.ports.UrlContentFetcher

class IrcBot(ctx: ExecutionContext) extends PircBot:
  private val bot = TextBot()
  export bot.processMessage

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
