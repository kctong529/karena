import java.time.LocalDateTime
import java.util.UUID

/** Documentation for reference:
 *   https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html
 *   https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#getBytes-- **/

case class EventId(value: UUID)

object EventId:
  def random(): EventId =
    EventId(UUID.randomUUID())

  def fromEventData(title: String, time: LocalDateTime): EventId =
    val rawString: String = s"$title $time"
    // println(s"Representative string: \"$rawString\"")
    val rawBytes: Array[Byte] = rawString.getBytes()
    EventId(UUID.nameUUIDFromBytes(rawBytes))
