import org.scalatest.flatspec.AnyFlatSpec
import java.time.LocalDateTime
import java.util.UUID

class EventIdTest extends AnyFlatSpec {

  "Same event data" must "produce the same EventId" in {
    val title = "OS2 Sprint 2 Meeting"
    val time = LocalDateTime.of(2026, 3, 19, 14, 30)

    val id1 = EventId.fromEventData(title, time)
    val id2 = EventId.fromEventData(title, time)

    assert(id1 == id2)
  }

  "Different event data" should "produce a different EventId" in {
    val time = LocalDateTime.of(2026, 3, 19, 14, 30)

    val id1 = EventId.fromEventData("OS2 Sprint 2 Meeting", time)
    val id2 = EventId.fromEventData("OS2 Sprint 3 Meeting", time)

    assert(id1 != id2)
  }

  "Known event data" should "produce the expected UUID" in {
    val title = "OS2 Sprint 2 Meeting"
    val time = LocalDateTime.of(2026, 3, 19, 14, 30)

    val expected = EventId(UUID.fromString("75ff0a2f-17e2-383b-9531-9d64f4add6b2"))
    val actual = EventId.fromEventData(title, time)

    assert(actual == expected)
  }

}
