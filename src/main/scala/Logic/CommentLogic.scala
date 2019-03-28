package Logic

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import Runner.Json4sSupport._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}

case class Comment(postId: Int, id: Int, name: String, email: String, body: String)
object Comment {
  def apply(id: Int, params: CommentParams): Comment = {
    Comment(params.postId.getOrElse(-1), id, params.name.getOrElse("unnamed"), params.email.getOrElse("no email"),
      params.body.getOrElse("empty body"))
  }

  def apply(id: Int, params: CommentParams, default: Comment): Comment = {
    Comment(params.postId.getOrElse(default.postId), id, params.name.getOrElse(default.name),
      params.email.getOrElse(default.email), params.body.getOrElse(default.body))
  }
}

case class CommentParams(postId: Option[Int], name: Option[String], email: Option[String], body: Option[String])

class CommentLogic(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext)
  extends AbstractLogic[Comment, CommentParams] {
  val uriStr: String = "https://jsonplaceholder.typicode.com/comments"

  override def getItemById(id: Int): Future[Comment] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/$id"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[Comment]
      }
  }

  override def getItemsList(): Future[List[Comment]] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[List[Comment]]
      }
  }

  override def addNewItem(params: CommentParams): Future[Comment] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(uriStr),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Comment])
  }

  override def updateItemAllFields(id: Int, params: CommentParams): Future[Comment] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Comment])
  }

  override def updateItemPartFields(id: Int, params: CommentParams): Future[Comment] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Comment])
  }

  override def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map(resp => resp.status)
  }

  override def getItemsWithParams(params: Map[String, String]): Future[List[Comment]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[Comment]])
  }
}
