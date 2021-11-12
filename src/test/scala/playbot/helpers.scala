package playbot

import playbot.domain.entities.Content
import playbot.domain.entities.UrlContent
import java.time.Duration
import playbot.domain.entities.Site

def defaultContent = Content(
  author = "Ophidian",
  duration = Duration.ofSeconds(150),
  externalId = "djophidian/ophidian-love-is-digital",
  id = 42,
  site = Site.Soundcloud,
  title = "Love is Digital",
  url = "https://soundcloud.com/djophidian/ophidian-love-is-digital"
)

def defaultUrlContent = UrlContent(
  author = "Ophidian",
  duration = Duration.ofSeconds(150),
  externalId = "djophidian/ophidian-love-is-digital",
  site = Site.Soundcloud,
  title = "Love is Digital",
  url = "https://soundcloud.com/djophidian/ophidian-love-is-digital"
)
