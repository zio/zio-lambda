package zio.lambda.internal

import zio._
import zio.json._
import zio.lambda.ZLambdaApp

final case class CustomPayload(value: String)
object CustomPayload {
  implicit val decoder: JsonDecoder[CustomPayload] = DeriveJsonDecoder.gen[CustomPayload]
  implicit val encoder: JsonEncoder[CustomPayload] = DeriveJsonEncoder.gen[CustomPayload]
}

final case class CustomResponse(value: String)
object CustomResponse {
  implicit val encoder: JsonEncoder[CustomResponse] = DeriveJsonEncoder.gen[CustomResponse]
}

object SuccessZLambda extends ZLambdaApp[CustomPayload, CustomResponse] {
  override def apply(event: CustomPayload): RIO[ZEnv, CustomResponse] =
    ZIO.succeed(CustomResponse(event.value))
}

object ErrorZLambda extends ZLambdaApp[CustomPayload, CustomResponse] {
  override def apply(event: CustomPayload): RIO[ZEnv, CustomResponse] =
    ZIO.fail(new Throwable("ZLambda error"))
}
