package playbot.controllers.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.jibble.pircbot.Colors
import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Channel
import playbot.domain.entities.Content
import playbot.domain.entities.User
import playbot.domain.ports.TextBot
import playbot.domain.ports.TextMessage

import java.io.IOException
import java.net.InetSocketAddress
import scala.io.Source

object RpcServer:
  def run(): Executable[Unit] =
    val server = HttpServer.create(new InetSocketAddress(8000), 10)
    server.createContext("/rpc", new RpcHandler())
    server.setExecutor(null)

    server.start()

    println("Hit any key to exit...")
    System.in.read()
    server.stop(3)

class RpcHandler()(using ctx: ExecutionContext) extends HttpHandler:
  private val textBot = new TextBot {
    def contentToString(content: Content): String =
      val duration =
        val seconds = content.duration.getSeconds()
        if seconds >= 3600 then
          f"${(seconds / 3600)}%02d:${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"
        else f"${(seconds % 3600) / 60}:${(seconds % 3600) % 60}"

      val tags = content.tags.map(_.name).mkString("#", " #", "")
      s"${Colors.RED}[${content.id}] ${Colors.DARK_GREEN}${content.title} | ${content.author} ${Colors.BLUE}($duration) ${Colors.OLIVE}$tags"
  }

  def handle(t: HttpExchange): Unit =
    if t.getRequestMethod == "POST" then
      try
        val body = Source.fromInputStream(t.getRequestBody).mkString
        val channel :: user :: value :: Nil = body.split('\n').toList

        textBot
          .processMessage(
            TextMessage(
              Channel(channel),
              User(user),
              value
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
