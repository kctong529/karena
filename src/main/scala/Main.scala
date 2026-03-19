import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.Pane
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*

import java.time.LocalDateTime

object Main extends JFXApp3:

  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "UniqueProjectName"
      width = 600
      height = 450

    val root = Pane()

    val scene = Scene(parent = root)
    stage.scene = scene

    val rectangle = new Rectangle:
      x = 275
      y = 175
      width = 50
      height = 50
      fill = Blue

    root.children += rectangle

    /** Random UUID **/
    val eventId_1: EventId = EventId.random()
    println(s"Event ID: $eventId_1")

    /** Deterministic UUID **/
    // Define test data for generating the UUID
    val eventTitle_2: String = "OS2 Sprint 2 Meeting"
    val eventTime_2: LocalDateTime = LocalDateTime.of(2026, 3, 19, 14, 30)
    // Scala string interpolation:
    //   https://docs.scala-lang.org/scala3/book/string-interpolation.html
    val rawString_2: String = s"$eventTitle_2 $eventTime_2"
    println(s"Representative string: \"$rawString_2\"")

  end start

end Main
