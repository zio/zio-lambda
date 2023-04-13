package zio.lambda

import zio.ZIO
import zio.json.{JsonDecoder, JsonEncoder}
import zio.json._

class ZLambdaApp[R, E:JsonDecoder, A:JsonEncoder](userFunction:(E,Context) => ZIO[R,Throwable,A]) {
  def applyJson(json: String, context: Context): ZIO[R,Throwable,String] =
    JsonDecoder[E].decodeJson(json) match {
      case Left(errorMessage) =>
        ZIO.fail(new Throwable(s"Error decoding json. Json=$json, Error$errorMessage"))
      case Right(event) =>
        userFunction(event, context).map(_.toJson)
    }

}

object ZLambdaApp {
  def apply[R,I:JsonDecoder, O:JsonEncoder](fn:(I,Context)=>ZIO[R,Throwable,O]):ZLambdaApp[R,I,O] =
    new ZLambdaApp(fn)

}
