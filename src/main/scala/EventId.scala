import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.UUID

/** Documentation for reference:
 *   https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html
 *   https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#getBytes--
 *   https://docs.oracle.com/javase/8/docs/api/java/nio/charset/StandardCharsets.html **/

case class EventId(value: UUID)

object EventId:
  def random(): EventId =
    EventId(UUID.randomUUID())

  def deterministic(title: String, createdAt: LocalDateTime): EventId =
    /** Encode with UTF-8 explicitly to make UUID generation deterministic across environments.
     *  Using getBytes() without a charset would rely on the platform default encoding. **/
    val rawString: String = s"$title $createdAt"
    // println(s"Representative string: \"$rawString\"")
    val rawBytes: Array[Byte] = rawString.getBytes(StandardCharsets.UTF_8)
    EventId(UUID.nameUUIDFromBytes(rawBytes))
