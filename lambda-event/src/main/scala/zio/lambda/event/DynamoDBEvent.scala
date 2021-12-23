package zio.lambda.event

final case class DynamoDBEvent(
// records: List[Dynamodb.DynamodbStreamRecord]
)

object DynamoDBEvent {
//   implicit val codec: JsonValueCodec[Dynamodb] = JsonCodecMaker.make
//   final case class DynamodbStreamRecord(
//     eventName: String,
//     eventVersion: String,
//     eventSource: String,
//     awsRegion: String,
//     eventSourceARN: String,
//     dynamodb: StreamRecord,
//     userIdentity: Identity
//   )
//   object DynamodbStreamRecord {
//     implicit val codec: JsonValueCodec[DynamodbStreamRecord] = JsonCodecMaker.make
//   }
//   final case class StreamRecord(
//     approximateCreationDateTime: java.time.Instant,
//     keys: Map[String, AttributeValue],
//     newImage: Map[String, AttributeValue],
//     oldImage: Map[String, AttributeValue],
//     sequenceNumber: String,
//     sizeBytes: Long,
//     streamViewType: String
//   )
//   object StreamRecord {
//     implicit val codec: JsonValueCodec[StreamRecord] = JsonCodecMaker.make
//   }

//   final case class Identity(principalId: String, `type`: String)
//   object Identity {
//     implicit val codec: JsonValueCodec[Identity] = JsonCodecMaker.make
//   }
//   final case class AttributeValue(
//     n: String,
//     b: String, // Revisit this as it was defined as java.nio.ByteBuffer,
//     sS: List[String],
//     nS: List[String],
//     bS: List[String], // Revisit this as it was defined as List[java.nio.ByteBuffer]
//     m: Map[String, AttributeValue],
//     l: List[AttributeValue],
//     nULLValue: Boolean,
//     bOOL: Boolean
//   )
//   object AttributeValue {
//     implicit val codec: JsonValueCodec[AttributeValue] = JsonCodecMaker.make
//   }
// }

// final case class DynamodbTimeWindow(records: Dynamodb.DynamodbStreamRecord)
// object DynamodbTimeWindow {
//   implicit val codec: JsonValueCodec[DynamodbTimeWindow] = JsonCodecMaker.make
// }
}
