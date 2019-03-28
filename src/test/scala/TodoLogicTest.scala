import Logic.{Post, PostParams, Todo, TodoParams}
import Runner.Json4sSupport
import Runner.Main.{photos, todos}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import util.HttpClientTest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TodoLogicTest  extends TestKit(ActorSystem()) with HttpClientTest
  with Json4sSupport with Matchers with ScalaFutures with FlatSpecLike {

  behavior of "getItemById"
  it should "get post by with id = 1" in {
    val todo1 = Todo(1, 1, "delectus aut autem", completed = false)

    todos.getItemById(1).futureValue shouldBe todo1
  }

  behavior of "addNewItem"
  it should "add new todo" in {
    val todoParams = TodoParams(Some(1), Some("title"), Some(false))
    val newTodo = Todo(201, todoParams)

    todos.addNewItem(todoParams).futureValue shouldBe newTodo
  }

  behavior of "updateItemAllFields"
  it should "update todo with particular id" in {
    val id = 1
    val todoParams = TodoParams(Some(1), Some("title"), Some(false))
    val newTodo = Todo(id, todoParams)

    todos.updateItemAllFields(id, todoParams).futureValue shouldBe newTodo
  }

  behavior of "updateItemPartFields"
  it should "partially update todo with particular id" in {
    val id = 1
    val origTodo = Await.result(todos.getItemById(id), Duration.Inf)
    val todoParams = TodoParams(Some(1), None, Some(false))
    val newTodo = Todo(id, todoParams, origTodo)

    todos.updateItemPartFields(id, todoParams).futureValue shouldBe newTodo
  }

  behavior of "removeItemById"
  it should "delete todo with id = 1" in {
    todos.removeItemById(1).futureValue.intValue() shouldBe 200
  }

  behavior of "getItemsWithParams"
  it should "get all todos with specified params" in {
    val count = 20
    todos.getItemsWithParams(Map("userId" -> "1")).map(todoList => todoList.size)
      .futureValue shouldBe count
  }
}
