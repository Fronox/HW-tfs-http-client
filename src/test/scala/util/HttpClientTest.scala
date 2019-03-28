package util

import java.util.concurrent.ForkJoinPool

import org.scalatest.FlatSpecLike
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object HttpClientExecutionContext {
  private val forkJoinPool = new ForkJoinPool(4)
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(forkJoinPool)
}

trait HttpClientTest extends FlatSpecLike with ScalaFutures with ImplicitLogger {
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(1.minute)

  val timeout: FiniteDuration = 1.second
  val iteration = 30

  implicit val ec: ExecutionContextExecutor = HttpClientExecutionContext.ec
}
