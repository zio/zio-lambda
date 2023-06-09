package zio.lambda.internal

import zio._
import zio.test.Assertion._
import zio.test._

import java.net.{ConnectException, ServerSocket}

object RuntimeApiLiveSpec extends ZIOSpecDefault {

  val serverSocket = new ServerSocket(8085)

  override def spec =
    suite("RuntimeApiLiveSpec spec")(
      test("sendInvocationResponse should not throw any error when unicode string is provided") {

        check(Gen.string(Gen.unicodeChar)) { unicodeString =>
          val env     = LambdaEnvironment("localhost:8085", "", "", 0, "", "", "", "")
          val resp    = InvocationResponse("id", unicodeString)
          val runtime = new RuntimeApiLive(env)
          for {
            _ <- ZIO.attempt {
                   serverSocket.accept().close()
                 }.fork
            res <- runtime.sendInvocationResponse(resp).retryWhile(_.isInstanceOf[ConnectException])
          } yield assert(res)(isUnit)
        }

      }
    )

}
