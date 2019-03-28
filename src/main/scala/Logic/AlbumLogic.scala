package Logic
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import Runner.Json4sSupport._
import org.json4s.jackson.Serialization.write

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

  override def getItemById(id: Int): Future[Album] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/${id.toString}"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[Album]
      }
  }

  def getItemsList(): Future[List[Album]] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[List[Album]]
      }
  }

  override def addNewItem(params: AlbumParams): Future[Album] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(uriStr),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Album])
  }

  override def updateItemAllFields(id: Int, params: AlbumParams): Future[Album] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Album])
  }

  override def updateItemPartFields(id: Int, params: AlbumParams): Future[Album] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Album])
  }

  override def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map{resp => resp.status}
  }

  override def getItemsWithParams(params: Map[String, String]): Future[List[Album]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    println(s"$uriStr?$queryParams")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[Album]])
  }

  def getPhotosFromAlbum1: Future[List[Photo]] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr/1/photos")
      )).flatMap(Unmarshal(_).to[List[Photo]])
  }
}
