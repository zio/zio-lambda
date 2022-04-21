package zio.lambda.event

import com.amazonaws.services.lambda.runtime.events.models.kinesis.EncryptionType
import com.amazonaws.services.lambda.runtime.events.{KafkaEvent => JavaKafkaEvent}
import com.amazonaws.services.lambda.runtime.events.{KinesisEvent => JavaKinesisEvent}
import com.amazonaws.services.lambda.runtime.events.{SQSEvent => JavaSQSEvent}
import com.amazonaws.services.lambda.runtime.events.{ScheduledEvent => JavaScheduledEvent}
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import zio.test._

import java.nio.ByteBuffer
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.TimeZone
import scala.jdk.CollectionConverters._

object JavaLambdaEventsGen {

  private val genKinesisEventRecordUnit =
    for {
      kinesisSchemaVersion        <- Gen.string
      approximateArrivalTimestamp <- Gen.instant(Instant.now(), Instant.now().plus(365, ChronoUnit.DAYS))
      encryptionType <- Gen.oneOf(
                          Gen.const(EncryptionType.KMS.toString()),
                          Gen.const(EncryptionType.NONE.toString())
                        )
      partitionKey   <- Gen.string
      sequenceNumber <- Gen.string
      data           <- Gen.string
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
      awsRegion         <- Gen.string
      eventID           <- Gen.string
      eventName         <- Gen.string
      eventSource       <- Gen.string
      eventSourceArn    <- Gen.string
      eventVersion      <- Gen.string
      invokeIdentityArn <- Gen.string
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

  val genKinesisEvent: Gen[Sized, JavaKinesisEvent] =
    Gen.listOf1(genKinesisEventRecord).map { records =>
      val kinesisEvent = new JavaKinesisEvent()
      kinesisEvent.setRecords(records.asJava)
      kinesisEvent
    }

  val genScheduledEvent: Gen[Sized, JavaScheduledEvent] =
    for {
      account    <- Gen.string
      region     <- Gen.string
      detail     <- Gen.mapOf(Gen.string, Gen.string)
      source     <- Gen.string
      id         <- Gen.string
      time       <- Gen.offsetDateTime(OffsetDateTime.now(), OffsetDateTime.now().plusDays(365))
      resources  <- Gen.listOf(Gen.string)
      detailType <- Gen.string
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
      topic     <- Gen.string
      partition <- Gen.int
      offset    <- Gen.long
      timestamp <- Gen
                     .instant(
                       Instant.now(),
                       Instant
                         .now()
                         .plus(365, ChronoUnit.DAYS)
                     )
                     .map(_.toEpochMilli())
      timestampType <- Gen.string
      key           <- Gen.string
      value         <- Gen.string
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

  val genKafkaEvent: Gen[Sized, JavaKafkaEvent] =
    for {
      eventSource      <- Gen.string
      eventSourceArn   <- Gen.string
      bootstrapServers <- Gen.string
      records          <- Gen.mapOf(Gen.string, Gen.listOf(genKafkaEventRecord).map(_.asJava))
    } yield JavaKafkaEvent
      .builder()
      .withEventSource(eventSource)
      .withEventSourceArn(eventSourceArn)
      .withBootstrapServers(bootstrapServers)
      .withRecords(records.asJava)
      .build()

  private val genMessageAttribute =
    for {
      stringValue <- Gen.string
      dataType    <- Gen.oneOf(Gen.const("String"), Gen.const("Number"), Gen.const("Binary"))
    } yield {
      val messageAttribute = new JavaSQSEvent.MessageAttribute()
      messageAttribute.setDataType(dataType)
      messageAttribute.setStringValue(stringValue)
      messageAttribute
    }

  private val genSQSEventRecord =
    for {
      messageId              <- Gen.string
      receiptHandle          <- Gen.string
      body                   <- Gen.string
      md5OfBody              <- Gen.string
      md5OfMessageAttributes <- Gen.string
      eventSourceArn         <- Gen.string
      eventSource            <- Gen.string
      awsRegion              <- Gen.string
      attributes             <- Gen.mapOf(Gen.string, Gen.string)
      messageAttributes      <- Gen.mapOf(Gen.string, genMessageAttribute)
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

  val genSQSEvent: Gen[Sized, JavaSQSEvent] =
    Gen.listOf(genSQSEventRecord).map { records =>
      val sqsEvent = new JavaSQSEvent()
      sqsEvent.setRecords(records.asJava)
      sqsEvent
    }

}
