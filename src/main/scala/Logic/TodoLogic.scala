package Logic

import Runner.Json4sSupport._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}
case class Todo(userId: Int, id: Int, title: String, completed: Boolean)
object Todo {
  def apply(id: Int, params: TodoParams): Todo = {
    Todo(params.userId.getOrElse(-1), id, params.title.getOrElse("empty title"), params.completed.getOrElse(false))
  }

  def apply(id: Int, params: TodoParams, default: Todo): Todo = {
    Todo(params.userId.getOrElse(default.userId), id, params.title.getOrElse(default.title),
      params.completed.getOrElse(default.completed))
  }
}
case class TodoParams(userId: Option[Int], title: Option[String], completed: Option[Boolean])

class TodoLogic(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext)
  extends AbstractLogic[Todo, TodoParams] {

  val uriStr: String = "https://jsonplaceholder.typicode.com/todos"

  override def getItemById(id: Int): Future[Todo] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/$id"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[Todo]
      }
  }

  override def getItemsList(): Future[List[Todo]] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(uriStr),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[List[Todo]]
      }
  }

  override def addNewItem(params: TodoParams): Future[Todo] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(s"$uriStr"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Todo])
  }

  override def updateItemAllFields(id: Int, params: TodoParams): Future[Todo] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Todo])
  }

  override def updateItemPartFields(id: Int, params: TodoParams): Future[Todo] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[Todo])
  }

  override def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map(resp => resp.status)
  }

  override def getItemsWithParams(params: Map[String, String]): Future[List[Todo]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[Todo]])
  }
}
