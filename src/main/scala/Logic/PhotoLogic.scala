package Logic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import Runner.Json4sSupport._
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}

case class Photo(albumId: Int, id: Int, title: String, url: String, thumbnailUrl: String)
object Photo {
  def apply(id: Int, params: PhotoParams): Photo = {
    Photo(params.albumId.getOrElse(-1), id, params.title.getOrElse("empty title"), params.url.getOrElse("no url"),
      params.thumbnailUrl.getOrElse("no thumbnailUrl"))
  }

  def apply(id: Int, params: PhotoParams, default: Photo): Photo = {
    Photo(params.albumId.getOrElse(default.albumId), id, params.title.getOrElse(default.title),
      params.url.getOrElse(default.url), params.thumbnailUrl.getOrElse(default.thumbnailUrl))
  }
}
case class PhotoParams(albumId: Option[Int], title: Option[String], url: Option[String], thumbnailUrl: Option[String])

class PhotoLogic(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext)
  extends AbstractLogic[Photo, PhotoParams] {
  val uriStr: String = "https://jsonplaceholder.typicode.com/photos"
}
