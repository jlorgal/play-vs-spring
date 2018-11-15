package controllers

import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Logger
import play.api.mvc._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._

// Reactive Mongo imports
import reactivemongo.api.{ Cursor, ReadPreference }
import reactivemongo.bson.BSONObjectID
// BSON-JSON conversions/collection
import reactivemongo.play.json._, collection._

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}

import repositories._
import model._

class PostsController @Inject()(components: ControllerComponents, postsRepository: PostsRepository)
  extends AbstractController(components) {

    implicit def ec: ExecutionContext = components.executionContext

    def create() = Action.async(parse.json) { implicit request =>
      request.body match {
        case body: JsValue => body match {
          case post: JsObject => postsRepository.create(post).map(createdPost => Ok(createdPost))
          case _ => Future.successful(BadRequest("Invalid request"))
        }
        case _ => Future.successful(BadRequest("Invalid request"))
      }
    }

    def get(id: String) = Action.async {
      postsRepository.get(id)
        .map(post => Ok(Json.toJson(post)))
    }

}
