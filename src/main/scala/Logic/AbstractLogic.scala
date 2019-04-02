package Logic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, _}
import akka.http.scaladsl.unmarshalling.{FromMessageUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import org.json4s.jackson.Serialization.write
import spray.json.DefaultJsonProtocol
import Runner.Json4sSupport._

import scala.concurrent.{ExecutionContext, Future}


abstract class AbstractLogic[T, TParams <: AnyRef](implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext,
                                         u: FromMessageUnmarshaller[T], ul: FromMessageUnmarshaller[List[T]])
  /*extends DefaultJsonProtocol with SprayJsonSupport*/{
  val uriStr: String

  //implicit val clientJsonFormat: RootJsonFormat[T] = jsonFormat5()

  //implicit val um: Unmarshaller[HttpResponse, List[T]]

  def getItemsList(): Future[List[T]] = {
    Http()
      .singleRequest(
        HttpRequest(
          method = HttpMethods.GET,
          uri = Uri(uriStr)
        )
      ).flatMap(Unmarshal(_).to[List[T]])
  }
  def getItemById(id: Int): Future[T] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/${id.toString}"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[T]
      }
  }
  def getItemsWithParams(params: Map[String, String]): Future[List[T]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    println(s"$uriStr?$queryParams")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[T]])
  }
  def addNewItem(params: TParams): Future[T] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(uriStr),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[T])
  }
  def updateItemAllFields(id: Int, params: TParams): Future[T] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[T])
  }
  def updateItemPartFields(id: Int, params: TParams): Future[T] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[T])
  }
  def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map{resp => resp.status}
  }
}
