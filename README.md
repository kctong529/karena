# Karena

[![Scala](https://img.shields.io/badge/Scala-3.8.2-red)](https://www.scala-lang.org/)
![JDK](https://img.shields.io/badge/JDK-21-blue)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Karena is a small personal project to recreate the design and architecture of a calendar application in Scala.

The goal of this project is not to build a production-ready calendar, but to independently replicate the design process that students go through in the course project I mentor. By building the same kind of system myself, I can better understand the architectural decisions, challenges, and implementation trade-offs that students encounter.

This repository serves as an experimental reference implementation of a calendar-style application written with ScalaFX.

## Development Principles

This project intentionally follows a strict rule:

> [!IMPORTANT]
> The implementation is written **without consulting AI tools**.

The only exception is **documentation**, where AI assistance may be used to help produce clear explanations and project descriptions.

The goal is to keep the design and coding process as close as possible to the experience of the students I mentor.

## Scope

The project aims to explore the design of a typical desktop calendar application, including concepts such as:

* events with start and end times
* validation of event data
* calendar views (daily / weekly)
* UI layout for time-based events
* interaction patterns (creating, editing, displaying events)

The implementation will evolve iteratively as the design becomes clearer.

## Technology Stack

The project is implemented using:

* **Scala:** 3.8.2
* **JDK / SDK:** 21
* **ScalaFX:** 23.0.1-R34

ScalaFX is a Scala wrapper around JavaFX that provides a more idiomatic Scala interface for building desktop user interfaces.

## Project Structure

```
src/
  main/
    scala/
      Main.scala
```

At the moment the project only contains the initial template application.

## Current State

The `Main.scala` file currently contains only the template code provided in the course instructions.
It simply launches a minimal ScalaFX window demonstrating basic UI elements.

No calendar functionality has been implemented yet.

Future work will gradually introduce:

* a data model for calendar events
* UI layout for time-based views
* interaction logic
* input validation

## Running the Project

Make sure you have:

* **JDK 21**
* **sbt**
* a working Scala environment

Then run:

```
sbt run
```

This launches the ScalaFX demo window.

## Purpose of This Repository

This project exists primarily to:

* experiment with architectural decisions for a calendar application
* mirror the development process used by students
* gain practical insight into the difficulties students may face during the project

> [!NOTE]
> It is not intended as a reference solution or a finished application.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.