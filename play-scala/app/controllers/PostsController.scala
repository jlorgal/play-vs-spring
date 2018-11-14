package controllers

import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Logger
import play.api.mvc.{ AbstractController, ControllerComponents }
import play.api.libs.functional.syntax._
import play.api.libs.json._

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


class PostsController @Inject()(components: ControllerComponents, val reactiveMongoApi: ReactiveMongoApi)
  extends AbstractController(components) with MongoController with ReactiveMongoComponents {

    def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("posts"))

    implicit def ec: ExecutionContext = components.executionContext

    def create() = Action.async {
      val json = Json.obj(
        "title" -> "play-scala",
        "body" -> "test with play scala"
      )
      collection.flatMap(_.insert(json))
        .map(lastError => Ok("Mongo LastError: %s".format(lastError)))
    }

    def get(id: BSONObjectID) = Action.async {
      val selector = Json.obj("_id" -> id)
      collection.flatMap(_.find(selector).one[JsObject])
        .map(post => Ok(Json.toJson(post)))
    }

}



// class PostsController @Inject()(postsRepository: PostsRepository) extends Controller {

//   private val logger = Logger(getClass)

//   def create() = Action { request =>
//     logger.trace(s"create")
//     postsRepository.save(BSONDocument(
//       "title" -> "play-scala",
//       "body" -> "test with play scala"
//     )).map(lastError => Ok("Mongo LastError: %s".format(lastError)))
//   }

//   def get(id: String) = Action.async { request =>
//     logger.trace(s"show: id = $id")
//     postsRepository.get(BSONDocument("_id" -> BSONObjectID(id))).map(widget => Ok(Json.toJson(widget)))
//   }
// }
