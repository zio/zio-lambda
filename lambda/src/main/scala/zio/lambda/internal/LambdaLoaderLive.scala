package zio.lambda.internal

import zio._
import zio.lambda.ZLambdaApp

final case class LambdaLoaderLive(
  customClassLoader: CustomClassLoader,
  environment: LambdaEnvironment
) extends LambdaLoader {

  override lazy val loadLambda: UIO[Either[Throwable, ZLambdaApp[_, _]]] =
    customClassLoader.getClassLoader
      .flatMap(classLoader =>
        ZIO
          .fromOption[String](environment.handler)
          .mapError[Throwable](_ => new Throwable("Function Handler not defined"))
          .flatMap[Any, Throwable, ZLambdaApp[_, _]](handler =>
            ZIO
              .effect(
                Class
                  .forName(
                    handler + "$",
                    true,
                    classLoader
                  )
                  .getDeclaredField("MODULE$")
                  .get(null)
                  .asInstanceOf[ZLambdaApp[_, _]]
              )
              .refineOrDie { case ex: ClassNotFoundException => ex }
          )
      )
      .either

}

object LambdaLoaderLive {
  val layer: ZLayer[Has[CustomClassLoader] with Has[LambdaEnvironment], Throwable, Has[LambdaLoader]] =
    (LambdaLoaderLive(_, _)).toLayer
}
