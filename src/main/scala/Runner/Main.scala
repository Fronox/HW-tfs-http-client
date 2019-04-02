package Runner

import Logic._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await

object Main {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val albums = new AlbumLogic()
  val comments = new CommentLogic()
  val photos = new PhotoLogic()
  val posts = new PostLogic()
  val todos = new TodoLogic()
  val users = new UserLogic()

  def main(args: Array[String]): Unit = {
    /*val album1 = Await.result(albums.getItemById(1), Duration.Inf)
    println(album1)

    val comment1 = Await.result(comments.getItemById(1), Duration.Inf)
    println(comment1)

    val photo1 = Await.result(photos.getItemById(1), Duration.Inf)
    println(photo1)

    val post1 = Await.result(posts.getItemById(1), Duration.Inf)
    println(post1)

    val todo1 = Await.result(todos.getItemById(1), Duration.Inf)
    println(todo1)

    val user1 = Await.result(users.getItemById(1), Duration.Inf)
    println(user1)

    val albumList = Await.result(albums.getItemsList(), Duration.Inf)
    println(albumList.size)

    val commentList = Await.result(comments.getItemsList(), Duration.Inf)
    println(commentList.size)

    val photoList = Await.result(photos.getItemsList(), Duration.Inf)
    println(photoList.size)

    val postList = Await.result(posts.getItemsList(), Duration.Inf)
    println(postList.size)

    val todoList = Await.result(todos.getItemsList(), Duration.Inf)
    println(todoList.size)

    val userList = Await.result(users.getItemsList(), Duration.Inf)
    println(userList.size)*/
    val res = Await.result(albums.getItemsWithParams(Map("postId" -> "1")), Duration.Inf)
    println(res)
    actorSystem.terminate()
  }
}
