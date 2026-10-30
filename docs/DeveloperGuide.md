---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TAssist Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

For `delete 1`, `DeleteCommandParser` parses the displayed index into a `StudentIdentifier` named `targetStudent` and passes it to `DeleteCommand(targetStudent)`.
During `execute(m)`, where `m` is the `Model` instance, `DeleteCommand` calls `targetStudent.resolve(m)` to obtain the target `Person`, stored in `personToDelete`.
It then calls `m.deletePerson(personToDelete)` and returns a `CommandResult` containing the deletion success message.

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a student).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />

This diagram is simplified: it omits some value classes, such as `AttendanceDate`.


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores shared assignment definitions and student marks in a `Gradebook` owned by `AddressBook`. Gradebook mutations are exposed through the `Model` API, while reads use a snapshot.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* displays the filtered students through a sorted view. Name order is the default; `add`, `find`, and `list` restore it after a sort. `SortCommand` selects a comparator for name, matriculation number, email, attendance record count, or one assignment's marks, and may reverse its primary comparison. Equal values use name and matriculation number as tie-breakers. Sorting changes only the view, not the stored student order.
* exposes the active sort's optional card detail through an observable `SortDisplay`. The UI refreshes student cards when this changes. Attendance sorting shows each student's record count; mark sorting shows the selected assignment's result or missing status. Gradebook changes, student edits, and data restoration reset an active mark sort to name order so the saved comparison snapshot does not become stale.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-T10-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* saves assignment definitions and student assignment marks with the address book data, so they are restored together with their students.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Assignment mark feature

The `seedu.address.model.gradebook` package defines the data model for assignment marks. `AddressBook` owns a `Gradebook`, and `Model` exposes operations for assigning work, recording marks, and removing links or definitions. JSON storage saves and restores this data. The `assignment` command creates and attaches assignments, `mark` records or updates a student's score, `marks` displays the results for one assignment, and `removeassignment` removes one student's link or the entire shared assignment.

<puml src="diagrams/GradebookDomainDiagram.puml" alt="Assignment mark domain model" />

`AssignmentId` combines a module code and an assignment code, such as `CS2103T:A1`. This keeps `A1` in one module distinct from `A1` in another. `Assignment` holds that ID, a name, and a positive maximum mark. The `Gradebook` rejects duplicate IDs and names within the same module; names are compared without regard to case or repeated spaces. The maximum is defined once for all students assigned that work.

The `Gradebook` separately maps each student's matriculation number and assignment ID to an optional `Mark`. Assigning work creates an unmarked entry. A recorded zero is represented by a `Mark` with value `0`, so it remains distinct from an unmarked entry. `Mark` uses `BigDecimal` to preserve fractional marks without floating-point rounding; the `Gradebook` checks that a recorded value does not exceed the assignment maximum. `Assignment` does not contain a student's mark because the same assignment can have different marks for different students.

`GradebookNumber` checks the length of a normalized value's plain decimal form using its precision and scale, before creating that string. `Assignment` and `Mark` both reject values that would display as more than 64 characters. The check is in the domain constructors, so commands and restored JSON data follow the same limit. Parsers turn this validation failure into a normal input error. This prevents short scientific-notation inputs with enormous exponents from expanding during display or JSON saving.

`AddressBook` coordinates student and gradebook changes. Creating an assignment for a student adds its definition and the student's unmarked entry as one operation. Editing a student's matriculation number moves their assignment entries and marks; editing other details leaves them in place. Deleting a student removes their entries but keeps shared assignment definitions. Replacing the student list removes entries for students no longer present, while resetting or clearing the whole address book replaces the gradebook too. A failed student replacement leaves the original gradebook unchanged. `ReadOnlyAddressBook#getGradebook()` returns an independent snapshot so callers cannot mutate the stored gradebook through a read-only view.

JSON storage keeps shared definitions in `assignments` and each student's link to a definition in `studentAssignments`. An assignment stores its ID, name, and decimal maximum mark. A student assignment stores a matriculation number, assignment ID, and decimal `mark`; a `null` mark means the assignment is unmarked, while `"0"` means a recorded zero. Decimal values are written as strings to preserve exact fractional marks. On restoration, students are loaded first, followed by definitions and student assignments. The storage adapter rejects invalid or duplicate definitions, unknown students or assignments, duplicate student links, and marks outside the allowed range. Files created before these two JSON arrays existed load with an empty gradebook. A failed restoration leaves the saved file unchanged.

`AssignmentCommandParser` accepts a student's matriculation number and assignment ID, plus a name and maximum mark together when defining new work. `AssignmentCommand` resolves the student against all stored students, independently of the displayed list. For new work, it calls `Model#addAssignmentForStudent`, which validates and publishes the definition and student link as one change. For existing work, it calls `Model#assignStudent` using the stored definition. The gradebook rejects a duplicate ID or name in the module and prevents assigning the same work twice to one student. Neither command form records a mark.

`MarkCommandParser` accepts a matriculation number, an assignment ID, and a nonnegative decimal score. `MarkCommand` checks that the student, shared definition, and student's assignment link exist before calling `Model#recordMark`. The gradebook rejects scores above the shared maximum. `recordMark` returns the previous optional value, allowing the command to distinguish the first recorded mark from an update; a previous zero is present and therefore produces the updated response. The command changes only that student's score. `edit` remains for student particulars, and `view` remains focused on profile details.

`MarksCommandParser` accepts one assignment ID. `MarksCommand` obtains a read-only gradebook snapshot and joins that assignment's student marks to current student names by matriculation number. It sorts rows by matriculation number, independently of the filtered and sorted student list, and formats each score against the shared maximum. A missing `Mark` is printed as `Unmarked`, while a recorded zero is printed as `0/MAX_MARK`. An assignment with no students prints `None assigned.`. This command does not change the model or the displayed student list.

`RemoveAssignmentCommandParser` accepts an assignment ID and an optional matriculation number. With a student number, `RemoveAssignmentCommand` checks that the student and assignment exist, then calls `Model#unassignStudent`; the gradebook removes only that student's link and optional mark. Without a student number, it calls `Model#removeAssignment`, which removes the definition and all links and marks under that ID. Both operations leave `Person` objects and unrelated gradebook entries untouched. Removing a definition releases its ID and name for reuse. The changed address book is saved through the usual command flow, so the removal persists after reopening the app. There is no archive or separate assignment edit operation.

### Sorting displayed students

The `sort` command accepts `sort`, `sort FIELD [asc|desc]`, or `sort mark id/MODULE:CODE [asc|desc]`. `SortCommandParser` ignores letter case for field and direction keywords, and reports the assignment ID constraints for an invalid ID. `SortCommand` sorts the displayed students without changing their stored order. Name and email comparisons ignore case, and an empty attendance history counts as zero. Recorded marks come before `Unmarked` and `Not assigned` in either direction; ties use name and then matriculation number.

`SortDisplay` supplies the optional extra line on each student card. An attendance sort shows the record count. A mark sort shows the assignment ID and each student's mark or missing status. `add`, `find`, and `list` restore name order and remove that line. Assignment, mark, or student detail changes reset an active mark sort so its display does not become stale.

### Attendance feature

The `attendance` command marks a student or a whole class present at a class session, and `attendance unmark` removes an individual student's record. Each `AttendanceRecord` contains an `AttendanceDate` and a `ClassCode`; the same student may have records for different classes on one date, but not two records for the same class and date. Single-student mark and unmark requests identify the student by `INDEX` or `m/MATRICULATION_NUMBER`. All forms take an optional `d/DATE`, which defaults to today. Mark requires `c/MODULE-CLASS`; unmark accepts an optional `[c/MODULE-CLASS]`.

`MarkAttendanceCommand`, `MarkClassAttendanceCommand` and `UnmarkAttendanceCommand` extend the abstract `AttendanceCommand`, which holds the command word, the optional date (defaulting to today), equality that requires the same command type and date, and the helper that copies a student with new attendance.

`AttendanceCommandParser` checks the first word of the arguments. If it is `unmark`, in any letter case, it calls `parseUnmark` with the remaining arguments; otherwise, it tokenizes the arguments before choosing the mark form. Both paths use `tokenizeAttendanceArguments` to tokenize `m/`, `c/` and `d/` and reject duplicate prefixes. Single-student requests then parse the student identifier, class code and optional date, in that order, before constructing their command. `parseMark` requires a class code, while `parseUnmark` leaves it optional. Duplicate-prefix errors take priority over identifier errors, then class code errors, then date errors.

For marking, an empty preamble with no `m/` and a present `c/` selects `parseMarkClass`, which parses the class code and optional date and constructs `MarkClassAttendanceCommand`; all other arguments go to `parseMark`. The whole-class command collects enrolled students from all students in TAssist, including those outside the displayed list. It reports an error without changing anything if none are enrolled. Otherwise, it skips students already recorded for that class and date, reuses `AttendanceCommand#createStudentWithAttendance` to create each updated student, and calls `Model#setPerson` once per marked student. The result reports the marked count and names any skipped students.

The simplified sequence diagram below shows a successful run of `attendance unmark m/A1234567X` after parsing, assuming the student exists and has exactly one attendance record today. It omits date selection and choosing which record to remove, which the steps below explain.

<puml src="diagrams/UnmarkAttendanceSequenceDiagram.puml" alt="Execution of the attendance unmark command" />

1. `StudentIdentifier#resolve` returns the student `s`, found by displayed index or matriculation number.
2. The command uses today's date because none was given. With `c/`, it chooses the record matching that class and date, or throws a `CommandException` if none exists. Without `c/`, it chooses the only record on that date. An empty history for that date produces an error; several records produce an error naming their classes in ascending order and requesting `c/MODULE-CLASS`. Every error leaves the student unchanged. Unmark checks existing records, not current enrollment.
3. The command does not modify `s`. It creates a copy `e` whose `Attendance` no longer contains the chosen record, using `Attendance#withoutRecord`.
4. `Model#setPerson` replaces `s` with `e`, and the command returns a `CommandResult` `r` with the success message.

Single-student marking resolves the student first, then checks that the student has enrolled classes, is enrolled in the given class, and has no record for that class and date, in that order. Class code comparison ignores letter case, as in `enroll`. The copy gains the record using `Attendance#withRecord`. Single-student mark and unmark success messages name the student, class and date, including when unmark infers the class.

Design considerations:

* Whole-class marking skips students already recorded for the class and date instead of rejecting the entire request. This lets tutors mark the remaining students even when some records already exist, and safely repeat the command.
* Records keep their class code independently of current enrollment, so unenrolling a student, from one class or with `all/`, keeps their attendance history. Unmarking with `c/` and loading saved data do not require a record's class to be a current enrollment.
* Date-only attendance entries from earlier versions are rejected rather than migrated, because they do not identify the class attended. TAssist reports the load error, blocks commands and leaves the file unchanged until the user fixes or removes it.


### Viewing class attendance statistics

The attendance statistics feature allows tutors to check how many students attended a class on a given date, compared with the number of students currently enrolled in that class.

#### Command parsing

The feature is implemented as a `stats` subcommand under the existing `attendance` command.

`AddressBookParser` continues to delegate attendance-related commands to `AttendanceCommandParser`. The parser identifies the `stats` subcommand and creates a `ViewAttendanceStatsCommand`.

The command requires a class code and date. The parser reuses `ParserUtil` to validate the class code and attendance date, and rejects missing arguments, student identifiers and repeated prefixes.

#### Calculating attendance statistics

`ViewAttendanceStatsCommand` reads the complete student list through `Model.getAddressBook().getPersonList()`, rather than the filtered list currently shown in the UI.

It first identifies students currently enrolled in the specified class. The size of this group gives the total number of enrolled students.

The command then creates an `AttendanceRecord` using the requested class code and date. For each enrolled student, it checks whether the student's `Attendance` contains that record.

The number of matching students gives the attended count. Since attendance records are unique for each student, class and date, a student is counted at most once.

The command returns both counts through `CommandResult`.

#### Design decisions

* The statistics are calculated from existing attendance records, so no additional fields or storage format changes are needed.
* The command is read-only and does not modify students, attendance records, or the displayed list.
* Both the class and date are required to identify a particular attendance session.
* The total uses current enrollment rather than historical enrollment, since the application does not store enrollment history for each date.
* Students who are no longer enrolled are excluded from both counts, even if they still have historical attendance records.
* A missing attendance record is not treated as confirmed absence. It simply does not contribute to the attended count.
* An empty class produces zero for both counts, avoiding division by zero or unnecessary errors.

### Undo/redo feature

#### Implementation

The undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Each snapshot contains students and an independent copy of the gradebook, including assignment links and marks. History lasts for the current application session and is not persisted. It implements the following operations:

* `VersionedAddressBook#commit()` -- Saves changed address book data in its history and discards any redo branch. If the data matches the current snapshot, it does nothing and preserves redo history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

`LogicManager#execute()` calls `Model#commitAddressBook()` once after successful command execution and before saving to storage. Commands do not commit history themselves. The model ignores commits whose data matches the current snapshot, so display-only commands, no-op changes, and undo/redo preserve the current history and redo branch.

After successful restoration, `ModelManager` clears the person filter, restores `COMPARATOR_NAME_ASC` and `SortDisplay.NONE`, and increments `addressBookRestoreCount`. `MainWindow` listens to this counter to close student details that could otherwise show stale records. Unavailable undo/redo throws `IllegalStateException` at model level; the commands check availability first and report a `CommandException` without changing the display or counter.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th student in the address book. After the command succeeds, `LogicManager` calls `Model#commitAddressBook()`, saving the modified address book in `addressBookStateList` and shifting `currentStatePointer` to the newly inserted state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new student. After successful execution, `LogicManager` commits the changed data, adding another state to `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** Parsing or command-execution failures do not reach the history commit. `LogicManager` commits successful changes before saving the data file. If saving fails, the in-memory change and history remain effective even though the user receives a `CommandException`. Likewise, a successful undo/redo still restores records and notifies the UI if the subsequent save fails.
</box>

Step 4. The user now decides that adding the student was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. `LogicManager` calls `Model#commitAddressBook()` after it succeeds, but the model detects that records match the current snapshot. Thus, `addressBookStateList` and redo history remain unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, after which `LogicManager` calls `Model#commitAddressBook()`. Assuming the address book is nonempty, this changes records. Since `currentStatePointer` is not pointing at the end of `addressBookStateList`, states after the pointer are purged. If the address book were already empty, `clear` would make no change and preserve redo history.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the student being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* is a university Computer Science tutor
* manages multiple modules and tutorial/lab groups concurrently
* needs quick access to students' particulars, grades, attendance and assignment progress
* prefers typing to mouse interactions
* is comfortable using CLI apps

**Value proposition**: View and update student particulars, grades, attendance and assignments with single-line commands, faster than multi-click web interfaces and without breaking the tutor's coding workflow.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                   | I want to …                                                  | So that I can…                                                  |
|----------|------------------------------------------|--------------------------------------------------------------|-----------------------------------------------------------------|
| `* * *`  | tutor                                    | add a student                                                | keep track of the students in my classes                        |
| `* * *`  | tutor                                    | delete a student                                             | remove students who are no longer in my classes                 |
| `* * *`  | tutor                                    | edit a student's details                                     | keep their information up to date without re-adding them        |
| `* * *`  | tutor                                    | list all students                                            | see everyone under my care                                      |
| `* * *`  | tutor                                    | search for a student by name                                 | find their information quickly                                  |
| `* * *`  | tutor                                    | view a student's profile                                     | review their particulars, classes and attendance in one place   |
| `* * *`  | tutor teaching multiple modules          | assign students to modules and class sections                | organise the classes I teach                                    |
| `* * *`  | tutor teaching multiple modules          | filter students by class                                     | see all students from one class                                 |
| `* * *`  | tutor                                    | record a student's attendance for a specific class session   | keep track of whether they attended class                       |
| `* * *`  | tutor                                    | record a student's assignment marks                          | keep track of their academic performance                        |
| `* * *`  | tutor                                    | sort my class by grades in ascending or descending order     | identify struggling students quickly                            |
| `* * *`  | tutor                                    | have my student records restored when I reopen the app       | continue working without entering them again                    |
| `* *`    | new user                                 | see a list of commands and how to use them                   | start using the app without memorising commands                 |
| `* *`    | forgetful tutor                          | search for a student by matriculation number                 | find them when I cannot recall their name                       |
| `* *`    | tutor                                    | update an attendance record                                  | correct mistakes made during attendance taking                  |
| `* *`    | tutor                                    | mark a whole class present for a session                     | record attendance quickly and then unmark absentees             |
| `* *`    | tutor                                    | view attendance statistics for a class                       | monitor participation trends                                    |
| `* *`    | tutor                                    | record a student's class participation                       | grade their participation                                       |
| `* *`    | tutor                                    | undo my last action                                          | reverse mistakes                                                |
| `* *`    | tutor                                    | redo an undone action                                        | recover from an accidental undo                                 |
| `* *`    | tutor                                    | have my filters persist across searches                      | avoid retyping the same filters                                 |
| `*`      | tutor who has finished teaching a class  | delete students in batches                                   | remove a whole class at once                                    |
| `*`      | tutor teaching a new class               | import students from a file with one command                 | avoid adding them one by one                                    |
| `*`      | tutor                                    | add notes to a student                                       | remember details about them                                     |
| `*`      | tutor                                    | colour-code student entries by conditions such as class      | tell my classes apart at a glance                               |
| `*`      | tutor                                    | see a student's photo                                        | recognise them in class                                         |
| `*`      | tutor                                    | view a student's grade trend                                 | see their progress over time                                    |
| `*`      | tutor                                    | record which students are friends                            | ask friends to help students who are struggling                 |
| `*`      | tutor managing at-risk students          | pin a student entry                                          | find them quickly for special attention                         |
| `*`      | tutor                                    | open my mail app with a student's email filled in            | email students without copying their addresses                  |
| `*`      | tutor teaching many modules              | autocomplete course codes                                    | avoid memorising module identifiers                             |
| `*`      | tutor                                    | view previously executed commands                            | reuse commands without retyping them                            |
| `*`      | tutor                                    | view my search history                                       | repeat past searches easily                                     |
| `*`      | tutor                                    | clear my search history                                      | keep only relevant entries                                      |
| `*`      | experienced user                         | set custom command aliases                                   | type fewer characters                                           |
| `*`      | experienced user                         | customise the app's appearance                               | match it to my other applications                               |

### Use cases

These use cases describe the current program's behavior. For all use cases, the **System** is TAssist, the **Actor** is a university Computer Science tutor, and **MSS** means Main Success Scenario. Unless stated otherwise, TAssist is running and startup has completed without a restoration error. Restoration failures are covered by UC07.

**Common extension (UC01–UC06, UC08–UC14):** If TAssist cannot save the records after processing a request, it reports the save failure instead of confirming success. Any changes already made to student information, assignment marks, or the displayed list remain. The main success scenarios assume saving succeeds.

#### UC01: Add a student

**MSS**

1. Tutor requests to add a student, supplying a name, matriculation number, and email address, and optionally tags.
2. TAssist adds the student, displays all students in name order, and confirms the addition.

Use case ends.

**Extensions**

* 1a. The request has invalid syntax, a required detail is missing, or a supplied detail has an invalid format.

  * 1a1. TAssist explains the invalid input and does not add a student.

  Use case resumes at step 1.

* 1b. The matriculation number belongs to an existing student.

  * 1b1. TAssist reports the duplicate and does not add another student.

  Use case resumes at step 1.

#### UC02: Delete a student

**MSS**

1. Tutor requests to delete a student using their displayed index or matriculation number. The matriculation number can identify a student excluded from the current search results.
2. TAssist deletes the selected student from TAssist and confirms the deletion.

Use case ends.

**Extensions**

* 1a. The request has invalid syntax, the identifier is missing or malformed, or both an index and a matriculation number are supplied.

  * 1a1. TAssist explains the expected input without deleting a student.

  Use case resumes at step 1.

* 1b. The index exceeds the size of the currently displayed list, including when the list is empty.

  * 1b1. TAssist reports that the displayed student index is invalid and deletes nothing.

  Use case resumes at step 1.

* 1c. No stored student has the supplied matriculation number.

  * 1c1. TAssist reports that no student with that matriculation number was found and deletes nothing.

  Use case resumes at step 1.

#### UC03: Search students

**MSS**

1. Tutor requests to search for students, supplying one or more name keywords.
2. TAssist displays the matching students in name order and the number of matches, replacing the previous search results and clearing the active sort.

Use case ends.

Matching considers all students and ignores case. A student matches if any supplied keyword occurs within a word in their name. Each matching student appears once, including distinct students with the same name. Non-empty keywords are accepted without student-name validation.

**Extensions**

* 1a. Tutor supplies no search keywords.

  * 1a1. TAssist reports an input error and explains the expected input. The displayed list and student records remain unchanged.

  Use case resumes at step 1.

* 2a. No students match any of the keywords.

  * 2a1. TAssist reports zero matches and displays an empty result list.

  Use case ends.

#### UC04: Record attendance

**MSS**

1. Tutor requests to record a student's attendance, identifying the student by their displayed index or matriculation number, and supplying a class and date. The matriculation number can identify a student excluded from the current search results.
2. TAssist records the student as present in that class on that date and confirms the student's name, class and attendance date.

Use case ends.

**Extensions**

* 1a. Tutor omits the date.

  * 1a1. TAssist uses the computer's current date.

  Use case resumes at step 2.

* 1b. The index exceeds the size of the currently displayed list, including when the list is empty.

  * 1b1. TAssist reports that the displayed student index is invalid without adding an attendance record.

  Use case resumes at step 1.

* 1c. The supplied date is empty or does not meet the date requirements.

  * 1c1. TAssist explains the date requirements without adding an attendance record.

  Use case resumes at step 1.

* 1d. Attendance has already been recorded for that student in the same class on that date.

  * 1d1. TAssist reports the existing record without adding a duplicate.

  Use case ends.

* 1e. The student identifier is missing or malformed, both an index and a matriculation number are supplied, or the matriculation number, class or date is supplied more than once.

  * 1e1. TAssist explains the invalid input without adding an attendance record.

  Use case resumes at step 1.

* 1f. No stored student has the supplied matriculation number.

  * 1f1. TAssist reports that no student with that matriculation number was found without adding an attendance record.

  Use case resumes at step 1.

* 1g. The class is missing or invalid.

  * 1g1. TAssist requests a class or explains the class code requirements without adding an attendance record.

  Use case resumes at step 1.

* 1h. The student is not enrolled in any class or is not enrolled in the supplied class.

  * 1h1. TAssist reports the enrollment problem without adding an attendance record. If the student has no classes, it asks the tutor to enroll them first.

  Use case resumes at step 1.

Removing an attendance record (unmarking) follows the same interaction, with these differences: in step 2, TAssist removes the student's record for the given class and date instead of adding one. The class is optional when exactly one record exists on that date. With several records on that date and no class supplied, TAssist reports an error naming the classes and asks the tutor to specify one; no records change (use case resumes at step 1). Extensions 1d and 1h do not apply, and 1g applies only to an invalid supplied class: the record's class need not be a current enrollment. If no record matches the given class and date, or no records exist on that date when the class is omitted, TAssist reports this and leaves the student's records unchanged (use case ends). Success confirms the removed record's class even when inferred.

For whole-class marking, the tutor supplies a class and an optional date without a student identifier. TAssist marks every enrolled student present for that session and reports how many students were marked, naming any students already recorded for that class and date who were skipped. If no students are enrolled in the class, TAssist reports an error and changes nothing.

#### UC05: List all students

**MSS**

1. Tutor requests to list all students.
2. TAssist displays every student exactly once in name order, removing any search restriction and clearing the active sort, and confirms that all students have been listed.

Use case ends.

The default display order ignores case when comparing names. It does not change the order of students in saved data.

**Extensions**

* 1a. Tutor supplies additional details with the request.

  * 1a1. TAssist reports an input error and explains the expected input. The displayed list, active search filter, and student records remain unchanged.

  Use case resumes at step 1.

* 2a. TAssist has no stored student records.

  * 2a1. TAssist displays an empty list and confirms that all students have been listed.

  Use case ends.

#### UC06: View a student's profile

**Guarantees:** The current student list, its order, any active search or filter, and student information remain unchanged.

**MSS**

1. Tutor requests to view a student's profile using the student's index in the currently displayed list.
2. TAssist opens or updates a details window showing the selected student's name, matriculation number, email, tags, enrolled classes and attendance records with their dates and classes. Tags are sorted by their text, classes by module and class group, and attendance by date from most recent to oldest, then by class code in ascending order within a date.

Use case ends.

**Extensions**

* 1a. The index is missing, more than one index is supplied, or the index format is invalid.

  * 1a1. TAssist explains the expected input.

  Use case resumes at step 1.

* 1b. No students are currently displayed.

  * 1b1. TAssist reports that the student index is invalid.

  Use case ends.

* 1c. The displayed list is non-empty, but the index exceeds its size.

  * 1c1. TAssist reports that the student index is invalid.

  Use case resumes at step 1.

* 2a. The student has no tags or no recorded attendance.

  * 2a1. TAssist indicates that no tags or attendance have been recorded, as applicable, alongside the student's other details.

  Use case ends.

#### UC07: Restore student records when reopening the app

**Precondition:** The tutor has added a student, recorded attendance, or saved assignment marks in an earlier session, and the change was saved successfully.

**MSS**

1. Tutor closes TAssist and reopens it.
2. TAssist restores the saved student, attendance, and assignment records.

Use case ends.

**Extensions**

* 2a. No saved data file exists.

  * 2a1. TAssist starts with sample student records.

  Use case ends.

* 2b. TAssist cannot restore the saved records because the data file cannot be read, contains malformed data, or contains invalid student, attendance, or assignment records.

  * 2b1. TAssist reports why restoration failed.
  * 2b2. TAssist leaves the original data file unchanged and prevents command execution.
  * 2b3. TAssist instructs the tutor to close the app, correct the file, replace it with a valid backup, or delete it to start with sample student records, then reopen the app.

  Use case ends.

#### UC08: Enroll student into a class

**MSS**

1. Tutor requests to enroll a student from the displayed list in a class, supplying one class code that includes the module and group.
2. TAssist enrolls the student in that class and confirms the enrolment.

Use case ends.

**Extensions**

* 1a. The module code or class code is invalid.

    * 1a1. TAssist will specify the format of the module and class code.

    Use case ends.

* 1b. The index is not a positive integer, or is outside the range of the displayed student list.

    * 1b1. TAssist shows an error message.

    Use case ends.

* 1c. The student is already enrolled in that class.

    * 1c1. TAssist shows an error message and does not change the student.

    Use case ends.
#### UC09: Assign work to a student

**MSS**

1. Tutor supplies a stored student's matriculation number, a new assignment ID, its name, and its maximum mark.
2. TAssist creates the shared assignment definition, attaches it to that student without a recorded mark, and confirms the assignment.

Use case ends.

**Extensions**

* 1a. Tutor supplies only a student's matriculation number and an existing assignment ID.

  * 1a1. TAssist attaches that definition to the student without changing its name or maximum mark.

  Use case resumes at step 2.

* 1b. The student does not exist, the new assignment ID or name already exists in its module, or the existing assignment is already attached to the student.

  * 1b1. TAssist reports the problem and changes no records.

  Use case ends.

* 1c. Input is malformed, the maximum mark is not positive, or the shorter form names an unknown assignment ID.

  * 1c1. TAssist explains the problem and changes no records.

  Use case ends.

#### UC10: Record or update an assignment mark

**MSS**

1. Tutor supplies a stored student's matriculation number, an assignment ID already attached to that student, and a score from zero through that assignment's maximum.
2. TAssist records the score and confirms it alongside the assignment maximum.

Use case ends.

**Extensions**

* 1a. The student or assignment does not exist, the student has not been assigned that work, or the score exceeds its maximum.

  * 1a1. TAssist reports the problem and leaves the records unchanged.

  Use case ends.

* 1b. The input is malformed or the score is not a nonnegative number.

  * 1b1. TAssist explains the invalid input and leaves the records unchanged.

  Use case ends.

* 2a. A score was already recorded for that student and assignment, including zero.

  * 2a1. TAssist replaces the score and confirms that the mark was updated.

  Use case ends.

#### UC11: View an assignment's marks

**MSS**

1. Tutor supplies an existing assignment ID.
2. TAssist shows its name and maximum mark, followed by each assigned student's current name, matriculation number, and score or `Unmarked` status, ordered by matriculation number.

Use case ends.

**Extensions**

* 1a. The assignment ID is malformed or does not exist.

  * 1a1. TAssist explains the problem without changing records or the displayed student list.

  Use case ends.

* 2a. No students are assigned to the work.

  * 2a1. TAssist shows `None assigned.` below the assignment details.

  Use case ends.

#### UC12: Remove an assignment

**MSS**

1. Tutor supplies an existing assignment ID without a student number.
2. TAssist removes its shared definition and every student's link and mark for that ID, saves the change, and confirms removal.

Use case ends.

**Extensions**

* 1a. Tutor also supplies a student's matriculation number.

  * 1a1. TAssist removes only that student's link and mark for the assignment, leaving the shared definition and other students' results intact.
  * 1a2. TAssist saves the change and confirms which student was unassigned.

  Use case ends.

* 1b. The assignment or student does not exist, or the student is not assigned that work.

  * 1b1. TAssist reports the problem without changing records.

  Use case ends.

* 1c. The input is malformed.

  * 1c1. TAssist shows the accepted command format without changing records.

  Use case ends.

Neither removal form changes student particulars. A removed shared assignment can be created again with the same ID or name.

#### UC13: Sort displayed students

**MSS**

1. Tutor requests to sort the currently displayed students by name, matriculation number, email address, recorded attendance count, or one existing assignment's mark, optionally choosing ascending or descending order.
2. TAssist reorders the displayed students and confirms the selected field and direction.

Use case ends.

With no field or direction supplied, TAssist uses name ascending. Students without a recorded mark appear after students with marks in either direction. Equal values are ordered by name, then matriculation number. Sorting changes the displayed order, not the stored student records.

**Extensions**

* 1a. The field or direction is unknown, or extra arguments are supplied.

  * 1a1. TAssist explains the expected syntax. The displayed list and its order remain unchanged.

  Use case resumes at step 1.

* 1b. The assignment ID is syntactically valid but does not exist.

  * 1b1. TAssist reports the unknown assignment and preserves the displayed order and card details.

  Use case ends.

* 2a. No students are currently displayed.

  * 2a1. TAssist confirms the sort; the displayed list remains empty.

  Use case ends.

#### UC14: Unenroll a student from a class

**MSS**

1. Tutor requests to unenroll a student using their displayed index, supplying one class code that includes the module and group.
2. TAssist removes the student's enrolment in that class and confirms the removal.

Use case ends.

**Extensions**

* 1a. Tutor omits the class code but indicates unenrollment from all classes, and the student is enrolled in at least one class.

  * 1a1. TAssist removes all the student's class enrolments and confirms the removal.

  Use case ends.

* 1b. The index is missing or malformed, the supplied class code is invalid, more than one class code is supplied, neither class code nor unenrollment from all classes is indicated, or unenrollment from all classes is indicated more than once.

  * 1b1. TAssist explains the invalid input and leaves the student's enrolments unchanged.

  Use case resumes at step 1.

* 1c. The index exceeds the size of the currently displayed list, including when the list is empty.

  * 1c1. TAssist reports that the displayed student index is invalid and changes no records.

  Use case resumes at step 1.

* 1d. The student is not enrolled in the specified class.

  * 1d1. TAssist reports this and leaves the student's enrolments unchanged.

  Use case ends.

* 1e. Tutor omits the class code but indicates unenrollment from all classes, and the student is not enrolled in any classes.

  * 1e1. TAssist reports that the student has no class enrolments to remove and changes no records.

  Use case ends.


### UC16: View attendance statistics for a class

**Actor:** Tutor

**Main success scenario**

1. The tutor requests attendance statistics for a class on a specified date.
2. TAssist finds all students currently enrolled in that class.
3. TAssist counts the enrolled students who have an attendance record for that class and date.
4. TAssist displays the attended count and total enrolled count.

Use case ends.

**Extensions**

1a. The class code or date is missing or invalid.

    1a1. TAssist displays an error message explaining the required input.

    Use case resumes at step 1.

2a. No students are currently enrolled in the class.

    2a1. TAssist displays 0 students attended and 0 total students.

    Use case ends.


### Non-Functional Requirements

Compatibility, packaging and storage requirements follow the [CS2103T project constraints](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-constraints.html). Performance targets assume a computer with at least 8 GB RAM running Java 25.

1. **Platform:** Should work on Windows, Linux and macOS on a computer with only Java 25 installed.
2. **Distribution:** Should be a single JAR file of at most 100 MB that runs without an installer.
3. **Storage:** Should store data locally in a human-editable text file, without a database management system.
4. **Performance:** Should respond to any command within 2 seconds with 1,000 students, each with 40 attendance records, stored.
5. **Startup:** Should be ready to accept commands within 10 seconds of launch with the same data as NFR 4 stored.
6. **Keyboard use:** Every operation should be possible with the keyboard alone, with each command entered on a single line.
7. **Learnability:** A first-time user familiar with CLI apps should be able to add, find, view, record attendance for and delete a student within 15 minutes, using only the User Guide.
8. **Error handling:** Invalid input should not crash the app or change any record, and the error message should name the problem.
9. **Persistence:** Changes from a successful command should remain after closing and reopening the app.
10. **Save failure:** A failed save should be reported instead of shown as a success.
11. **Corrupted data:** If the data file cannot be read or contains invalid data, the app should report the problem and leave the file unchanged.
12. **Offline use:** All features should work without an internet connection; only reading the online User Guide needs one.
13. **Display:** Should display without overlapping or hidden content at 1920 x 1080 and above with 100% or 125% scaling, and keep all functions usable at 1280 x 720 and above with 150% scaling.
14. **Single user:** Should be for a single user, without the data file being shared between users.

### Glossary

| Term | Definition |
|------|------------|
| Student record | The stored information for one student, including particulars, module and class memberships, and associated academic records. |
| Student particulars | The identifying and contact details held in a student record. The MVP stores name, matriculation number, and email address. |
| Matriculation number | The student's unique identifier. In the MVP, it is case-insensitive and consists of `A`, seven digits, and a final letter. It identifies the student for deletion and for recording or removing attendance. |
| Displayed index | A student's temporary position in the currently displayed list or search results. Profile viewing, deletion, and recording or removing attendance use this index; it is not a permanent identifier. |
| Student profile | The view of one student's particulars, available class memberships, and attendance records. It displays stored memberships but does not assign students to classes. Later versions may also show assignment and grade information. |
| Module | A university course identified by a module code, such as CS2103T. |
| Class / class section | A tutorial or lab group within a module. A class identifier is interpreted together with its module code. |
| Class session | One occurrence of a tutorial or lab, identified by its class code and date. |
| Attendance record | A record that a student was present at a class session; at most one per student, class and date. No record does not mean the student was absent. |
| Assignment | An assessed piece of work with a module-scoped ID, a name, and a maximum mark shared by assigned students. |
| Assignment mark | A student's numeric score for an assigned piece of work. An unrecorded mark is not a zero mark. |
| Grade | An academic result used for comparison, such as an assignment mark. Sorting by grades must identify which assessment result is being compared; marks from different assignments are not interchangeable. |
| Grade trend | The pattern of change in a student's grades over time. |
| Participation | A recorded measure of a student's contribution during a tutorial or lab, distinct from attendance. |
| Attendance statistics | Summaries of recorded attendance for a stated student or class over a stated period. These require the applicable attendance data to be defined and are beyond the MVP's present-on-date records. |
| Filter | A condition used to limit the displayed student list to matching students, such as those in a particular class. Filtering does not delete student records. |
| Command alias | A tutor-defined alternative name for an existing command. |

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample students. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Sorting the displayed students

1. Checking the default order and search reset

   1. With the sample students loaded, add a student whose name comes before the sample names using
      `add n/Aaron Tan m/A1111111A e/aaron@example.com`.<br>
      Expected: Aaron appears first in the displayed list, regardless of when the student was added.

   1. Run `find Yeoh`, followed by `sort`.<br>
      Expected: Only matching students are displayed, in name order.

   1. Run `list`.<br>
      Expected: All students are displayed again in name order, including Aaron.

1. Sorting by another displayed attribute

   1. In a fresh data folder with the original sample students, run `sort matric desc`.<br>
      Expected: Students appear in descending matriculation number order. Roy Balakrishnan appears first.

   1. Run `sort email asc`.<br>
      Expected: Students appear in ascending email order. Alex Yeoh appears first, followed by Roy Balakrishnan.

   1. Run `list`.<br>
      Expected: Students return to alphabetical name order.

1. Sorting by attendance record count

   1. In a fresh data folder with the original sample students, run `sort attendance desc`.<br>
      Expected: Every student card shows `Attendance records: 0`, and students remain in name order.

   1. Run `enroll 6 c/CS2103T-T10` to enroll Roy Balakrishnan, then `attendance m/A6789012U c/CS2103T-T10 d/07-10-2026`.<br>
      Expected: Roy Balakrishnan moves to the first position and shows `Attendance records: 1`.

   1. Run `list`.<br>
      Expected: Students return to name order and the attendance count line disappears.

1. Sorting by one assignment's mark

   1. In a fresh data folder with the original sample students, create an assignment and attach it to two students using `assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20` and `assignment m/A6789012U id/CS2103T:A1`. Record `mark m/A1234567X id/CS2103T:A1 mark/0`.<br>
      Expected: One student has a recorded zero, one is unmarked, and at least one other sample student is not assigned.

   1. Run `sort mark id/CS2103T:A1 desc`.<br>
      Expected: The student with zero appears before the unmarked and unassigned students. Every card shows `CS2103T:A1` with `0/20`, `Unmarked`, or `Not assigned` as appropriate.

   1. Run `sort mark id/CS2103T:A9`.<br>
      Expected: TAssist reports that the assignment does not exist; the displayed order and card details remain unchanged.

   1. Record another mark using `mark m/A6789012U id/CS2103T:A1 mark/15`.<br>
      Expected: The list returns to name order and the assignment result line disappears. Running `sort mark id/CS2103T:A1 desc` again shows the student with 15 first.

### Deleting a student

1. Deleting a student while all students are being shown

   1. Prerequisites: List all students using the `list` command, with multiple students in the list.

   1. Test case: `delete 1`<br>
      Expected: The first student is deleted from the list. The status message shows the deleted student's details.

   1. Test case: `delete 0`<br>
      Expected: No student is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Recording attendance

1. Marking and unmarking attendance

   1. Prerequisites: List all students using the `list` command, then run `enroll 1 c/CS2103T-T10`. The first student has no attendance recorded on 07-10-2026.

   1. Test case: `attendance 1 c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The status message shows `Marked attendance for NAME in CS2103T-T10 on 07-10-2026`, where NAME is the first student's name. `view 1` lists the date and class.

   1. Test case: `attendance unmark 1 c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The status message shows `Unmarked attendance for NAME in CS2103T-T10 on 07-10-2026`. `view 1` no longer lists that record.

   1. Test case: `attendance unmark 1 d/07-10-2026`<br>
      Expected: Nothing changes. The status message shows `No attendance recorded for NAME on 07-10-2026.`

1. Identifying a student outside the displayed list

   1. Prerequisites: Run `list` and `enroll 2 c/CS2103T-T10`. Note the second student's matriculation number (MATRIC) using `view 2`; that student has no attendance recorded on 07-10-2026. Then use `find` with a keyword that matches the first student only, so the second student is not displayed.

   1. Test case: `attendance m/MATRIC c/CS2103T-T10 d/07-10-2026`<br>
      Expected: Attendance is recorded for the second student, although they are not in the displayed list.

   1. Test case: `attendance unmark m/MATRIC c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The record is removed. The status message names the second student.

1. Rejecting a class the student is not enrolled in

   1. Prerequisites: Run `list`. The first student is enrolled in CS2103T-T10 but not CS2103T-T11.

   1. Test case: `attendance 1 c/CS2103T-T11 d/07-10-2026`<br>
      Expected: The status message shows `NAME is not enrolled in CS2103T-T11.` No attendance record is added.

1. Removing one of two classes on the same date

   1. Prerequisites: Run `enroll 1 c/CS2103T-T11`. The first student has no attendance recorded on 07-10-2026.

   1. Test case: `attendance 1 c/CS2103T-T10 d/07-10-2026`, then `attendance 1 c/CS2103T-T11 d/07-10-2026`<br>
      Expected: Both succeed. `view 1` shows `2 records`, with CS2103T-T10 before CS2103T-T11 for that date.

   1. Test case: `attendance unmark 1 d/07-10-2026`<br>
      Expected: The status message shows `NAME has attendance recorded in more than one class on 07-10-2026: CS2103T-T10, CS2103T-T11. Specify the class with c/MODULE-CLASS.` Both records remain.

   1. Test case: `attendance unmark 1 c/CS2103T-T11 d/07-10-2026`, then `attendance unmark 1 d/07-10-2026`<br>
      Expected: The first command removes only the CS2103T-T11 record. The second infers CS2103T-T10 and shows `Unmarked attendance for NAME in CS2103T-T10 on 07-10-2026`. Neither record remains.

1. Marking a whole class

   1. Prerequisites: Close TAssist and back up `data/addressbook.json` before testing. Move the data file out of the `data` folder and reopen TAssist. Run `list`, then `enroll 1 c/CS2103T-T10`, `enroll 2 c/CS2103T-T10`, and `attendance 1 c/CS2103T-T10 d/07-10-2026`.

   1. Test case: `attendance c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The status message shows `Marked attendance for 1 student in CS2103T-T10 on 07-10-2026`, followed on a second line by `Already recorded, skipped: Alex Yeoh`.

   1. Test case: `attendance c/CS2103T-T99`<br>
      Expected: The status message shows `No students are enrolled in CS2103T-T99.` No attendance records change.

### Viewing class attendance statistics

Start in a fresh test folder with the original sample students.

1. Viewing statistics when some students attended
   1. Prerequisites: Run `list` with at least three students. Enroll the first three students in CS2103T-T10 using `enroll 1 c/CS2103T-T10`, `enroll 2 c/CS2103T-T10`, and `enroll 3 c/CS2103T-T10`, skipping any students who are already enrolled. Ensure the students have no attendance records for CS2103T-T10 on 28-10-2026.
   2. Test case: `attendance 1 c/CS2103T-T10 d/28-10-2026`, followed by `attendance 2 c/CS2103T-T10 d/28-10-2026`.\
      Expected: Attendance is recorded for the first two students.
   3. Test case: `attendance stats c/CS2103T-T10 d/28-10-2026`\
      Expected: The result shows 2 students attended. The total equals the number of students currently enrolled in CS2103T-T10, including any students who were already enrolled before testing. No student data is changed.

2. Viewing statistics when no attendance is recorded
   1. Prerequisites: At least one student is enrolled in CS2103T-T10. No student currently enrolled in this class has an attendance record for 30-10-2026.
   2. Test case: `attendance stats c/CS2103T-T10 d/30-10-2026`\
      Expected: The result shows 0 students attended. The total still includes every student currently enrolled in CS2103T-T10.

3. Viewing statistics for a class without enrolled students
   1. Prerequisites: Ensure no students are enrolled in CS2103T-T99.
   2. Test case: `attendance stats c/CS2103T-T99 d/28-10-2026`\
      Expected: The result shows 0 students attended and 0 total students. No error occurs and no student data is changed.

4. Checking that statistics are independent of the displayed list
   1. Prerequisites: Have at least two students enrolled in CS2103T-T10 with attendance recorded on 28-10-2026. Run `attendance stats c/CS2103T-T10 d/28-10-2026` and note both counts.
   2. Test case: Run `find` using a keyword that displays only one of those students. Then run `attendance stats c/CS2103T-T10 d/28-10-2026`.\
      Expected: Both counts are unchanged. Statistics include students who are not currently displayed, and the filtered list remains unchanged.

5. Rejecting invalid or incomplete input
   1. Prerequisites: Run `attendance stats c/CS2103T-T10 d/28-10-2026` and note the result.
   2. Test case: `attendance stats c/CS2103T-T10`\
      Expected: The command is rejected because the date is missing. No data is changed.
   3. Test case: `attendance stats d/28-10-2026`\
      Expected: The command is rejected because the class code is missing.
   4. Test case: `attendance stats c/CS2103T-T10 d/31-02-2026`\
      Expected: The command is rejected because the date is not a valid calendar date.
   5. Test case: `attendance stats c/CS2103T-T10 c/CS2103T-T11 d/28-10-2026`\
      Expected: The repeated class prefix is rejected.
   6. Test case: `attendance stats 1 c/CS2103T-T10 d/28-10-2026`\
      Expected: The command is rejected because statistics are requested for a class, not an individual student.
   7. Run `attendance stats c/CS2103T-T10 d/28-10-2026` again.\
      Expected: The original statistics are unchanged after the invalid commands.

6. Checking different dates and classes
   1. Prerequisites: Enroll the first student in CS2103T-T10 and CS2103T-T11. Ensure the student has no attendance records for those classes on 29-10-2026.
   2. Test case: `attendance 1 c/CS2103T-T11 d/29-10-2026`, followed by `attendance stats c/CS2103T-T10 d/29-10-2026`.\
      Expected: The attendance record for CS2103T-T11 is not counted towards CS2103T-T10.
   3. Test case: `attendance stats c/CS2103T-T11 d/29-10-2026`\
      Expected: The first student's attendance is included in the attended count for CS2103T-T11.
   4. Test case: `attendance stats c/CS2103T-T11 d/30-10-2026`\
      Expected: The attendance recorded on 29-10-2026 is not counted for 30-10-2026.

### Assigning work to a student

1. Add two students with different matriculation numbers, then assign the same work to each.
   1. Test case: `assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20`<br>
      Expected: TAssist confirms the assignment was attached to the first student without a mark.
   1. Test case: `assignment m/A2345678Y id/CS2103T:A1`<br>
      Expected: TAssist confirms the same definition was attached to the second student without a mark.
   1. Repeat either command for the same student, or create `CS2103T:A2` with the name `Assignment One`.<br>
      Expected: TAssist reports the duplicate and preserves the existing assignments.
   1. Enter `assignment m/A1234567X id/CS2103T:A3 n/Extreme max/1e1000000`.<br>
      Expected: TAssist rejects the maximum as too long for display, and does not create `CS2103T:A3`.
   1. Close and reopen TAssist. Enter `marks id/CS2103T:A1`.<br>
      Expected: Both assigned students appear as `Unmarked`, confirming their links were restored.

### Recording an assignment mark

1. After assigning `CS2103T:A1` with maximum `20` to student `A1234567X`, enter `mark m/A1234567X id/CS2103T:A1 mark/0`.<br>
   Expected: TAssist confirms a recorded score of `0/20`.
1. Enter `mark m/A1234567X id/CS2103T:A1 mark/17.5`.<br>
   Expected: TAssist confirms the score was updated to `17.5/20`.
1. Enter the same command with `mark/21`.<br>
   Expected: TAssist rejects the score because it exceeds the maximum. The previous score remains stored after reopening the app.
1. Enter the same command with `mark/1e1000000`.<br>
   Expected: TAssist rejects the score as too long for display. The previous score remains stored.

### Viewing assignment marks

1. After recording marks for `CS2103T:A1`, enter `marks id/CS2103T:A1`.<br>
   Expected: TAssist shows the assignment name and maximum, then every assigned student in matriculation number order. Recorded scores appear as `MARK/MAX_MARK`; students without a score appear as `Unmarked`.
1. Run `find` so some assigned students are hidden, then enter `marks id/CS2103T:A1` again.<br>
   Expected: The marks result still contains all assigned students, and the current student list remains filtered.

### Removing assignments

1. Assign `CS2103T:A1` to two students and record a mark for each. Enter `removeassignment id/CS2103T:A1 m/A1234567X`.<br>
   Expected: TAssist confirms removal from that student. `marks id/CS2103T:A1` still shows the other student's mark, and both student records remain.
1. Enter the same student removal again.<br>
   Expected: TAssist reports that the assignment is not attached to that student, with no change to the other mark.
1. Enter `removeassignment id/CS2103T:A1` and close and reopen TAssist.<br>
   Expected: `marks id/CS2103T:A1` reports an unknown assignment. Both students remain, and the same ID and name can be used to create a new assignment.

### Saving data

1. Dealing with missing/corrupted data files

   1. Close TAssist and back up `data/addressbook.json` before testing.
   1. Move the data file out of the `data` folder and reopen TAssist.<br>
      Expected: Sample student records appear and commands can be executed.
   1. Close TAssist, restore the backup, and make the JSON invalid (e.g., remove its closing brace).
      Reopen TAssist.<br>
      Expected: The result display reports the file path, failure reason, and recovery instructions.
      The command box is disabled. The data file is unchanged after startup and after closing the app
      using the window's close button.
   1. While TAssist is showing the restoration failure, click the command box and attempt to type `list`
      and press Enter.<br>
      Expected: The command box does not accept input or execute a command. The failure message remains
      visible; scroll the result display to read the full recovery instructions if necessary.
   1. Repeat with valid JSON containing an invalid student email, an invalid attendance record date
      (e.g., `31-02-2026`), a duplicate attendance record for the same class and date, a date-only attendance entry
      (e.g., `"attendance": ["07-10-2026"]`), or an assignment mark above its maximum.<br>
      Expected: The same command block and file preservation apply, with the corresponding validation error.
   1. Close TAssist, correct the file or replace it with the valid backup, and reopen TAssist.<br>
      Expected: The saved student and attendance records are restored, the command box is enabled,
      and `list` executes successfully without a restoration failure message.
      Alternatively, delete the file while the app is closed and reopen it to start with sample records.

1. _{ more test cases … }_
