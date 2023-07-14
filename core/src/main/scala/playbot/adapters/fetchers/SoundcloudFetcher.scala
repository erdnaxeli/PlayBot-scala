package playbot.adapters.fetchers

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import playbot.Executable
import playbot.ExecutionContext
import playbot.domain.entities.Site
import playbot.domain.entities.Url
import playbot.domain.entities.UrlContent
import sttp.client3.*

import java.time.Duration

case class SoundcloudTokenResponse(
    access_token: String
)

case class SoundcloudUser(username: String)

case class SoundcloudResponse(
    duration: Int,
    id: Int,
    permalink_url: String,
    title: String,
    user: SoundcloudUser
)

class SoundcloudFetcher():
  private var accessToken = ""
  private val httpBackend = HttpClientSyncBackend()

  def get(url: Url.Soundcloud): Executable[Option[UrlContent]] =
    val ctx = summon[ExecutionContext]

    if accessToken == "" then
      accessToken = getAccessToken(
        ctx.settings.soundcloud_client_id,
        ctx.settings.soundcloud_client_secret
      )

    val fullUrl = s"https://soundcloud.com/${url.externalId}"
    val apiUrl = basicRequest
      .followRedirects(false)
      .get(uri"https://api.soundcloud.com/resolve?url=$fullUrl")
      .header("Authorization", s"OAuth $accessToken")
      .response(asString)
      .send(httpBackend)
      .header("Location")
      .get
    val response = basicRequest
      .get(uri"$apiUrl")
      .header("Authorization", s"OAuth $accessToken")
      .response(asString.getRight)
      .send(httpBackend)
      .body

    implicit val UserReads = Json.reads[SoundcloudUser]
    implicit val ResponseReads = Json.reads[SoundcloudResponse]
    Json.parse(response).validate[SoundcloudResponse] match
      case JsSuccess(response, _) =>
        Some(
          UrlContent(
            author = response.user.username,
            duration =
              Duration.ofSeconds((0.5 + response.duration / 1000).toInt),
            externalId = response.id.toString,
            site = Site.Soundcloud,
            title = response.title,
            url = response.permalink_url
          )
        )
      case e: JsError =>
        throw Exception(s"Error while reading soundcloud json: $e")

  private def getAccessToken(clientId: String, clientSecret: String): String =
    val response = basicRequest
      .post(
        uri"https://api.soundcloud.com/oauth2/token?grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret"
      )
      .response(asString.getRight)
      .send(httpBackend)
      .body

    implicit val tokenResponseReads = Json.reads[SoundcloudTokenResponse]
    Json.parse(response).validate[SoundcloudTokenResponse] match
      case JsSuccess(token, _) => token.access_token
      case e: JsError          => throw Exception("Error while reading json")
