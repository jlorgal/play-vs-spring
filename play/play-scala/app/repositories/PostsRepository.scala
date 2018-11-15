package repositories

import javax.inject.Inject

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

// Reactive Mongo imports
import reactivemongo.bson.BSONObjectID
// BSON-JSON conversions/collection
import reactivemongo.play.json._, collection._
// import reactivemongo.core.commands.LastError

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
  MongoController,
  ReactiveMongoApi,
  ReactiveMongoComponents
}
import scala.concurrent.Future

class PostsRepository @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) {

    def collection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection[JSONCollection]("posts"))

    def create(post: JsObject): Future[JsObject] = {
      val postWithId = post + ("_id" -> Json.obj("$oid" -> BSONObjectID.generate().stringify))
      collection.flatMap(_.insert(postWithId)).map(_ => postWithId)
    }

    def get(id: String): Future[Option[JsObject]] = {
      val selector = Json.obj("_id" -> Json.obj("$oid" -> id))
      collection.flatMap(_.find(selector, Option.empty[JsObject]).one[JsObject])
    }

}
