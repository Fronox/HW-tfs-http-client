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

  override def getItemById(id: Int): Future[Photo] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/$id"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[Photo]
      }
  }

  override def getItemsList(): Future[List[Photo]] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[List[Photo]]
      }
  }

  override def addNewItem(params: PhotoParams): Future[Photo] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(uriStr),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Photo])
  }

  override def updateItemAllFields(id: Int, params: PhotoParams): Future[Photo] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Photo])
  }

  override def updateItemPartFields(id: Int, params: PhotoParams): Future[Photo] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Photo])
  }

  override def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map(x => x.status)
  }

  override def getItemsWithParams(params: Map[String, String]): Future[List[Photo]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[Photo]])
  }
}
