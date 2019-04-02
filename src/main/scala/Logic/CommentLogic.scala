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
}
