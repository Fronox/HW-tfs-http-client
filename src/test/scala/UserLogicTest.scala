import Logic._
import Runner.Json4sSupport
import Runner.Main.{todos, users}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import util.HttpClientTest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UserLogicTest extends TestKit(ActorSystem()) with HttpClientTest
  with Json4sSupport with Matchers with ScalaFutures with FlatSpecLike {
  behavior of "UserLogic"
  it should "get post by with id = 1" in {
    val geo1 = Geo("-37.3159", "81.1496")
    val address1 = Address("Kulas Light", "Apt. 556", "Gwenborough", "92998-3874", geo1)
    val company1 = Company("Romaguera-Crona", "Multi-layered client-server neural-net", "harness real-time e-markets")
    val user1 = User(1, "Leanne Graham", "Bret", "Sincere@april.biz", address1, "1-770-736-8031 x56442", "hildegard.org", company1)

    users.getItemById(1).futureValue shouldBe user1
  }

  behavior of "addNewItem"
  it should "add new user" in {
    val userParams = UserParams(Some("name"), Some("uname"), Some("email"), Some(Address("1", "2", "3", "4", Geo("5", "6"))),
      Some("phone"), Some("site"), Some(Company("", "", "")))
    val newUser = User(11, userParams)

    users.addNewItem(userParams).futureValue shouldBe newUser
  }

  behavior of "updateItemAllFields"
  it should "update user with particular id" in {
    val id = 1
    val userParams = UserParams(Some("name"), Some("uname"), Some("email"), Some(Address("1", "2", "3", "4", Geo("5", "6"))),
      Some("phone"), Some("site"), Some(Company("", "", "")))
    val newUser = User(id, userParams)

    users.updateItemAllFields(id, userParams).futureValue shouldBe newUser
  }

  behavior of "updateItemPartFields"
  it should "partially update user with particular id" in {
    val id = 1
    val origUser = Await.result(users.getItemById(id), Duration.Inf)
    val userParams = UserParams(Some("name"), None, Some("email"),
      None,
      Some("phone"), Some("site"), Some(Company("", "", "")))
    val newUser = User(id, userParams, origUser)

    users.updateItemPartFields(id, userParams).futureValue shouldBe newUser
  }

  behavior of "removeItemById"
  it should "delete user with id = 1" in {
    users.removeItemById(1).futureValue.intValue() shouldBe 200
  }

  behavior of "getItemsWithParams"
  it should "get all users with specified params" in {
    val count = 1
    users.getItemsWithParams(Map("id" -> "1")).map(userList => userList.size)
      .futureValue shouldBe count
  }

  behavior of "getAlbumsFromUser1"
  it should "get all albums from user with id = 1" in {
    val count = 100
    users.getAlbumsFromUser1.map(albumList => albumList.size)
      .futureValue shouldBe count
  }

  behavior of "getTodosFromUser1"
  it should "get all todos from user with id = 1" in {
    val count = 200
    users.getTodosFromUser1.map(todoList => todoList.size)
      .futureValue shouldBe count
  }

  behavior of "getPostsFromUser1"
  it should "get all posts from user with id = 1" in {
    val count = 100
    users.getPostsFromUser1.map(postList => postList.size)
      .futureValue shouldBe count
  }
}