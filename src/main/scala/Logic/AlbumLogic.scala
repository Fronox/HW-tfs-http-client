package Logic
import Runner.Json4sSupport._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

case class Album(userId: Int, id: Int, title: String)
object Album {
  def apply(id: Int, params: AlbumParams): Album = {
    Album(params.userId.getOrElse(-1), id, params.title.getOrElse("empty title"))
  }

  def apply(id: Int, params: AlbumParams, default: Album): Album = {
    Album(params.userId.getOrElse(default.userId), id, params.title.getOrElse(default.title))
  }
}

case class AlbumParams(userId: Option[Int], title: Option[String])

class AlbumLogic(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext)
  extends AbstractLogic[Album, AlbumParams] {

  val uriStr: String = "https://jsonplaceholder.typicode.com/albums"

  def getPhotosFromAlbum1: Future[List[Photo]] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr/1/photos")
      )).flatMap(Unmarshal(_).to[List[Photo]])
  }
}
