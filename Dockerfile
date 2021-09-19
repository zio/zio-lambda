FROM hseeberger/scala-sbt:8u312_1.5.5_2.13.7

ADD . /zio-lambda

WORKDIR /zio-lambda

RUN apt-get -y update 

RUN apt-get -y install build-essential libz-dev zlib1g-dev

CMD sbt "zio-lambda-example / nativeImage"