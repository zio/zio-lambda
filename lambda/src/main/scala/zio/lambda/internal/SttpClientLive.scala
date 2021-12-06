package zio.lambda.internal

import okhttp3.OkHttpClient
import sttp.client3._
import sttp.client3.okhttp.OkHttpSyncBackend
import zio._

import java.net.InetAddress
import java.net.Socket
import javax.net.SocketFactory

object SttpClientLive {
  val layer: ULayer[Has[SttpBackend[Identity, Any]]] =
    ZManaged
      .make(
        ZIO.succeed {
          new OkHttpClient.Builder()
            .socketFactory(new TcpNoDelaySocketFactory())
            .build()
        }
      )(okHttpClient => ZIO.succeed(okHttpClient.dispatcher().executorService().shutdown()))
      .use[Any, Nothing, SttpBackend[Identity, Any]](okHttpClient =>
        ZIO.succeed(OkHttpSyncBackend.usingClient(okHttpClient))
      )
      .toLayer

  class TcpNoDelaySocketFactory extends SocketFactory {
    private lazy val socketFactory = SocketFactory.getDefault()

    override def createSocket(): Socket =
      configureSocket(socketFactory.createSocket())

    override def createSocket(host: String, port: Int): Socket =
      configureSocket(socketFactory.createSocket(host, port))

    override def createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket =
      configureSocket(socketFactory.createSocket(host, port, localHost, localPort))

    override def createSocket(address: InetAddress, port: Int): Socket =
      configureSocket(socketFactory.createSocket(address, port))

    override def createSocket(address: InetAddress, port: Int, localHost: InetAddress, localPort: Int): Socket =
      configureSocket(socketFactory.createSocket(address, port, localHost, localPort))

    private def configureSocket(socket: Socket): Socket = {
      socket.setTcpNoDelay(true)
      socket
    }
  }
}
