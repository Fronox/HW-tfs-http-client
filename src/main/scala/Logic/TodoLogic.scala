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
}
