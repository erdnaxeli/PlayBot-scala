package playbot

import com.typesafe.config.ConfigFactory

import java.io.File
import scala.jdk.CollectionConverters.ListHasAsScala

case class Settings(
    irc_channels: List[String],
    irc_debug: Boolean,
    irc_host: String,
    irc_name: String,
    db_name: String,
    db_host: String,
    db_password: String,
    db_user: String,
    http_key: String,
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
      db_name = config.getString("db.name"),
      db_host = config.getString("db.host"),
      db_password = config.getString("db.password"),
      db_user = config.getString("db.user"),
      http_key = config.getString("http.key"),
      youtube_api_key = config.getString("youtube.api_key")
    )
