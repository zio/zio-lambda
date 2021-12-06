package zio.lambda.internal

import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers

import java.io.ByteArrayOutputStream
import scala.reflect.ClassTag

object JavaLambdaEventJsonEncoder {
  def toJson[T](value: T)(implicit tag: ClassTag[T]): String = {
    val pojoSerializer: PojoSerializer[T] =
      LambdaEventSerializers.serializerFor(
        tag.runtimeClass.asInstanceOf[Class[T]],
        this.getClass().getClassLoader()
      )
    val outputStream = new ByteArrayOutputStream(1024)
    pojoSerializer.toJson(value, outputStream)
    outputStream.toString()
  }
}
