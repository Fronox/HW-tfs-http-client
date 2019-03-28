import Logic.{Photo, PhotoParams, Post, PostParams}
import Runner.Json4sSupport
import Runner.Main.{albums, comments, photos}
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{FlatSpecLike, Matchers}
import org.scalatest.concurrent.ScalaFutures
import util.HttpClientTest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class PhotoLogicTest extends TestKit(ActorSystem()) with HttpClientTest
  with Json4sSupport with Matchers with ScalaFutures with FlatSpecLike {

  behavior of "getItemById"
  it should "get photo by with id = 1" in {
    val photo1 = Photo(1, 1, "accusamus beatae ad facilis cum similique qui sunt", "https://via.placeholder.com/600/92c952",
      "https://via.placeholder.com/150/92c952")

    photos.getItemById(1).futureValue shouldBe photo1
  }

  behavior of "addNewItem"
  it should "add new photo" in {
    val photoParams = PhotoParams(Some(1), Some("title"), Some("https://via.placeholder.com/600/92c952"),
      Some("https://via.placeholder.com/150/92c952"))
    val newPhoto = Photo(5001, photoParams)

    photos.addNewItem(photoParams).futureValue shouldBe newPhoto
  }

  behavior of "updateItemAllFields"
  it should "update photo with particular id" in {
    val id = 1
    val photoParams = PhotoParams(Some(1), Some("title"), Some("https://via.placeholder.com/600/92c952"),
      Some("https://via.placeholder.com/150/92c952"))
    val newPhoto = Photo(id, photoParams)

    photos.updateItemAllFields(id, photoParams).futureValue shouldBe newPhoto
  }

  behavior of "updateItemPartFields"
  it should "partially update photo with particular id" in {
    val id = 1
    val origPhoto = Await.result(photos.getItemById(id), Duration.Inf)
    val photoParams = PhotoParams(Some(1), None, None,
      Some("https://via.placeholder.com/150/92c952"))
    val newPhoto = Photo(id, photoParams, origPhoto)

    photos.updateItemPartFields(id, photoParams).futureValue shouldBe newPhoto
  }

  behavior of "removeItemById"
  it should "delete photo with id = 1" in {
    photos.removeItemById(1).futureValue.intValue() shouldBe 200
  }

  behavior of "getItemsWithParams"
  it should "get all photos with specified params" in {
    val count = 50
    photos.getItemsWithParams(Map("albumId" -> "1")).map(photoList => photoList.size)
      .futureValue shouldBe count
  }
}
