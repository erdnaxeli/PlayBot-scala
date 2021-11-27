package playbot.domain.entities

import playbot.UnitSpec

class UrlSpec extends UnitSpec:
  behavior of "A Url"

  it should "match soundcloud" in {
    Url(
      "https://soundcloud.com/kwamebelair/la-darude-2-years-megamix"
    ).value shouldBe Url.Soundcloud("kwamebelair/la-darude-2-years-megamix")
  }

  it should "return None if it can't match" in {
    Url("https://perdu.com") shouldBe None
  }
