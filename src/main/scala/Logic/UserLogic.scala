package Logic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import Runner.Json4sSupport._
import org.json4s.jackson.Serialization.write

import scala.concurrent.{ExecutionContext, Future}

case class Geo(lat: String, lng: String)

case class Address(street: String, suite: String, city: String, zipcode: String, geo: Geo)

case class Company(name: String, catchPhrase: String, bs: String)

case class User(id: Int, name: String, username: String, email: String, address: Address, phone: String, website: String, company: Company)

object User {
  def apply(id: Int, params: UserParams): User = {
    User(id, params.name.getOrElse(""), params.username.getOrElse(""), params.email.getOrElse(""),
      params.address.getOrElse(Address("", "", "", "", Geo("", ""))), params.phone.getOrElse(""),
      params.website.getOrElse(""), params.company.getOrElse(Company("", "", "")))
  }

  def apply(id: Int, params: UserParams, default: User): User = {
    User(id, params.name.getOrElse(default.name), params.username.getOrElse(default.username),
      params.email.getOrElse(default.email),
      params.address.getOrElse(default.address), params.phone.getOrElse(default.phone),
      params.website.getOrElse(default.website), params.company.getOrElse(default.company))
  }
}

case class UserParams(name: Option[String], username: Option[String], email: Option[String], address: Option[Address],
                      phone: Option[String], website: Option[String], company: Option[Company])

class UserLogic(implicit ac: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext)
  extends AbstractLogic[User, UserParams] {
  val uriStr: String = "https://jsonplaceholder.typicode.com/users"

  override def getItemById(id: Int): Future[User] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(s"$uriStr/$id"),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[User]
      }
  }

  override def getItemsList(): Future[List[User]] = {
    Http()
      .singleRequest(HttpRequest(
        uri = Uri(uriStr),
        method = HttpMethods.GET
      ))
      .flatMap{
        x => Unmarshal(x).to[List[User]]
      }
  }

  override def addNewItem(params: UserParams): Future[User] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(uriStr),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[User])
  }

  override def updateItemAllFields(id: Int, params: UserParams): Future[User] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PUT,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[User])
  }

  override def updateItemPartFields(id: Int, params: UserParams): Future[User] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.PATCH,
        uri = Uri(s"$uriStr/$id"),
        entity = HttpEntity(ContentTypes.`application/json`, write(params))),
      ).flatMap(Unmarshal(_).to[User])
  }

  override def removeItemById(id: Int): Future[StatusCode] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.DELETE,
        uri = Uri(s"$uriStr/$id"),
      )).map(resp => resp.status)
  }

  override def getItemsWithParams(params: Map[String, String]): Future[List[User]] = {
    val queryParams = params.toList.map(x => s"${x._1}=${x._2}").mkString("&")
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr?$queryParams")
      )).flatMap(Unmarshal(_).to[List[User]])
  }

  def getAlbumsFromUser1: Future[List[Album]] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr/1/albums")
      )).flatMap(Unmarshal(_).to[List[Album]])
  }

  def getTodosFromUser1: Future[List[Todo]] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr/1/todos")
      )).flatMap(Unmarshal(_).to[List[Todo]])
  }

  def getPostsFromUser1: Future[List[Post]] = {
    Http()
      .singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = Uri(s"$uriStr/1/posts")
      )).flatMap(Unmarshal(_).to[List[Post]])
  }

}

