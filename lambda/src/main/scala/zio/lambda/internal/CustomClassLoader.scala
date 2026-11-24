package zio.lambda.internal

import zio._
import zio.stream.ZStream

import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths

/** A service that builds the class loader used to load the handler. */
trait CustomClassLoader {

  /** Builds the class loader. */
  def getClassLoader: Task[ClassLoader]
}

/** Constructors and accessors for [[CustomClassLoader]]. */
object CustomClassLoader {

  /**
   * A [[CustomClassLoader]] whose class loader has one class-path entry for each file and folder directly inside
   * `LAMBDA_TASK_ROOT`.
   */
  val live: URLayer[LambdaEnvironment, CustomClassLoader] = ZLayer.fromFunction { (environment: LambdaEnvironment) =>
    new CustomClassLoader {
      override def getClassLoader: Task[ClassLoader] =
        ZStream
          .fromJavaStream(Files.list(Paths.get(environment.taskRoot)))
          .runCollect
          .map(stream =>
            new URLClassLoader(
              stream
                .map(_.toUri().toURL())
                .toArray
            )
          )
    }
  }

  /** Builds the class loader with the [[CustomClassLoader]] service of the environment. */
  def getClassLoader: ZIO[CustomClassLoader, Throwable, ClassLoader] =
    ZIO.serviceWithZIO(_.getClassLoader)
}
