package playbot.controllers.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import org.jibble.pircbot.Colors
import playbot.ExecutionContext
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.User
import playbot.domain.ports.TextBot
import playbot.domain.ports.TextMessage
import upickle.default.read

import scala.io.Source

class RpcHandler()(using ctx: ExecutionContext) extends HttpHandler:
  private val textBot = new TextBot {
    def contentToString(content: Content): String =
      val duration =
        val seconds = content.duration.getSeconds()
        if seconds >= 3600 then
          f"${(seconds / 3600)}%02d:${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"
        else f"${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"

      val tags = content.tags.map(t => s"#${t.name}").mkString(" ")
      s"${Colors.RED}[${content.id}] ${Colors.DARK_GREEN}${content.title} | ${content.author} ${Colors.BLUE}($duration) ${Colors.OLIVE}$tags"
  }

  def handle(t: HttpExchange): Unit =
    if t.getRequestMethod == "POST" then
      try
        val rpcMessage = read[RpcMessage](t.getRequestBody)

        textBot
          .processMessage(
            TextMessage(
              Channel(rpcMessage.channel),
              User(rpcMessage.user),
              rpcMessage.value
            )
          )
          .map(msg => sendResponse(msg.value, t))
      catch
        case e: Exception =>
          e.printStackTrace
          t.sendResponseHeaders(500, 0)
    else t.sendResponseHeaders(400, 0)

    t.getResponseBody.close

  private def sendResponse(msg: String, t: HttpExchange): Unit =
    t.sendResponseHeaders(200, msg.length)
    val rb = t.getResponseBody
    rb.write(msg.getBytes)
    rb.close
