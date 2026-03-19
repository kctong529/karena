import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.*

import java.time.LocalDateTime
import java.util.UUID

class EventIdTest extends AnyFlatSpec {

  "Same title and creation time" should "produce the same EventId" in {
    val title = "OS2 Sprint 2 Meeting"
    val createdAt = LocalDateTime.of(2026, 3, 19, 14, 30)

    val id1 = EventId.deterministic(title, createdAt)
    val id2 = EventId.deterministic(title, createdAt)

    assert(id1 == id2)
  }

  "Different title" should "produce a different EventId" in {
    val createdAt = LocalDateTime.of(2026, 3, 19, 14, 30)

    val id1 = EventId.deterministic("OS2 Sprint 2 Meeting", createdAt)
    val id2 = EventId.deterministic("OS2 Sprint 3 Meeting", createdAt)

    assert(id1 != id2)
  }

  "Different creation time" should "produce a different EventId" in {
    val title = "OS2 Sprint 2 Meeting"
    val createdAt1 = LocalDateTime.of(2026, 3, 19, 14, 30)
    val createdAt2 = LocalDateTime.of(2026, 3, 19, 16, 30)

    val id1 = EventId.deterministic(title, createdAt1)
    val id2 = EventId.deterministic(title, createdAt2)

    assert(id1 != id2)
  }

  "Known title and creation time" should "produce the expected UUID" in {
    val title = "OS2 Sprint 2 Meeting"
    val createdAt = LocalDateTime.of(2026, 3, 19, 14, 30)

    val expected = EventId(UUID.fromString("75ff0a2f-17e2-383b-9531-9d64f4add6b2"))
    val actual = EventId.deterministic(title, createdAt)

    assert(actual == expected)
  }

  "EventId generation" must "not throw exceptions for titles with unusual Unicode characters" in {
    val createdAt = LocalDateTime.of(2026, 3, 19, 14, 30)

    // Some unusual but valid Unicode titles to check that ID generation does not crash
    val titles = Seq(
      "Sprint \"review\" and 'retro'",
      "Sprint “review” and ‘retro’",
      "Team sync 🙂 🚀",
      "Tapaaminen äöå",
      "Проект встреча",
      "こんにちは会議",
      "会议安排",
      "회의 일정"
    )

    // https://www.scalatest.org/user_guide/using_matchers#expectedExceptions
    titles.foreach { title =>
      noException should be thrownBy {
        EventId.deterministic(title, createdAt)
      }
    }
  }
}
