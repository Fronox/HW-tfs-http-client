package Logic

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import Runner.Json4sSupport._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}

case class Post(userId: Int, id: Int, title: String, body: String)
object Post {
  def apply(id: Int, params: PostParams): Post = {
    Post(params.userId.getOrElse(-1), id, params.title.getOrElse("empty title"), params.body.getOrElse("empty body"))
  }

  def apply(id: Int, params: PostParams, default: Post): Post = {
    Post(params.userId.getOrElse(default.userId), id, params.title.getOrElse(default.title),
      params.body.getOrElse(default.body))
  }
}
case class PostParams(userId: Option[Int], title: Option[String], body: Option[String])

class PostLogic(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext)
  extends AbstractLogic[Post, PostParams]{

  val uriStr: String = "https://jsonplaceholder.typicode.com/posts"

  override def getItemById(id: Int): Future[Post] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/${id.toString}"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[Post]
      }
  }

  override def getItemsList(): Future[List[Post]] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(uriStr),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[List[Post]]
      }
  }

  override def addNewItem(params: PostParams): Future[Post] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(s"$uriStr"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Post])
  }

  override def updateItemAllFields(id: Int, params: PostParams): Future[Post] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Post])
  }

  override def updateItemPartFields(id: Int, params: PostParams): Future[Post] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Post])
  }

  override def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map(resp => resp.status)
  }

  override def getItemsWithParams(params: Map[String, String]): Future[List[Post]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[Post]])
  }

  def getCommentsFromPost1: Future[List[Comment]] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr/1/comments")
      )).flatMap(Unmarshal(_).to[List[Comment]])
  }
}
