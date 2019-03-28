import Logic._
import Runner.Json4sSupport
import Runner.Main.{photos, posts}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpecLike, Matchers}
import util.HttpClientTest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PostLogicTest extends TestKit(ActorSystem()) with HttpClientTest
  with Json4sSupport with Matchers with ScalaFutures with FlatSpecLike {
  behavior of "getItemById"
  it should "get post by with id = 1" in {
    val post = Post(1, 1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
      "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
    )
    posts.getItemById(1).futureValue shouldBe post
  }

  behavior of "addNewItem"
  it should "add new post" in {
    val postParams = PostParams(Some(1), Some("title"), Some("body"))
    val newPost = Post(101, postParams)

    posts.addNewItem(postParams).futureValue shouldBe newPost
  }

  behavior of "updateItemAllFields"
  it should "update post with particular id" in {
    val id = 1
    val postParams = PostParams(Some(1), Some("title"), Some("body"))
    val newPost = Post(id, postParams)

    posts.updateItemAllFields(id, postParams).futureValue shouldBe newPost
  }

  behavior of "updateItemPartFields"
  it should "partially update post with particular id" in {
    val id = 1
    val origPost = Await.result(posts.getItemById(id), Duration.Inf)
    val postParams = PostParams(Some(1), None, Some("body"))
    val newPost = Post(id, postParams, origPost)

    posts.updateItemPartFields(id, postParams).futureValue shouldBe newPost
  }

  behavior of "removeItemById"
  it should "delete post with id = 1" in {
    posts.removeItemById(1).futureValue.intValue() shouldBe 200
  }

  behavior of "getItemsWithParams"
  it should "get all photos with specified params" in {
    val count = 10
    posts.getItemsWithParams(Map("userId" -> "1")).map(postList => postList.size)
      .futureValue shouldBe count
  }

  behavior of "getCommentsFromPost1"
  it should "get all comments from post with id = 1" in {
    val count = 500
    posts.getCommentsFromPost1.map(photoList => photoList.size)
      .futureValue shouldBe count
  }
}
