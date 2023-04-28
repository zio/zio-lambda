package zio.lambda.internal

import zio._
import zio.test.Assertion._
import zio.test._

import java.net.{ConnectException, ServerSocket}

object RuntimeApiLiveSpec extends ZIOSpecDefault {

  override def spec =
    suite("RuntimeApiLiveSpec spec")(
      test("sendInvocationResponse should not throw any error when UTF-8 characters are provided") {

        val env     = LambdaEnvironment("localhost:8085", "", "", 0, "", "", "", "")
        val resp    = InvocationResponse("id", "payload with UTF-8 characters: 'тест'")
        val runtime = new RuntimeApiLive(env)
        for {
          _ <- ZIO.attempt {
                 val serverSocket = new ServerSocket(8085)
                 val clientSocket = serverSocket.accept()
                 clientSocket.close()
               }.fork
          res <- runtime.sendInvocationResponse(resp).retryWhile(_.isInstanceOf[ConnectException])
        } yield assert(res)(isUnit)

      }
    )

}
