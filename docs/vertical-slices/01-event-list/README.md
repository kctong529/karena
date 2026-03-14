# Slice 1 — Event List

## Goal of this slice

The goal of this slice is to implement the **first complete vertical slice** of the project.

After completing this slice the system will already contain:

* a domain model
* application logic
* a UI layer
* unit tests

All connected end-to-end.

This does **not** mean the system is already a full calendar application. It means we already have one small but complete piece of functionality that passes through the main layers of the program.

In this slice, that functionality is the ability to work with a basic list of events.

The purpose of this slice is not only to make visible progress, but also to create a concrete setting in which we can discuss foundational design questions that are often hidden in educational examples.


## Why start with an event list?

Calendar systems revolve around events, but a full calendar UI introduces many concerns at once:

* layout in time grids
* overlapping events
* date navigation
* time zone handling
* rendering logic

If we start with all of that immediately, it becomes harder to see which design decisions actually matter at the core of the system.

Starting with an event list gives us a smaller but still meaningful slice:

* the user can see events in the UI
* the application already contains a real data flow
* model and storage decisions become concrete
* testing becomes possible early

This is therefore a good first slice because it is **small enough to reason about**, but still **complete enough to be a real software increment**.


## Scope of this slice

This slice includes:

* a minimal `Event` domain model
* a structure that owns the collection of events
* application logic for basic event operations
* a simple UI representation of the event list
* unit tests for the core logic

This slice intentionally does **not yet** include:

* calendar grid rendering
* drag-based interactions
* recurring events
* ICS import/export
* conflict resolution
* advanced filtering
* time zone-heavy behaviour

Those belong to later slices.


## End-to-end outcome

At the end of this slice, the application should support the following flow:

1. A small set of events exists in the system
2. The application logic can retrieve and manage them
3. The UI can display them as a list
4. Core logic is covered by unit tests

This is enough to count as a real vertical slice, because the main architectural layers are already connected.


## Core design questions explored in this slice

Although the visible functionality is just an event list, this slice is really about several deeper design questions:

* What is the minimum useful `Event` model?
* How should time be represented?
* Where should validation happen?
* Who should own the collection of events?
* Should built-in ordering or event-level interaction matter more in this slice?
* Which Scala collection should be used internally?
* Should events be addressed by index or by ID?
* Where should event objects be created?

These questions matter because they shape later work on editing, filtering, persistence, and calendar rendering.

A good educational walkthrough should not pretend these decisions are obvious from the beginning.


## Event model

The central domain object in this slice is `Event`.

A minimal version might include:

```scala
case class Event(
  id: EventId,
  title: String,
  start: LocalDateTime,
  end: LocalDateTime
)
```

This is intentionally small. We are not yet adding descriptions, locations, categories, recurrence, or reminders.

The point of the first slice is to establish a clean and usable minimum.


## Representing time

Java provides the modern `java.time` API for working with time.

Possible types include:

| Type            | Meaning                        |
| --------------- | ------------------------------ |
| `LocalDate`     | date only                      |
| `LocalDateTime` | date and time without timezone |
| `Instant`       | absolute point in time         |
| `ZonedDateTime` | time with timezone             |

For this slice we choose:

```scala
LocalDateTime
```

Reasons:

* simple and readable
* sufficient for local calendar behavior
* avoids premature timezone complexity

Handling time zones correctly is surprisingly complicated and will be postponed until it becomes necessary.

This is an intentionally local design choice for an early slice, not a claim that `LocalDateTime` is always the best representation for calendar systems in general.


## Event invariants

Even in this minimal slice, an `Event` should still represent a valid concept.

The most basic invariant is:

```scala
start < end
```

An event cannot end before it starts.

This immediately raises a design question: **where should that rule be enforced?**

One possible approach is to keep `Event` as a passive data container and perform validation somewhere else, such as in the UI or application logic. That may seem simpler at first, but it has an important downside: invalid events can then exist inside the system and only fail later.

For this slice, we take the opposite view. The `Event` model should protect its own basic correctness.

That means the rule `start < end` should be checked **when an event is created**, not only later when it is displayed, stored, or edited.

There are several ways to express this in Scala:

* throw an exception if construction is invalid
* return `Option[Event]`
* return `Either[ValidationError, Event]`
* use a smart constructor or factory method

At this stage, the most important decision is not yet which error-handling style is best. The important decision is that **validation belongs at event creation time**.

For this first slice, it is reasonable to keep the mechanism simple, as long as the design makes one thing clear: the system should not silently allow invalid events to exist.


## Immutable events

Events should be immutable.

If an event changes, we replace it with a new value rather than mutating fields in place.

This aligns well with Scala’s overall style and makes reasoning about the system easier, especially once UI updates, filtering, or persistence are added.

Immutability also fits well with testing, because it reduces hidden state changes.


## Who owns the collection of events?

The system needs some class that owns the collection of events and exposes operations over it.

Calling this class `EventStore` is reasonable, though other names are possible.

In this first slice, its purpose is to act as the central owner of the event collection and provide a small amount of application logic over it.

That includes operations such as:

* adding an event
* retrieving all events
* finding one by ID
* updating an event
* deleting an event

This is already enough application logic for a meaningful first vertical slice.

The important architectural point is that this class owns the collection and provides controlled operations over it, instead of letting the UI manipulate raw collections directly.


## A linked design decision

At this point, several design questions start to depend on each other.

The first UI in this slice is only an event list, so it is natural to think in terms of display order. That makes sequence-like collections such as `List` or `Vector` look appealing at first.

At the same time, even a simple event list is likely to grow toward interactions such as selecting, editing, and deleting individual events. That pushes the design toward stable event identity rather than position in a sequence.

Because of that, three questions are tightly connected:

* should we care more about built-in ordering or direct interaction with individual events?
* which collection fits that direction best?
* should events be addressed by position or by ID?

It is better to make this coupling visible than to pretend these are three unrelated decisions.


### The tradeoff: ordering or interaction?

This is the core tension in the slice.

If we use an ordered sequence, display order comes more directly.

If we use a map keyed by ID, lookup and event-level interaction come more directly.

Neither side is universally correct. Different structures make different things feel natural.

For this slice, we choose to give more weight to **interaction with individual events** than to built-in ordering.

That is because the system is already being shaped for later actions such as:

* selecting an event
* editing an event
* deleting an event
* linking a UI element to a specific underlying event

These all treat an event as an individual entity, not just as an item occupying a position in a list.


### Internal collection choice

Once that tradeoff is visible, the collection options become easier to compare.

A student looking at the problem for the first time might reasonably consider several candidates:

* `List[Event]`
* `Vector[Event]`
* a mutable buffer such as `ArrayBuffer[Event]`
* `Map[EventId, Event]`

A sequence type such as `List` or `Vector` feels natural because events are often displayed in order.

A mutable buffer may also seem attractive because insertion and deletion feel straightforward.

However, if stable identity matters more than built-in ordering, a map keyed by event ID becomes a strong candidate:

```scala
Map[EventId, Event]
```

This gives direct access to events by identity and makes updating and deletion conceptually cleaner.

So the other collections are not wrong. They simply emphasize a different concern more strongly.


### Event identity: index or ID?

Once we choose to treat events as individual entities rather than mainly as positions in a sequence, the identity question becomes much easier to answer.

If the main mental model were “events as items in a sequence,” then using position might seem natural:

* first event
* third event
* item at index 5

But positions are unstable. They change when events are inserted, deleted, filtered, or sorted differently.

That makes index unsuitable as the main notion of identity.

For this project, event identity should therefore be separate from display position.

A practical implementation is to use Java’s `UUID` type:

```scala
import java.util.UUID

case class EventId(value: UUID)
```

This makes identity explicit while relying on a standard mechanism for unique IDs.


### Consequence for ordering

Once we choose identity-based access, display order becomes something we derive when needed rather than something baked into storage.

For example:

```scala
store.getAll().toSeq.sortBy(_.start)
```

That is acceptable in this slice because the number of events is still small, and the main goal is to make the underlying design clearer.

So the point is not that maps are always better.

It is that, in this slice, **stable identity and event-level interaction are treated as more important than built-in display order**.


## Where should events be created?

This is one of the key design questions in this slice.

There are two main options.

### Option A: the owner class creates events

In this design, the owner class receives raw attributes and constructs the `Event` itself.

For example:

```scala
store.create(title, start, end)
```

This can seem convenient, because the rest of the program does not need to instantiate `Event` directly.

But it also mixes responsibilities:

* event construction
* validation
* storage

### Option B: events are created outside and then passed in

In this design, another layer creates the event first:

```scala
val event = ...
store.add(event)
```

The owner class then manages the collection, not the construction.

We choose **Option B**.

This keeps responsibilities cleaner:

* `Event` is responsible for being a valid event
* another layer creates or requests event creation
* the store manages the collection

This design is also easier to test and easier to evolve later.


## Application logic in this slice

Because this is a vertical slice, we need more than just a data class.

We also need a small amount of application logic that sits between the model and the UI.

In this slice, that logic is intentionally small and can live inside the event-owning class.

At minimum, it should support these operations:

* `add(event: Event)`
* `getAll(): Iterable[Event]`
* `findById(id: EventId): Option[Event]`
* `update(event: Event)`
* `delete(id: EventId)`

`update` modifies an existing event identified by its ID.
Unlike `add`, which introduces a new event, `update` should only succeed if the event already exists.

These operations are enough to support the first end-to-end functionality.

The class does **not** create events from raw fields. Instead, it manages already constructed event values.

One additional question should be decided explicitly: what happens if `add(event)` is called with an ID that already exists? For this slice, that behavior should be chosen deliberately and covered by tests rather than left implicit.


## UI in this slice

This slice is not only about backend structure. It must also include a UI layer.

The UI does not need to look like a full calendar yet.

A simple event list is enough.

That UI should:

* obtain events through the application logic
* display them in a readable order
* make the domain objects visible to the user through a real interface

The purpose of this UI is not completeness, but integration.

It proves that the model and logic are already usable from the presentation layer.


## Unit tests in this slice

This slice should also include unit tests.

At minimum, the tests should cover:

* valid event creation
* invalid event rejection
* adding events to the owner class
* retrieving events
* updating an event
* deleting an event
* duplicate-ID behaviour
* possibly ordering as a derived view if that logic is introduced

This is important because the slice is not only a prototype. It is the beginning of a codebase whose behaviour should already be verifiable.


## Example structure

A minimal internal structure could look like this:

```scala
class EventStore {

  private var events: Map[EventId, Event] = Map.empty

  def add(event: Event): Unit =
    events += (event.id -> event)

  def getAll(): Iterable[Event] =
    events.values

  def findById(id: EventId): Option[Event] =
    events.get(id)

  def update(event: Event): Unit =
    events += (event.id -> event)

  def delete(id: EventId): Unit =
    events -= id
}
```

This is intentionally simple, but it does not yet enforce the intended semantic difference between `add` and `update`.

The purpose is not to finalize the architecture immediately, but to create a clean starting point that already supports the first vertical slice.

However, one subtle point should be noticed: in this version, `add` and `update` currently behave the same way if the ID already exists. That may or may not be what we want. The important thing is to notice the choice and make it explicit rather than letting it slip in accidentally.


## Tasks for this slice

### 1. Define `EventId`

Create a dedicated type for event identity.

Acceptance criteria:

* event IDs are represented by a dedicated type
* the type supports equality comparison
* raw strings are not used directly as event identifiers throughout the code

### 2. Implement `Event`

Create the basic event model.

Acceptance criteria:

* contains `id`, `title`, `start`, and `end`
* uses `LocalDateTime` for `start` and `end`
* is immutable
* enforces the invariant that `start < end`

### 3. Implement the event-owning application logic

Create the class that owns and manages the event collection.

Acceptance criteria:

* stores events internally using `Map[EventId, Event]`
* supports:
  * `add(event)`
  * `getAll()`
  * `findById(id)`
  * `update(event)`
  * `delete(id)`
* event creation is handled outside this class
* duplicate-ID behaviour for `add` is defined explicitly
* missing-ID behaviour for `update` is defined explicitly

### 4. Connect a simple event list UI

Create a minimal UI that displays events from the application layer.

Acceptance criteria:

* events are retrieved through the application logic
* the UI displays the current list of events
* display order is derived explicitly when needed

### 5. Add unit tests

Create tests for the domain model and application logic.

Acceptance criteria:

* valid events can be created
* invalid events are rejected
* events can be added, retrieved, updated, and deleted correctly
* duplicate-ID behaviour is tested


## What this slice teaches

Although the visible result is small, this slice already teaches several important engineering lessons:

* a vertical slice should connect layers end-to-end
* domain identity should be treated explicitly
* ordering and identity are different concerns
* collection choice reflects intended interaction patterns
* time representation is a design decision, not just a syntax choice
* validation should not be left vague
* architecture should expose real tradeoffs, not hide them

This is exactly why the slice is valuable educationally.

The goal is not to pretend that the first design is perfect, but to make the reasoning visible and honest.


## After this slice

Once this slice is complete, later slices can build on a working foundation.

Possible next steps include:

* moving from list view to calendar view
* adding event creation from the UI
* supporting editing interactions
* adding persistence
* introducing filtering and search
* handling layout and overlap

Because this first slice already establishes identity, ownership, time representation, and basic application flow, those later additions will have a clearer place to live.
