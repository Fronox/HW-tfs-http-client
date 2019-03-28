import Logic.{Album, AlbumParams}
import Runner.Json4sSupport
import Runner.Main.albums
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpecLike, Matchers}
import util.HttpClientTest

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class AlbumLogicTest extends TestKit(ActorSystem()) with HttpClientTest
  with Json4sSupport with Matchers with ScalaFutures with FlatSpecLike {

  behavior of "getItemById"
  it should "get album by with id = 1" in {
    val album1 = Album(1, 1, "quidem molestiae enim")

    albums.getItemById(1).futureValue shouldBe album1
  }

  behavior of "addNewItem"
  it should "add new album" in {
    val albumParams = AlbumParams(Some(1), Some("hello!"))
    val newAlbum = Album(101, albumParams)

    albums.addNewItem(albumParams).futureValue shouldBe newAlbum
  }

  behavior of "updateItemAllFields"
  it should "update album with particular id" in {
    val id = 1
    val albumParams = AlbumParams(Some(1), Some("hello!"))
    val newAlbum = Album(id, albumParams)

    albums.updateItemAllFields(id, albumParams).futureValue shouldBe newAlbum
  }

  behavior of "updateItemPartFields"
  it should "partially update album with particular id" in {
    val id = 1
    val origAlbum = Await.result(albums.getItemById(id), Duration.Inf)
    val albumParams = AlbumParams(None, Some("hello!"))
    val newAlbum = Album(id, albumParams, origAlbum)

    albums.updateItemPartFields(id, albumParams).futureValue shouldBe newAlbum
  }

  behavior of "removeItemById"
  it should "delete album with id = 1" in {
    albums.removeItemById(1).futureValue.intValue() shouldBe 200
  }

  behavior of "getItemsWithParams"
  it should "get all albums with specified params" in {
    val count = 10
    albums.getItemsWithParams(Map("userId" -> "1")).map(albumList => albumList.size)
      .futureValue shouldBe count
  }

  behavior of "getPhotosFromAlbum1"
  it should "get all photos from album with id = 1" in {
    val count = 5000
    albums.getPhotosFromAlbum1.map(photoList => photoList.size)
      .futureValue shouldBe count
  }
}
