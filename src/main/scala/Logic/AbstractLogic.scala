package Logic

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCode
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

abstract class AbstractLogic[T, TParams](implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext) {
  val uriStr: String

  def getItemsList(): Future[List[T]]
  def getItemById(id: Int): Future[T]
  def getItemsWithParams(params: Map[String, String]): Future[List[T]]
  def addNewItem(params: TParams): Future[T]
  def updateItemAllFields(id: Int, params: TParams): Future[T]
  def updateItemPartFields(id: Int, params: TParams): Future[T]
  def removeItemById(id: Int): Future[StatusCode]
  /*def putItem()*/
}
