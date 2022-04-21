package zio.lambda.internal

import zio._
import zio.json._
import zio.lambda.ZLambda
import zio.lambda.Context

final case class CustomPayload(value: String)
object CustomPayload {
  implicit val decoder: JsonDecoder[CustomPayload] = DeriveJsonDecoder.gen[CustomPayload]
  implicit val encoder: JsonEncoder[CustomPayload] = DeriveJsonEncoder.gen[CustomPayload]
}

final case class CustomResponse(value: String)
object CustomResponse {
  implicit val encoder: JsonEncoder[CustomResponse] = DeriveJsonEncoder.gen[CustomResponse]
}

object SuccessZLambda extends ZLambda[CustomPayload, CustomResponse] {
  override def apply(event: CustomPayload, context: Context): Task[CustomResponse] =
    ZIO.succeed(CustomResponse(event.value))
}

object ErrorZLambda extends ZLambda[CustomPayload, CustomResponse] {
  override def apply(event: CustomPayload, context: Context): Task[CustomResponse] =
    ZIO.fail(new Throwable("ZLambda error"))
}
