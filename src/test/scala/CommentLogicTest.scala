import Logic.{Album, AlbumParams, Comment, CommentParams}
import Runner.Json4sSupport
import Runner.Main.{albums, comments}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import util.HttpClientTest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CommentLogicTest extends TestKit(ActorSystem()) with HttpClientTest
  with Json4sSupport with Matchers with ScalaFutures with FlatSpecLike {

  behavior of "getItemById"
  it should "get comment by with id = 1" in {
    val comment1 = Comment(1, 1, "id labore ex et quam laborum", "Eliseo@gardner.biz",
      "laudantium enim quasi est quidem magnam voluptate ipsam eos\ntempora quo necessitatibus\ndolor quam autem quasi\nreiciendis et nam sapiente accusantium")

    comments.getItemById(1).futureValue shouldBe comment1
  }

  behavior of "addNewItem"
  it should "add new comment" in {
    val commentParams = CommentParams(Some(1), Some("new comment"), Some("email@domen.com"), Some("hello!"))
    val newComment = Comment(501, commentParams)

    comments.addNewItem(commentParams).futureValue shouldBe newComment
  }

  behavior of "updateItemAllFields"
  it should "update comment with particular id" in {
    val id = 1
    val commentParams = CommentParams(Some(1), Some("new comment"), Some("email@domen.com"), Some("hello!"))
    val newComment = Comment(id, commentParams)

    comments.updateItemAllFields(id, commentParams).futureValue shouldBe newComment
  }

  behavior of "updateItemPartFields"
  it should "partially update comment with particular id" in {
    val id = 1
    val origComment = Await.result(comments.getItemById(id), Duration.Inf)
    val commentParams = CommentParams(Some(1), Some("new comment"), Some("email@domen.com"), Some("hello!"))
    val newComment = Comment(id, commentParams, origComment)

    comments.updateItemAllFields(id, commentParams).futureValue shouldBe newComment
  }

  behavior of "removeItemById"
  it should "delete comment with id = 1" in {
    comments.removeItemById(1).futureValue.intValue() shouldBe 200
  }

  behavior of "getItemsWithParams"
  it should "get all comments with specified params" in {
    val count = 5
    comments.getItemsWithParams(Map("postId" -> "1")).map(commentList => commentList.size)
      .futureValue shouldBe count
  }
}
