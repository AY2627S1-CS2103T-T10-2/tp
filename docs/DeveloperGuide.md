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


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores shared assignment definitions and student marks in a `Gradebook` owned by `AddressBook`. Gradebook mutations are exposed through the `Model` API, while reads use a snapshot.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* displays the filtered students through a sorted view. Name order is the default; `add`, `find`, and `list` restore it after a sort. `SortCommand` selects a comparator for name, matriculation number, email, recorded attendance count, or one assignment's marks, and may reverse its primary comparison. Equal values use name and matriculation number as tie-breakers. Sorting changes only the view, not the stored student order.
* exposes the active sort's optional card detail through an observable `SortDisplay`. The UI refreshes student cards when this changes. Attendance sorting shows each student's recorded date count; mark sorting shows the selected assignment's result or missing status. Gradebook changes, student edits, and data restoration reset an active mark sort to name order so the saved comparison snapshot does not become stale.
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

`SortDisplay` supplies the optional extra line on each student card. An attendance sort shows the recorded date count. A mark sort shows the assignment ID and each student's mark or missing status. `add`, `find`, and `list` restore name order and remove that line. Assignment, mark, or student detail changes reset an active mark sort so its display does not become stale.

### Attendance feature

The `attendance` command marks a student present on a date, and `attendance unmark` removes that record. Both identify the student by `INDEX` or `m/MATRICULATION_NUMBER` and take an optional `d/DATE`, which defaults to today.

`AttendanceCommandParser` checks the first word of the arguments. If it is `unmark`, in any letter case, the parser builds an `UnmarkAttendanceCommand` from the remaining arguments; otherwise, it builds a `MarkAttendanceCommand` from all of them. One shared helper parses the student identifier and optional date for both commands, then passes them to the chosen command's constructor. This keeps the two forms' argument formats identical without a second parser class.

The simplified sequence diagram below shows a successful run of `attendance unmark m/A1234567X` after parsing, assuming the student exists and has attendance recorded today. It omits date selection and the attendance check, which the steps below explain.

<puml src="diagrams/UnmarkAttendanceSequenceDiagram.puml" alt="Execution of the attendance unmark command" />

1. `StudentIdentifier#resolve` returns the student `s`, found by displayed index or matriculation number.
2. The command uses today's date because none was given. If `s` has no attendance record on that date, it throws a `CommandException` and changes nothing.
3. The command does not modify `s`. It creates a copy `e` whose `Attendance` no longer contains the date, using `Attendance#withoutDate`.
4. `Model#setPerson` replaces `s` with `e`, and the command returns a `CommandResult` `r` with the success message.

Marking follows the same flow, except that the copy gains the date instead of losing it.

### Undo/redo feature

#### Implementation

The undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Each snapshot contains students and an independent copy of the gradebook, including assignment links and marks. History lasts for the current application session and is not persisted. It implements the following operations:

* `VersionedAddressBook#commit()` -- Saves changed address book data in its history and discards any redo branch. If the data matches the current snapshot, it does nothing and preserves redo history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

After successful restoration, `ModelManager` clears the person filter, restores `COMPARATOR_NAME_ASC` and `SortDisplay.NONE`, and increments `addressBookRestoreCount`. `MainWindow` listens to this counter to close student details that could otherwise show stale records. Unavailable undo/redo throws `IllegalStateException` at model level; the commands check availability first and report a `CommandException` without changing the display or counter.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th student in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new student. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** Validation failures that leave records unchanged do not commit history. Persistent changes are committed by the command before `LogicManager` saves the data file. If saving fails, the in-memory change and history remain effective even though the user receives a `CommandException`. Likewise, a successful undo/redo still restores records and notifies the UI if the subsequent save fails.
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

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Assuming the address book is nonempty, this changes records. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. If the address book were already empty, `clear` would make no change and preserve redo history.

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

1. Tutor requests to record a student's attendance, identifying the student by their displayed index or matriculation number, and supplying a date. The matriculation number can identify a student excluded from the current search results.
2. TAssist records the student as present on that date and confirms the student's name and attendance date.

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

* 1d. Attendance has already been recorded for that student on that date.

  * 1d1. TAssist reports the existing record without adding a duplicate.

  Use case ends.

* 1e. The student identifier is missing or malformed, both an index and a matriculation number are supplied, or the matriculation number or date is supplied more than once.

  * 1e1. TAssist explains the invalid input without adding an attendance record.

  Use case resumes at step 1.

* 1f. No stored student has the supplied matriculation number.

  * 1f1. TAssist reports that no student with that matriculation number was found without adding an attendance record.

  Use case resumes at step 1.

Removing an attendance record (unmarking) follows the same interaction, with these differences: in step 2, TAssist removes the student's record for that date instead of adding one; extension 1d does not apply; instead, if no attendance is recorded for that student on that date, TAssist reports this and leaves the student's records unchanged (use case ends).

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
2. TAssist opens or updates a details window showing the selected student's name, matriculation number, email, tags, enrolled classes and attendance dates. Tags are sorted by their text, classes by module and class group, and attendance from most recent to oldest.

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
| Class session | One occurrence of a tutorial or lab. This term is relevant to later class-aware attendance features; the MVP attendance command identifies an attendance record by student and date only. |
| Attendance record | In the MVP, a record that a student was present on a particular calendar date. The same student cannot have two records for the same date. No record does not mean the student was absent. |
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

1. Sorting by recorded attendance count

   1. In a fresh data folder with the original sample students, run `sort attendance desc`.<br>
      Expected: Every student card shows `Attendance dates: 0`, and students remain in name order.

   1. Run `attendance m/A6789012U d/07-10-2026`.<br>
      Expected: Roy Balakrishnan moves to the first position and shows `Attendance dates: 1`.

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

   1. Prerequisites: List all students using the `list` command. The first student has no attendance recorded on 07-10-2026.

   1. Test case: `attendance 1 d/07-10-2026`<br>
      Expected: The status message shows `Marked attendance for NAME on 07-10-2026`, where NAME is the first student's name. `view 1` lists 07-10-2026.

   1. Test case: `attendance unmark 1 d/07-10-2026`<br>
      Expected: The status message shows `Unmarked attendance for NAME on 07-10-2026`. `view 1` no longer lists 07-10-2026.

   1. Test case: repeat `attendance unmark 1 d/07-10-2026`<br>
      Expected: Nothing changes. The status message shows `No attendance recorded for NAME on 07-10-2026.`

1. Identifying a student outside the displayed list

   1. Prerequisites: Note the second student's matriculation number (MATRIC) using `view 2`. Then use `find` with a keyword that matches the first student only, so the second student is not displayed.

   1. Test case: `attendance m/MATRIC d/07-10-2026`<br>
      Expected: Attendance is recorded for the second student, although they are not in the displayed list.

   1. Test case: `attendance unmark m/MATRIC d/07-10-2026`<br>
      Expected: The record is removed. The status message names the second student.

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
   1. Repeat with valid JSON containing an invalid student email, an invalid attendance date
      (e.g., `31-02-2026`), a repeated attendance date, or an assignment mark above its maximum.<br>
      Expected: The same command block and file preservation apply, with the corresponding validation error.
   1. Close TAssist, correct the file or replace it with the valid backup, and reopen TAssist.<br>
      Expected: The saved student and attendance records are restored, the command box is enabled,
      and `list` executes successfully without a restoration failure message.
      Alternatively, delete the file while the app is closed and reopen it to start with sample records.

1. _{ more test cases … }_
