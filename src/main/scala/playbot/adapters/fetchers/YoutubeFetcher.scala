package playbot.adapters.fetchers

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import playbot.Executable
import playbot.Settings
import playbot.ExecutionContext
import playbot.domain.entities.Site
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent

import java.time.Duration

class YoutubeFetcher:
  private val youtube = YouTube
    .Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance(),
      null
    )
    .setApplicationName("PlayBot")
    .build()

  def get(url: Url.Youtube): Executable[Option[UrlContent]] =
    val ctx = summon[ExecutionContext]
    val response = youtube
      .videos()
      .list("snippet,contentDetails")
      .setKey(ctx.settings.youtube_api_key)
      .setId(url.externalId)
      .execute()

    if response.getItems.isEmpty() then None
    else
      val video = response.getItems().get(0)
      Some(
        UrlContent(
          author = video.getSnippet().getChannelTitle(),
          duration = Duration.parse(video.getContentDetails().getDuration()),
          externalId = url.externalId,
          site = Site.Youtube,
          title = video.getSnippet().getTitle(),
          url = s"https://www.youtube.com/watch?v=${url.externalId}"
        )
      )
