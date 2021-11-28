package playbot.controllers.http

import com.sun.net.httpserver.HttpServer
import playbot.Executable

import java.net.InetSocketAddress

object RpcServer:
  def run(): Executable[Unit] =
    val server = HttpServer.create(new InetSocketAddress(12345), 10)
    server.createContext("/rpc", new RpcHandler())
    server.setExecutor(null)

    server.start()

    println("Hit any key to exit...")
    System.in.read()
    server.stop(3)
