package playbot

import com.typesafe.config.ConfigFactory

import java.io.File
import scala.jdk.CollectionConverters.ListHasAsScala

case class Settings(
    irc_channels: List[String],
    irc_debug: Boolean,
    irc_host: String,
    irc_name: String,
    youtube_api_key: String
)

object Settings:
  def apply(): Settings =
    val config = ConfigFactory.parseFile(File("playbot.conf"))
    config.checkValid(ConfigFactory.defaultReference(), "playbot")

    Settings(
      irc_channels = config.getStringList("bot.channels").asScala.toList,
      irc_debug = config.getBoolean("bot.debug"),
      irc_host = config.getString("bot.host"),
      irc_name = config.getString("bot.name"),
      youtube_api_key = config.getString("youtube.api_key")
    )
