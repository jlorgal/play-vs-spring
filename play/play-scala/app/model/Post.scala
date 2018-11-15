package model

import play.api.libs.json._

case class Post(title: String, body: String)

object Post {
  implicit val format = Json.format[Post]
}
