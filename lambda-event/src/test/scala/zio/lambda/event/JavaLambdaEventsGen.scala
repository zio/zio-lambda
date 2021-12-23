package zio.lambda.event

import com.amazonaws.services.lambda.runtime.events.{KinesisEvent => JavaKinesisEvent}
import com.amazonaws.services.lambda.runtime.events.{ScheduledEvent => JavaScheduledEvent}
import com.amazonaws.services.lambda.runtime.events.{KafkaEvent => JavaKafkaEvent}
import com.amazonaws.services.lambda.runtime.events.{SQSEvent => JavaSQSEvent}
import com.amazonaws.services.lambda.runtime.events.models.kinesis.EncryptionType
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import zio.random.Random
import zio.test._

import java.nio.ByteBuffer
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.TimeZone
import scala.jdk.CollectionConverters._
import java.time.OffsetDateTime

object JavaLambdaEventsGen {

  private val genKinesisEventRecordUnit =
    for {
      kinesisSchemaVersion        <- Gen.anyString
      approximateArrivalTimestamp <- Gen.instant(Instant.now(), Instant.now().plus(365, ChronoUnit.DAYS))
      encryptionType <- Gen.oneOf(
                          Gen.const(EncryptionType.KMS.toString()),
                          Gen.const(EncryptionType.NONE.toString())
                        )
      partitionKey   <- Gen.anyString
      sequenceNumber <- Gen.anyString
      data           <- Gen.anyString
    } yield {
      val record = new JavaKinesisEvent.Record()
      record.setKinesisSchemaVersion(kinesisSchemaVersion)
      record.setApproximateArrivalTimestamp(Date.from(approximateArrivalTimestamp))
      record.setData(ByteBuffer.wrap(data.getBytes("UTF-8")))
      record.setEncryptionType(encryptionType)
      record.setPartitionKey(partitionKey)
      record.setSequenceNumber(sequenceNumber)
      record
    }

  private val genKinesisEventRecord =
    for {
      awsRegion         <- Gen.anyString
      eventID           <- Gen.anyString
      eventName         <- Gen.anyString
      eventSource       <- Gen.anyString
      eventSourceArn    <- Gen.anyString
      eventVersion      <- Gen.anyString
      invokeIdentityArn <- Gen.anyString
      kinesis           <- genKinesisEventRecordUnit
    } yield {
      val record = new JavaKinesisEvent.KinesisEventRecord()
      record.setAwsRegion(awsRegion)
      record.setEventID(eventID)
      record.setEventName(eventName)
      record.setEventSource(eventSource)
      record.setEventSourceARN(eventSourceArn)
      record.setEventVersion(eventVersion)
      record.setInvokeIdentityArn(invokeIdentityArn)
      record.setKinesis(kinesis)
      record
    }

  val genKinesisEvent: Gen[Random with Sized, JavaKinesisEvent] =
    Gen.listOf1(genKinesisEventRecord).map { records =>
      val kinesisEvent = new JavaKinesisEvent()
      kinesisEvent.setRecords(records.asJava)
      kinesisEvent
    }

  val genScheduledEvent: Gen[Random with Sized, JavaScheduledEvent] =
    for {
      account    <- Gen.anyString
      region     <- Gen.anyString
      detail     <- Gen.mapOf(Gen.anyString, Gen.anyString)
      source     <- Gen.anyString
      id         <- Gen.anyString
      time       <- Gen.offsetDateTime(OffsetDateTime.now(), OffsetDateTime.now().plusDays(365))
      resources  <- Gen.listOf(Gen.anyString)
      detailType <- Gen.anyString
    } yield {
      val scheduledEvent = new JavaScheduledEvent()
      scheduledEvent
        .withAccount(account)
        .withRegion(region)
        .withDetail(detail.asInstanceOf[Map[String, Object]].asJava)
        .withSource(source)
        .withId(id)
        .withTime(
          new DateTime(
            time.toInstant().toEpochMilli(),
            DateTimeZone.forTimeZone(TimeZone.getTimeZone(time.toZonedDateTime.getZone()))
          )
        )
        .withResources(resources.asJava)
        .withDetailType(detailType)

      scheduledEvent
    }

  private val genKafkaEventRecord =
    for {
      topic     <- Gen.anyString
      partition <- Gen.anyInt
      offset    <- Gen.anyLong
      timestamp <- Gen
                     .instant(
                       Instant.now(),
                       Instant
                         .now()
                         .plus(365, ChronoUnit.DAYS)
                     )
                     .map(_.toEpochMilli())
      timestampType <- Gen.anyString
      key           <- Gen.anyString
      value         <- Gen.anyString
    } yield JavaKafkaEvent.KafkaEventRecord
      .builder()
      .withTopic(topic)
      .withPartition(partition)
      .withOffset(offset)
      .withTimestamp(timestamp)
      .withTimestampType(timestampType)
      .withKey(key)
      .withValue(value)
      .build()

  val genKafkaEvent: Gen[Random with Sized, JavaKafkaEvent] =
    for {
      eventSource      <- Gen.anyString
      eventSourceArn   <- Gen.anyString
      bootstrapServers <- Gen.anyString
      records          <- Gen.mapOf(Gen.anyString, Gen.listOf(genKafkaEventRecord).map(_.asJava))
    } yield JavaKafkaEvent
      .builder()
      .withEventSource(eventSource)
      .withEventSourceArn(eventSourceArn)
      .withBootstrapServers(bootstrapServers)
      .withRecords(records.asJava)
      .build()

  private val genMessageAttribute =
    for {
      stringValue <- Gen.anyString
      dataType    <- Gen.oneOf(Gen.const("String"), Gen.const("Number"), Gen.const("Binary"))
    } yield {
      val messageAttribute = new JavaSQSEvent.MessageAttribute()
      messageAttribute.setDataType(dataType)
      messageAttribute.setStringValue(stringValue)
      messageAttribute
    }

  private val genSQSEventRecord =
    for {
      messageId              <- Gen.anyString
      receiptHandle          <- Gen.anyString
      body                   <- Gen.anyString
      md5OfBody              <- Gen.anyString
      md5OfMessageAttributes <- Gen.anyString
      eventSourceArn         <- Gen.anyString
      eventSource            <- Gen.anyString
      awsRegion              <- Gen.anyString
      attributes             <- Gen.mapOf(Gen.anyString, Gen.anyString)
      messageAttributes      <- Gen.mapOf(Gen.anyString, genMessageAttribute)
    } yield {
      val sqsMessage = new JavaSQSEvent.SQSMessage()
      sqsMessage.setMessageId(messageId)
      sqsMessage.setReceiptHandle(receiptHandle)
      sqsMessage.setBody(body)
      sqsMessage.setMd5OfBody(md5OfBody)
      sqsMessage.setMd5OfMessageAttributes(md5OfMessageAttributes)
      sqsMessage.setEventSourceArn(eventSourceArn)
      sqsMessage.setEventSource(eventSource)
      sqsMessage.setAwsRegion(awsRegion)
      sqsMessage.setAttributes(attributes.asJava)
      sqsMessage.setMessageAttributes(messageAttributes.asJava)
      sqsMessage
    }

  val genSQSEvent: Gen[Random with Sized, JavaSQSEvent] =
    Gen.listOf(genSQSEventRecord).map { records =>
      val sqsEvent = new JavaSQSEvent()
      sqsEvent.setRecords(records.asJava)
      sqsEvent
    }

}
