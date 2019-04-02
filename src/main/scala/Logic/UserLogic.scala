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

