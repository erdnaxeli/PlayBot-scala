package playbot.controllers.http

import upickle.default
import upickle.default.ReadWriter
import upickle.default.macroRW

case class RpcMessage(
    channel: String,
    user: String,
    value: String
)

object RpcMessage:
  given rw: ReadWriter[RpcMessage] = macroRW
