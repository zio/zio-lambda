package zio.lambda

import zio._
import zio.json._

final case class CustomPayload(value: String)
object CustomPayload {
  implicit val decoder: JsonDecoder[CustomPayload] = DeriveJsonDecoder.gen[CustomPayload]
  implicit val encoder: JsonEncoder[CustomPayload] = DeriveJsonEncoder.gen[CustomPayload]
}

final case class CustomResponse(value: String)
object CustomResponse {
  implicit val encoder: JsonEncoder[CustomResponse] = DeriveJsonEncoder.gen[CustomResponse]
}

object TestZLambda {

  object Success extends ZLambda[CustomPayload, CustomResponse] {
    override def handle(event: CustomPayload): RIO[ZEnv, CustomResponse] =
      ZIO.succeed(CustomResponse(event.value))
  }

  object Error extends ZLambda[CustomPayload, CustomResponse] {
    override def handle(event: CustomPayload): RIO[ZEnv, CustomResponse] =
      ZIO.fail(new Throwable("ZLambda error"))
  }

}
