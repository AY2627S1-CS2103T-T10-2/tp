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

* TAssist is adapted from [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) by the SE-EDU initiative. We reuse and extend its application architecture, code, tests, build configuration, documentation and diagrams, including the undo/redo design described in its [Developer Guide](https://se-education.org/addressbook-level3/DeveloperGuide.html).
* Libraries used: [JavaFX](https://openjfx.io/) for the GUI, [Jackson](https://github.com/FasterXML/jackson) for JSON storage, and [JUnit 5](https://github.com/junit-team/junit5) for testing.
* Documentation tools: [MarkBind](https://markbind.org/) for the website and [PlantUML](https://plantuml.com/) for UML diagrams.
* Notable uses of AI:
  * OpenAI Codex was used for coding tasks, including implementing features and fixes, and to suggest design decisions and draft commit messages, issue descriptions, and User Guide and Developer Guide text.
  * Claude Code was used to plan code changes and review code.
  * Claude was used to review code and pull requests, and to draft documentation text.
  * IntelliJ IDEA's built-in AI code completion was used for occasional inline code suggestions.

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
* exposes the active sort's optional card detail through an observable `SortDisplay`. The UI refreshes student cards when this changes. Attendance sorting shows each student's record count; mark sorting shows the selected assignment's result or missing status. Assignment or mark changes, changes to a student's name, matriculation number, email address, or tags, and data restoration reset an active mark sort to name order and clear the assignment result on student cards. Attendance, class enrollment, and participation updates preserve the mark sort and assignment results.
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
* reads and writes `data/addressbook.json` and `preferences.json` in the home folder, i.e., the folder containing the JAR file. When the app is not run from a JAR (e.g., `./gradlew run` or an IDE), the home folder is the working directory instead. `MainApp` resolves both paths using `HomeFolder`, so they do not depend on the folder the app is launched from.
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

Both JSON storage classes read through `JsonUtil`, which deserializes one root value and checks that the parser reaches the end of the input. Trailing whitespace is allowed; additional root values and invalid trailing content are rejected. Unknown fields remain ignored, and missing optional fields retain their existing defaults. This check uses the existing Jackson dependency without an upgrade.

For TAssist's saved records, parsing failures propagate as `DataLoadingException` to `MainApp`, which sets a startup error. `LogicManager` then rejects commands before execution or saving, preserving the original data file through startup, attempted commands, and shutdown. The app displays the existing restoration error and recovery instructions. Preference-loading failures retain their existing default-preferences behavior.

Before reading records or preferences, `MainApp.init()` acquires an exclusive operating-system file lock through `AppInstanceLock` on `.tassist.lock` in the home folder. A second launch from that folder displays a startup error and exits without initializing storage or opening the main window. Failure to create or access the lock also prevents startup. Using the home folder makes this protection independent of the terminal's working directory or the JAR filename.

The lock remains held until shutdown has finished saving preferences. Initialization failures release it, and the operating system releases it if the process terminates unexpectedly. The lock file stays on disk: its existence alone does not indicate a running instance. Do not delete or move it while the app is running, as replacing the file can allow another process to acquire a different lock.

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package. For example, `HomeFolder` locates the home folder that holds the data, preferences and log files.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Assignment mark feature

The `seedu.address.model.gradebook` package defines the data model for assignment marks. `AddressBook` owns a `Gradebook`, and `Model` exposes operations for assigning work, recording marks, and removing links or definitions. JSON storage saves and restores this data. The `assignment` command creates and attaches assignments, `mark` records or updates a student's score, `marks` displays the results for one assignment, `view` includes the selected student's assignments and marks in their profile, and `removeassignment` removes one student's link or the entire shared assignment.

<puml src="diagrams/GradebookDomainDiagram.puml" alt="Assignment mark domain model" />

`AssignmentId` combines a module code and an assignment code, such as `CS2103T:A1`. This keeps `A1` in one module distinct from `A1` in another. `Assignment` holds that ID, a name, and a positive maximum mark. The `Gradebook` rejects duplicate IDs and names within the same module; names are compared without regard to case or repeated spaces. The maximum is defined once for all students assigned that work.

The `Gradebook` separately maps each student's matriculation number and assignment ID to an optional `Mark`. Assigning work creates an unmarked entry. A recorded zero is represented by a `Mark` with value `0`, so it remains distinct from an unmarked entry. `Mark` uses `BigDecimal` to preserve fractional marks without floating-point rounding; the `Gradebook` checks that a recorded value does not exceed the assignment maximum. `Assignment` does not contain a student's mark because the same assignment can have different marks for different students.

`GradebookNumber` checks the length of a normalized value's plain decimal form using its precision and scale, before creating that string. `Assignment` and `Mark` both reject values that would display as more than 64 characters. The check is in the domain constructors, so commands and restored JSON data follow the same limit. Parsers turn this validation failure into a normal input error. This prevents short scientific-notation inputs with enormous exponents from expanding during display or JSON saving.

The helper handles zero first, so even extreme scientific representations of zero normalize to `0`. For nonzero values, it rejects numbers requiring more than 64 integer digits before calling `BigDecimal.stripTrailingZeros()`. The preliminary check uses `long` arithmetic for precision minus scale, preventing overflow both in the check and during subsequent normalization. The remaining values are normalized and checked against the full plain decimal length, including the leading zero and decimal point for fractions. This preserves valid scientific notation and trailing-zero normalization while rejecting inputs such as `100e2147483647` with the existing length validation message.

`AddressBook` coordinates student and gradebook changes. Creating an assignment for a student adds its definition and the student's unmarked entry as one operation. Editing a student's matriculation number moves their assignment entries and marks; editing other details leaves them in place. Deleting a student removes their entries but keeps shared assignment definitions. Replacing the student list removes entries for students no longer present, while resetting or clearing the whole address book replaces the gradebook too. A failed student replacement leaves the original gradebook unchanged. `ReadOnlyAddressBook#getGradebook()` returns an independent snapshot so callers cannot mutate the stored gradebook through a read-only view.

JSON storage keeps shared definitions in `assignments` and each student's link to a definition in `studentAssignments`. An assignment stores its ID, name, and decimal maximum mark. A student assignment stores a matriculation number, assignment ID, and decimal `mark`; a `null` mark means the assignment is unmarked, while `"0"` means a recorded zero. Decimal values are written as strings to preserve exact fractional marks. On restoration, students are loaded first, followed by definitions and student assignments. The storage adapter rejects invalid or duplicate definitions, unknown students or assignments, duplicate student links, and marks outside the allowed range. Files created before these two JSON arrays existed load with an empty gradebook. A failed restoration leaves the saved file unchanged.

`AssignmentCommandParser` accepts one displayed index or matriculation number and an assignment ID, plus a name and maximum mark together when defining new work. `AssignmentCommand` uses `StudentIdentifier` to resolve an index against `Model#getFilteredPersonList()` (including active searches, filters, and sorting), or a matriculation number against all stored students, including hidden students. Both forms require the student to be enrolled in the assignment's module: at least one of the student's class codes must have the same module code as the assignment ID. Module comparison ignores letter case and uses the entire module code. The command checks student existence, an existing definition for the shorter form, and module enrollment before making any changes. For new work, it calls `Model#addAssignmentForStudent`, which validates and publishes the definition and student link as one change. For existing work, it calls `Model#assignStudent` using the stored definition. The gradebook rejects a duplicate ID or name in the module and prevents assigning the same work twice to one student. Both command forms include the stored maximum in their confirmation, using plain decimal notation. Neither command form records a mark.

Module enrollment is a prerequisite for the `assignment` command, rather than a persistent gradebook constraint. Unenrolling from the last class in a module, including through `unenroll INDEX all/`, preserves existing assignment links and marks, and `mark` can still record or update those results. Storage restores historical links without requiring current enrollment. Removing a link and later reassigning the work through `assignment` requires current enrollment again.

`MarkCommandParser` accepts one displayed index or matriculation number, an assignment ID, and a nonnegative decimal score. Both parsers reuse `ParserUtil#parseStudentIdentifier` to reject missing, combined, repeated, or malformed identifiers. `MarkCommand` uses the same `StudentIdentifier` resolution as `assignment`, before reading or mutating the gradebook. Out-of-range indices raise the standard displayed-index error. Rejected commands preserve records and undo/redo history. `MarkCommand` checks that the student, shared definition, and student's assignment link exist before calling `Model#recordMark`. The gradebook rejects scores above the shared maximum before changing any records, with `Mark cannot exceed the maximum mark of MAXIMUM.` The maximum comes from the assignment definition and uses plain decimal notation. `recordMark` returns the previous optional value, allowing the command to distinguish the first recorded mark from an update; a previous zero is present and therefore produces the updated response. The command changes only that student's score. `edit` remains for student particulars. The student profile also displays these marks, as described below.

`parseStudentIdentifier` uses the calling command's usage when an identifier is missing, both forms are supplied, or a nonnumeric token such as bare `A1234567X` is supplied. The nonnumeric-token error explains `INDEX` and `m/MATRICULATION_NUMBER`. Tokens beginning with a digit, sign or decimal point retain index validation; `0`, `-1` and `1.5` report `Index must be a positive integer.`, while oversized positive integers report `The student index provided is invalid.` Whitespace or slashes in the preamble still produce a command-format error with usage; `mat/` remains unsupported. These rules are shared by the other callers of this helper. `parseIndex` itself is unchanged, so index-only commands retain their own guidance.

`AssignmentCommand.MESSAGE_USAGE` and `MarkCommand.MESSAGE_USAGE` contain worked index and matriculation examples. Assignment usage covers both new and existing work. Parser errors append these shared constants. Mark checks missing `id/` and `mark/` prefixes before parsing the student identifier and names the first missing prefix before showing usage.

`MarksCommandParser` accepts one assignment ID. `MarksCommand` obtains a read-only gradebook snapshot and joins that assignment's student marks to current student names by matriculation number. It sorts rows by matriculation number, independently of the filtered and sorted student list, and formats each score against the shared maximum. A missing `Mark` is printed as `Unmarked`, while a recorded zero is printed as `0/MAX_MARK`. An assignment with no students prints `None assigned.`. This command does not change the model or the displayed student list.

`RemoveAssignmentCommandParser` accepts an assignment ID and an optional matriculation number. With a student number, `RemoveAssignmentCommand` checks that the student and assignment exist, then calls `Model#unassignStudent`; the gradebook removes only that student's link and optional mark. Without a student number, it calls `Model#removeAssignment`, which removes the definition and all links and marks under that ID. Both operations leave `Person` objects and unrelated gradebook entries untouched. Removing a definition releases its ID and name for reuse. The changed address book is saved through the usual command flow, so the removal persists after reopening the app. There is no archive or separate assignment edit operation.

### Student profile assignment marks

`ViewCommand` resolves the displayed index against `Model#getFilteredPersonList()` and returns the selected `Person` in `CommandResult`. `MainWindow` constructs one reusable `StudentDetailsWindow` with the full observable student list and a `Supplier<ReadOnlyGradebook>` bound to `logic::getGradebook`, then passes the selected person to `showStudent(Person)`. `LogicManager` delegates the getter to `ReadOnlyAddressBook#getGradebook()`. The supplier obtains a fresh snapshot for each refresh without exposing the stored gradebook to mutation.

`StudentDetailsWindow#formatAssignmentRows` looks up assignments by the selected person's matriculation number using `ReadOnlyGradebook#getMarksForStudent()`. It sorts entries by the string form of the assignment ID, resolves each shared definition, and formats the ID, name, maximum mark, and optional score. Recorded scores use plain decimal notation as `Mark: SCORE/MAXIMUM`; a zero remains a recorded score, while an empty mark displays `Mark: Unmarked`. Unassigned definitions and other students' marks are excluded.

The `assignments` container in `StudentDetailsWindow.fxml` holds the rows under Assignments and marks. Each refresh clears the previous rows before displaying the selected student's results or `None assigned`. Labels wrap within the existing scrollable window. `showStudent` explicitly refreshes even when selecting the same `Person`, because an unchanged selection does not fire a property-change event. After other commands execute and save successfully, `MainWindow` calls `refreshIfShowing()` on an existing window. This immediately reflects `assignment`, `mark`, `removeassignment` without another `view` command. The refresh only reads the current model snapshot and updates UI controls; it does not alter student data, assignment links, marks, or history.

`StudentDetailsWindowTest` covers empty assignments, ordering, student isolation, decimal and zero scores, unmarked assignments, and updated or removed entries. Regression cases exercise repeated views, matriculation migration, rapid edits, switching students while a refresh is pending, closing, deletion, undo/redo, and preservation of model data. `mainWindow_assignmentCommands_refreshOpenDetailsWithoutAnotherView` enters commands through the actual `MainWindow` command box and verifies immediate mark, assignment, and removal updates, saved data, and a closed window staying closed. The tests initialize JavaFX and perform window operations on its application thread; Linux CI runs under Xvfb to provide a display. `LogicManagerTest#getGradebook_afterMarkChanges_returnsFreshSnapshot` verifies that a later request reflects a mark update while the earlier snapshot stays unchanged.



### Student profile participation history

`StudentDetailsWindow` displays the selected student's participation history in a separate Participation section below Attendance.

The window reads the records directly from `Person#getParticipationHistory().getRecords()`. It does not require a separate data source or storage mechanism.

`StudentDetailsWindow#updateParticipation` clears the previous entries and rebuilds the section using the selected student's participation records. Records are sorted by class code and then by date in ascending order. Each class code is displayed as a heading, followed by its participation dates and points. A score of 1 is displayed as 1 point, while other scores use points. If the history is empty, the section displays `None recorded`.

The `participationRecords` container in `StudentDetailsWindow.fxml` holds these entries below the Attendance section. Each refresh rebuilds the displayed records from the selected `Person`'s current participation history without modifying the stored records.

`StudentDetailsWindowTest` covers empty histories, multiple classes, date ordering and point formatting. The tests also verify that participation records are refreshed when points are added, updated or removed through the `MainWindow` command box without another `view` command.

### Sorting displayed students

The `sort` command accepts `sort`, `sort FIELD [asc|desc]`, or `sort mark id/MODULE:CODE [asc|desc]`. `SortCommandParser` ignores letter case for field and direction keywords, and reports the assignment ID constraints for an invalid ID. `SortCommand` sorts the displayed students without changing their stored order. Name and email comparisons ignore case, and an empty attendance history counts as zero. Mark sorting compares assignment status first: recorded marks, then `Unmarked`, then `Not assigned`, in both directions. Snapshot map key presence distinguishes assigned students with an empty mark from unassigned students. Only recorded numeric marks use the requested direction. Equal marks and students within each missing-mark group use `Model.COMPARATOR_NAME_ASC`: case-insensitive name, then matriculation number, both ascending.

`SortDisplay` supplies the optional extra line on each student card. An attendance sort shows the record count. A mark sort shows the assignment ID and each student's assignment mark, `Unmarked`, or `Not assigned`. `add`, `find`, and `list` restore name order and remove that line.

The mark comparator and card details capture a gradebook snapshot. Assignment and mark changes invalidate that snapshot; changing a matriculation number also invalidates it because the student's gradebook entries move to a new key. After a successful replacement, `ModelManager#setPerson` resets an active mark sort only if the student's name, matriculation number, email address, or tags changed. Updates affecting only attendance, class enrollment, or participation history preserve the comparator, `SortDisplay`, and displayed list's filter because they do not change the snapshot's marks or matriculation-number keys. Attendance sorting continues to use the updated attendance count. Index-based commands resolve the displayed index against the current displayed list.

### Keeping the student details window up to date

`ViewCommand` returns the selected `Person` in its `CommandResult`. `MainWindow` creates one reusable `StudentDetailsWindow`, passing it `Logic#getPersonList()` and the gradebook supplier described above, and calls `showStudent` to open or focus it. This list is an unmodifiable observable view of all students from `Model#getAddressBook()`, independent of the filtered and sorted list used by `PersonListPanel`.

`StudentDetailsSelection` observes this full list and exposes the selected student through a read-only object property. Students are immutable: an edit, enrollment change, or attendance change replaces the old `Person` with a new one. When a list change replaces the selected student with one new student, the selection follows that replacement. This also handles edits to the matriculation number, without trying to look up the student by their old number or displayed index. Removing the selected student, including through `clear`, clears the selection. Changes to other students leave it unchanged.

`StudentDetailsWindow` listens to the selection property. A replacement schedules a refresh with `Platform.runLater` rather than fetching the gradebook synchronously in the listener. `AddressBook#setPerson` publishes the replacement `Person` before installing the gradebook with migrated matriculation-number keys. Waiting until the current model operation finishes lets `refreshSelectedStudent()` read the replacement student and the migrated assignments together. `updateStudent` then refreshes particulars, classes, assignments, attendance, participation, and the window title.

Each queued refresh captures `refreshVersion` and checks it again before rendering. A newer edit, explicit view, completed-command refresh, deletion, or window close invalidates older work. `refreshIfShowing()` also checks visibility, so it neither reopens a hidden window nor takes keyboard focus from the main window. An empty selection hides the window; closing it, including with `Esc`, clears the selection. Successful undo/redo closes it through the existing restoration-counter listener. Filtering and sorting preserve the selected student, and changes to other students do not switch the profile.

`StudentDetailsSelectionTest` covers replacements, matriculation number edits followed by deletion, attendance and class changes, clearing, changes to other students, switching the selected student, and updates while the student is hidden by a filter. `LogicManagerTest` checks that the full list remains observable and cannot be modified through the `Logic` API.

### Attendance feature

The `attendance` command marks a student or a whole class present at a class session, and `attendance unmark` removes an individual student's record. Each `AttendanceRecord` contains an `AttendanceDate` and a `ClassCode`; the same student may have records for different classes on one date, but not two records for the same class and date. Single-student mark and unmark requests identify the student by `INDEX` or `m/MATRICULATION_NUMBER`. All forms take an optional `d/DATE`, which defaults to today. Mark requires `c/MODULE-CLASS`; unmark accepts an optional `[c/MODULE-CLASS]`.

`MarkAttendanceCommand`, `MarkClassAttendanceCommand` and `UnmarkAttendanceCommand` extend the abstract `AttendanceCommand`, which holds the command word, the optional date (defaulting to today), equality that requires the same command type and date, and the helper that copies a student with new attendance.

`AttendanceCommandParser` checks the first word of the arguments. If it is `unmark` or `stats`, in any letter case, it calls `parseUnmark` or `parseStats` respectively with the remaining arguments; otherwise, it tokenizes the arguments before choosing the mark form. The mark and unmark paths use `tokenizeAttendanceArguments` to tokenize `m/`, `c/` and `d/` and reject duplicate prefixes. Single-student requests then parse the student identifier, class code and optional date, in that order, before constructing their command. `parseMark` requires a class code, while `parseUnmark` leaves it optional. Duplicate-prefix errors take priority over identifier errors, then class code errors, then date errors. After date parsing, `parseMark`, `parseMarkClass` and `parseUnmark` reject a date later than today. The parser reads today's date from `AttendanceDate.today()` when constructed, and tests pass in a fixed date. `parseStats` does not apply this check, and neither does loading the data file, so future-dated records already saved still load.

For marking, an empty preamble with no `m/` and a present `c/` selects `parseMarkClass`, which parses the class code and optional date and constructs `MarkClassAttendanceCommand`; all other arguments go to `parseMark`. The whole-class command collects enrolled students from all students in TAssist, including those outside the displayed list. It reports an error without changing anything if none are enrolled. Otherwise, it skips students already recorded for that class and date, reuses `AttendanceCommand#createStudentWithAttendance` to create each updated student, and calls `Model#setPerson` once per marked student. The result reports the marked count and lists any skipped students by name and matriculation number.

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


### Class participation

The participation feature records a student's points for a class on a
particular date. Participation is stored separately from attendance.

#### Data model

`ParticipationPoints` represents a non-negative whole-number score.

`ParticipationRecord` contains a class code, date and score. Two records
refer to the same session when their class and date match. Their full
equality comparison also includes the score.

Each `Person` owns an immutable `ParticipationHistory`. The history
contains at most one record per class and date. Adding a record for an
existing session replaces its score rather than adding the scores or
creating another record.

History updates return a new history. `Person.withParticipationHistory`
creates an updated student while preserving the other fields.

#### Command execution

`AddressBookParser` delegates participation arguments to
`ParticipationCommandParser`, which selects the set, view or remove
operation.

The parser reuses `ParserUtil` for student identifiers, class codes and
calendar-date validation. Points are validated by `ParticipationPoints`.
Missing, repeated and invalid arguments are rejected before command
execution.

`ParticipationCommand` resolves the student through `StudentIdentifier`,
checks enrollment, creates the updated history and replaces the student
through the model.

`ViewParticipationCommand` sorts a copy of the records for display without
changing the stored history.

`RemoveParticipationCommand` checks that the requested record exists
before updating the student. Viewing and removing historical records do
not require current enrollment.

#### Persistence and preservation

`JsonAdaptedPerson` stores participation records through
`JsonAdaptedParticipationRecord`. Participation dates are stored in `dd-MM-yyyy` format.

A missing or null participation list is read as an empty history for
compatibility with older student files. Null records, invalid record
fields and duplicate class-date sessions are rejected.

The student-copy paths used for basic edits, attendance marking and
enrollment changes preserve the participation history.

#### Design decisions

* A score replaces the previous value for the same student, class and date.
  Repeating a command therefore does not accidentally accumulate points.
* Zero points and an absent record have different meanings. Removing a
  record is a separate operation.
* Participation does not automatically change attendance.
* Class and date identify a session; multiple sessions of the same class
  on one date are outside this version's scope.
* Scores have no configurable module-specific maximum. The supported
  whole-number range is determined by the integer representation.


### Searching and filtering students

`find` and `filter` narrow the displayed student list independently. A student is displayed only if they satisfy both the active class/module filter set by `filter` and the active search set by `find`. Neither command overwrites the other's criteria, so they can be run in either order.

`ModelManager` stores each layer in its own field, both defaulting to `PREDICATE_SHOW_ALL_PERSONS`:

* `filterPredicate` is set by `FilterCommand` through `Model#updateFilterPredicate` to a `PersonInModuleOrClassPredicate`.
* `findPredicate` is set by `FindCommand` through `Model#updateFindPredicate` to a `FindPredicate`: a `NameContainsKeywordsPredicate` for `find KEYWORD...` or a `MatriculationNumberMatchesPredicate` for `find m/MATRICULATION_NUMBER`.

Whenever either field changes, `applyPredicates()` calls `filteredPersons.setPredicate(filterPredicate.and(findPredicate))`. The sorted view that the UI binds to wraps this filtered list, so the UI updates without further work.

How each command affects the layers:

| Command | `filterPredicate` | `findPredicate` |
|---|---|---|
| `filter KEYWORD...` | replaced | kept |
| `find KEYWORD...` | kept | replaced |
| `find m/MATRICULATION_NUMBER` | cleared | replaced |
| `list`, `add`, `edit`, successful `undo`/`redo` | cleared | cleared |

`find` resets the sort to name order, as described in [Sorting displayed students](#sorting-displayed-students). `filter` does not change the sort.

The sequence diagram below shows `find John` while a class/module filter is active. `FindCommand` replaces only `findPredicate`, so the filter still applies.

<puml src="diagrams/FindFilterLayeringSequenceDiagram.puml" alt="FindFilterLayeringSequenceDiagram" />

`find m/MATRICULATION_NUMBER` is the one case that ignores the filter. A matriculation number identifies exactly one student, and tutors use it to look up a student who is hidden by the current view, as `attendance m/`, `delete m/` and `enroll m/` also do. `FindCommand` does not check the predicate's type. It asks `FindPredicate#ignoresActiveFilter()`, which returns `true` only for `MatriculationNumberMatchesPredicate`, and resets both layers first when the answer is `true`. A future predicate can opt in or out by implementing this method, without changing `FindCommand`.

<puml src="diagrams/FindActivityDiagram.puml" alt="FindActivityDiagram" width="300" />

An active `find` can make a new `filter` return no students, so `FilterCommand` reports a dedicated message in that case telling the user to run `list` to clear the search.

#### Design considerations

**Aspect: How `find` and `filter` combine:**

* **Alternative 1 (current choice):** Store `filterPredicate` and `findPredicate` separately and combine them with `Predicate#and`.
  * Pros: Each command replaces only its own layer, so the result does not depend on the order of the commands. `list`, `edit` and `undo` can clear everything with one reset.
  * Cons: `ModelManager` holds two display-state fields, and a third kind of criterion would need a new field.

* **Alternative 2:** `find` searches only the currently displayed list.
  * Pros: Needs no extra state.
  * Cons: Repeated `find` commands keep narrowing the list, so a new `find` cannot replace the previous one, and the result depends on the history of commands. The user must run `list` to widen it again.

* **Alternative 3:** Keep a list of active predicates and combine them all.
  * Pros: Supports any number of criteria.
  * Cons: Each command needs a way to find and replace its own entry. This is more complex than two criteria need.


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

Stories describe current capabilities unless marked **Future**. Future stories are retained as requirements for later iterations and do not describe functionality available in this release.

| Priority | As a …                                   | I want to …                                                  | So that I can…                                                  |
|----------|------------------------------------------|--------------------------------------------------------------|-----------------------------------------------------------------|
| `* * *`  | tutor                                    | add a student                                                | keep track of the students in my classes                        |
| `* * *`  | tutor                                    | delete a student                                             | remove students who are no longer in my classes                 |
| `* * *`  | tutor                                    | edit a student's details                                     | keep their information up to date without re-adding them        |
| `* * *`  | tutor                                    | list all students                                            | see everyone under my care                                      |
| `* * *`  | tutor                                    | search for a student by name                                 | find their information quickly                                  |
| `* * *`  | tutor                                    | view a student's profile                                     | review their particulars, classes, assignment marks and attendance in one place   |
| `* * *`  | tutor teaching multiple modules          | assign students to modules and class sections                | organize the classes I teach                                    |
| `* * *`  | tutor teaching multiple modules          | filter students by class                                     | see all students from one class                                 |
| `* * *`  | tutor                                    | record a student's attendance for a specific class session   | keep track of whether they attended class                       |
| `* * *`  | tutor                                    | record a student's assignment marks                          | keep track of their academic performance                        |
| `* * *`  | tutor                                    | sort my class by grades in ascending or descending order     | identify struggling students quickly                            |
| `* * *`  | tutor                                    | have my student records restored when I reopen the app       | continue working without entering them again                    |
| `* *`    | new user                                 | see a list of commands and how to use them                   | start using the app without memorizing commands                 |
| `* *`    | forgetful tutor                          | search for a student by matriculation number                 | find them when I cannot recall their name                       |
| `* *`    | tutor                                    | update an attendance record                                  | correct mistakes made during attendance taking                  |
| `* *`    | tutor                                    | mark a whole class present for a session                     | record attendance quickly and then unmark absentees             |
| `* *`    | tutor                                    | view attendance statistics for a class                       | monitor participation trends                                    |
| `* *`    | tutor                                    | record a student's class participation                       | grade their participation                                       |
| `* *`    | tutor                                    | undo my last action                                          | reverse mistakes                                                |
| `* *`    | tutor                                    | redo an undone action                                        | recover from an accidental undo                                 |
| `* *`    | tutor                                    | have my filters persist across searches                      | avoid retyping the same filters                                 |
| `*`      | tutor who has finished teaching a class  | delete students in batches                                   | remove a whole class at once                                    |
| `*`      | tutor teaching a new class               | **Future:** import students from a file with one command | avoid adding them one by one                                    |
| `*`      | tutor                                    | **Future:** add notes to a student | remember details about them                                     |
| `*`      | tutor                                    | **Future:** color-code student entries by conditions such as class | tell my classes apart at a glance                               |
| `*`      | tutor                                    | **Future:** see a student's photo | recognize them in class                                         |
| `*`      | tutor                                    | **Future:** view a student's grade trend | see their progress over time                                    |
| `*`      | tutor                                    | **Future:** record which students are friends                            | ask friends to help students who are struggling                 |
| `*`      | tutor managing at-risk students          | **Future:** pin a student entry | find them quickly for special attention                         |
| `*`      | tutor                                    | **Future:** open my mail app with a student's email filled in | email students without copying their addresses                  |
| `*`      | tutor teaching many modules              | **Future:** autocomplete course codes | avoid memorizing module identifiers                             |
| `*`      | tutor                                    | **Future:** view previously executed commands | reuse commands without retyping them                            |
| `*`      | tutor                                    | **Future:** view my search history | repeat past searches easily                                     |
| `*`      | tutor                                    | **Future:** clear my search history | keep only relevant entries                                      |
| `*`      | experienced user                         | **Future:** set custom command aliases | type fewer characters                                           |
| `*`      | experienced user                         | **Future:** customize the app's appearance | match it to my other applications                               |
| `* * *` | tutor | define work and assign it to students enrolled in its module | track who should complete each assignment |
| `* *` | tutor | view all students' marks for one assignment | compare results and identify unmarked work |
| `* *` | tutor | remove an assignment from one student or everyone | correct assignment allocation mistakes |
| `* *` | tutor | unenroll a student from one class or all classes | keep enrollment current while retaining historical records |
| `* *` | tutor | view and correct participation records | review awarded points and remove incorrect entries |
| `* *` | tutor | clear all student and assignment records | start with an empty dataset |
| `* *` | tutor | sort students by name, matriculation number, email or attendance count | locate and compare students using the relevant field |
| `*` | tutor | **Future:** archive old records | retain past teaching records separately from current work |

### Use cases

These use cases describe the current program's behavior. For all use cases, the **System** is TAssist, the **Actor** is a university Computer Science tutor, and **MSS** means Main Success Scenario. Unless stated otherwise, TAssist is running and startup has completed without a restoration error. Restoration failures are covered by UC07.

**Common extension (UC01–UC06, UC08–UC17):** If TAssist cannot save the records after processing a request, it reports the save failure instead of confirming success. Any changes already made to student information, assignment marks, or the displayed list remain. The main success scenarios assume saving succeeds.

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

1. Tutor requests to delete one or more students using their displayed indices, or one student using their matriculation number. The matriculation number can identify a student excluded from the current search results.
2. TAssist deletes the selected students from TAssist and confirms the deletion. All indices refer to the list before deletion; if any index is invalid or repeated, no students are deleted.

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

Name matching considers students satisfying any active class/module filter and ignores case. A new name search replaces the previous name search while keeping that filter. A student matches if any supplied keyword occurs within a word in their name. Each matching student appears once, including distinct students with the same name. Non-empty keywords are accepted without student-name validation.

**Extensions**

* 1a. Tutor supplies no search keywords.

  * 1a1. TAssist reports an input error and explains the expected input. The displayed list and student records remain unchanged.

  Use case resumes at step 1.

* 2a. No students match any of the keywords.

  * 2a1. TAssist reports zero matches and displays an empty result list.

  Use case ends.

* 1b. Tutor supplies one complete matriculation number instead of name keywords.

  * 1b1. TAssist clears the previous search and class/module filter, then searches all stored students for that exact number, ignoring case. It shows the matching student, or an empty list if none matches.

  Use case ends.

* 1c. Tutor combines name keywords with a matriculation number, repeats the matriculation prefix, or supplies an invalid number.

  * 1c1. TAssist rejects the request without changing the displayed list or records.

  Use case resumes at step 1.

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

* 1i. The supplied date is later than today.

  * 1i1. TAssist reports that the date is in the future without adding an attendance record.

  Use case resumes at step 1.

Removing an attendance record (unmarking) follows the same interaction, with these differences: in step 2, TAssist removes the student's record for the given class and date instead of adding one. The class is optional when exactly one record exists on that date. With several records on that date and no class supplied, TAssist reports an error naming the classes and asks the tutor to specify one; no records change (use case resumes at step 1). Extensions 1d and 1h do not apply, and 1g applies only to an invalid supplied class: the record's class need not be a current enrollment. If no record matches the given class and date, or no records exist on that date when the class is omitted, TAssist reports this and leaves the student's records unchanged (use case ends). Success confirms the removed record's class even when inferred. A date later than today is rejected and nothing changes.

For whole-class marking, the tutor supplies a class and an optional date without a student identifier. TAssist marks every enrolled student present for that session and reports how many students were marked, naming any students already recorded for that class and date who were skipped, each with their matriculation number. If no students are enrolled in the class, TAssist reports an error and changes nothing. A date later than today is rejected and nothing changes.

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
2. TAssist opens or updates a details window showing the selected student's name, matriculation number, email, tags, enrolled classes, assignment marks, and attendance records with their dates and classes. Tags are sorted by their text, classes by module and class group, assignments by assignment ID, and attendance by date from most recent to oldest, then by class code in ascending order within a date. Each assignment shows its ID, name, maximum mark, and recorded score or `Unmarked`. Only assignments attached to the selected student are shown.

While the window remains open, successful changes to the selected student's particulars, classes, attendance, assignments, or marks refresh its contents automatically without another `view` command. A matriculation-number edit preserves the displayed assignments and marks. Repeating `view` for the same student also refreshes all sections. Deleting that student, clearing all students, or successfully undoing or redoing a change closes the window. Searching, filtering, and sorting preserve the selected profile, even if the student is hidden from the displayed list.

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

* 2a. The student has no tags, enrolled classes, assignments, or recorded attendance.

  * 2a1. TAssist shows `No tags`, `No classes enrolled`, `None assigned`, or `None recorded` in the corresponding section, alongside the student's other details.

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

1. Tutor requests to enroll a student in a class, identifying the student by displayed index or matriculation number and supplying one class code that includes the module and group.
2. TAssist enrolls the student in that class and confirms the enrollment.

Use case ends.

**Extensions**

* 1a. The module code or class code is invalid.

    * 1a1. TAssist will specify the format of the module and class code.

    Use case ends.

* 1b. The index is not a positive integer or is outside the range of the displayed student list, or the matriculation number is invalid or matches no student.

    * 1b1. TAssist shows an error message.

    Use case ends.

* 1c. The student is already enrolled in that class.

    * 1c1. TAssist shows an error message and does not change the student.

    Use case ends.

* 1d. The tutor supplies both an index and a matriculation number, or neither.

    * 1d1. TAssist shows an error message and does not change any student.

    Use case ends.

#### UC09: Assign work to a student

**MSS**

1. Tutor supplies a stored student's displayed index or matriculation number, a new assignment ID, its name, and its maximum mark.
2. TAssist creates the shared assignment definition, attaches it to that student without a recorded mark, and confirms the assignment alongside its maximum mark.

Use case ends.

**Extensions**

* 1a. Tutor supplies only a student's displayed index or matriculation number and an existing assignment ID.

  * 1a1. TAssist attaches that definition to the student without changing its name or maximum mark.

  Use case resumes at step 2.

* 1b. The student does not exist or is not enrolled in the assignment's module, the new assignment ID or name already exists in its module, or the existing assignment is already attached to the student.

  * 1b1. TAssist reports the problem and changes no records.

  Use case ends.

* 1c. The student identifier is missing, malformed, combines both forms, or is an out-of-range displayed index; the maximum mark is not positive, or the shorter form names an unknown assignment ID.

  * 1c1. TAssist explains the problem and changes no records.

  Use case ends.

#### UC10: Record or update an assignment mark

**MSS**

1. Tutor supplies a stored student's displayed index or matriculation number, an assignment ID already attached to that student, and a score from zero through that assignment's maximum.
2. TAssist records the score and confirms it alongside the assignment maximum.

Use case ends.

**Extensions**

* 1a. The student or assignment does not exist, the student has not been assigned that work, or the score exceeds its maximum.

  * 1a1. TAssist reports the problem and leaves the records unchanged. For an over-maximum score, the error includes the assignment's actual maximum in plain decimal notation.

  Use case ends.

* 1b. The student identifier is missing, malformed, combines both forms, or is an out-of-range displayed index; or the score is not a nonnegative number.

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

With no field or direction supplied, TAssist uses name ascending. Mark sorting places students with recorded marks first, then `Unmarked` students, then `Not assigned` students in either direction. Equal values and students within each missing-mark group are ordered by case-insensitive name, then matriculation number, both ascending. Sorting changes the displayed order, not the stored student records.

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

1. Tutor requests to unenroll a student, identifying the student by displayed index or matriculation number and supplying one class code that includes the module and group.
2. TAssist removes the student's enrollment in that class and confirms the removal.

Use case ends.

**Extensions**

* 1a. Tutor omits the class code but indicates unenrollment from all classes, and the student is enrolled in at least one class.

  * 1a1. TAssist removes all the student's class enrollments and confirms the removal.

  Use case ends.

* 1b. The input is invalid. This includes any of the following: the student identifier is missing or malformed, the class code is invalid, more than one class code is supplied, neither a class code nor unenrollment from all classes is indicated, or unenrollment from all classes is indicated more than once.

  * 1b1. TAssist explains the invalid input and leaves the student's enrollments unchanged.

  Use case resumes at step 1.

* 1c. The index exceeds the size of the currently displayed list (including when the list is empty).

  * 1c1. TAssist reports that the displayed student index is invalid and changes no records.

  Use case resumes at step 1.

* 1d. The matriculation number matches no stored student.

  * 1d1. TAssist reports that no student with that matriculation number was found and changes no records.

  Use case resumes at step 1.

* 1e. The student is not enrolled in the specified class.

  * 1e1. TAssist reports this and leaves the student's enrollments unchanged.

  Use case ends.

* 1f. Tutor omits the class code but indicates unenrollment from all classes, and the student is not enrolled in any classes.

  * 1f1. TAssist reports that the student has no class enrollments to remove and changes no records.

  Use case ends.

* 1g. The tutor supplies both an index and a matriculation number.

  * 1g1. TAssist shows an error message and changes no records.

  Use case resumes at step 1.

### UC15: Record class participation points

**Actor:** Tutor

**Main success scenario**

1. The tutor requests to record participation points for a student,
   specifying the class, date and score.
2. TAssist records the score, replacing the previous score for the same
   class and date if one exists, and confirms the update.

Use case ends.

**Extensions**

1a. Required input is missing, repeated or invalid.

    1a1. TAssist explains the input error without changing student data.

    Use case resumes at step 1.

1b. The student cannot be found or is not enrolled in the specified class.

    1b1. TAssist explains the problem without changing student data.

    Use case resumes at step 1.

1c. The specified participation date is in the future.

    1c1. TAssist displays an error explaining that participation points cannot be recorded for a future date.

    Use case resumes at step 1.

### UC16: View attendance statistics for a class

**Actor:** Tutor

**Main success scenario**

1. The tutor requests attendance statistics for a class on a specified date.
2. TAssist finds all students currently enrolled in that class.
3. TAssist counts enrolled students with an attendance record for that class and date.
4. TAssist displays the attended count and total enrolled count.

Use case ends.

**Extensions**

1a. The class code or date is missing or invalid.

    1a1. TAssist displays an error message explaining the required input.

    Use case resumes at step 1.

2a. No students are currently enrolled in the class.

    2a1. TAssist displays 0 students attended and 0 total students.

    Use case ends.

#### UC17: Filter students by class or module

**MSS**

1. Tutor supplies one or more complete class or module codes.
2. TAssist replaces the previous class/module filter and shows students enrolled in any supplied class or module, also satisfying any active search.

Use case ends. Codes ignore case. A later name search retains this filter; `list` clears both, while a matriculation-number search clears the previous filter and search before looking up the number.

**Extensions**

* 1a. Codes are missing or malformed. TAssist reports an input error and preserves the displayed list and records. Use case resumes at step 1.
* 2a. No student satisfies the combined criteria. TAssist displays an empty list and explains that an active search may restrict the results. Use case ends.

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
| Student profile | The view of one student's particulars, available class memberships, assignment marks, attendance records, and participation history. It displays stored memberships but does not assign students to classes. |
| Module | A university course identified by a module code, such as CS2103T. |
| Class / class section | A tutorial or lab group within a module. A class identifier is interpreted together with its module code. |
| Class session | One occurrence of a tutorial or lab, identified by its class code and date. |
| Attendance record | A record that a student was present at a class session; at most one per student, class and date. No record does not mean the student was absent. |
| Assignment | An assessed piece of work with a module-scoped ID, a name, and a maximum mark shared by assigned students. |
| Assignment mark | A student's numeric score for an assigned piece of work. An unrecorded mark is not a zero mark. |
| Grade | An academic result used for comparison, such as an assignment mark. Sorting by grades must identify which assessment result is being compared; marks from different assignments are not interchangeable. |
| Grade trend | The pattern of change in a student's grades over time. |
| Participation | A recorded measure of a student's contribution during a tutorial or lab, distinct from attendance. |
| Attendance statistics | For one class session, the number of currently enrolled students with an attendance record for that class and date, and the total number of students currently enrolled in the class. |
| Filter | A condition used to limit the displayed student list to matching students, such as those in a particular class. Filtering does not delete student records. |
| Command alias | A tutor-defined alternative name for an existing command. |

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

Fresh folders load the original six identities with `@u.nus.edu` emails. Alex, Bernice and Charlotte are enrolled in CS2100-T01; David, Irfan and Roy in CS2040S-T02. Each has attendance on 01-09-2026 and 08-09-2026 and 2 participation points on 08-09-2026 in that class. Each is assigned their module's QUIZ1 (Week 4 Quiz, maximum 20), marked 17.5 except Roy, whose work is unmarked. These modules and dates are separate from the exercises below. Existing saved files, including empty datasets, are loaded unchanged.

Commands to enter are formatted like `redo`; messages and text displayed by TAssist are formatted like <samp>Redo successful.</samp>. In output samples, NAME represents the student's name and MARK/MAX_MARK represents the recorded mark and assignment maximum.

Invalid-input tables collect the rejected inputs for each section, including commands that are valid in form but cannot be performed in the current state. Run each case separately at the point indicated in the steps, so its prerequisites are satisfied. Case labels use section-specific prefixes, such as STUDENT-1, and are unique across this appendix.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder where you can create and update files. Use a separate test folder to protect your real records.

   1. Open a terminal and `cd` to that folder. Run `java -version` and check that Java 25 is selected. Run `java -jar "addressbook.jar"`, substituting the actual downloaded filename if different.<br>
      Expected: The GUI opens with a set of sample students. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app with the same terminal command from the same folder.<br>
       Expected: The most recent window size and location are retained.

1. Launching from another folder

   1. In the test folder's app, run `add n/Launch Test m/A9900164Z e/launch@example.com`, then close the app.

   1. Open a terminal in a different, empty folder and launch the same JAR using its full path, e.g., `java -jar "/path/to/test-folder/addressbook.jar"`.<br>
      Expected: Launch Test appears with the sample students, the window keeps the size and location from the previous test, and the status bar shows the full path of `data/addressbook.json` in the test folder. The folder you launched from stays empty.

1. Preventing simultaneous launches

   1. In a fresh test folder, launch TAssist and run `add n/Instance Test m/A9900165Z e/instance@example.com`.<br>
      Expected: The student is added successfully.

   1. Keep that window open. From a second terminal in another folder, launch the same JAR using its full path.<br>
      Expected: A startup error explains that TAssist is already running. No second main window opens. Dismiss the error.

   1. In the original window, run `list`, then close TAssist. Relaunch it from the same home folder.<br>
      Expected: Instance Test remains in the saved records. The `.tassist.lock` file remaining in the home folder does not prevent reopening.

1. Reopening after unexpected termination

   1. With TAssist running in the separate test folder, terminate its Java process using the operating system's process manager. Take care to select only the test instance.

   1. Relaunch the same JAR without removing `.tassist.lock`.<br>
      Expected: TAssist opens and restores the previously saved records; the terminated process no longer holds the lock.


### Adding and editing students

Start in a fresh test folder with the original sample students.

1. Run `add n/Zoe Test m/A9900001Z e/zoe@example.com t/tutorial`.<br>
   Expected: Zoe appears after the six sample students in name order, with the supplied details.
1. Run case STUDENT-1 in the invalid-input table below.
1. Run `find m/A9900001Z`, then `edit 1 n/Zoe Updated e/zoe.updated@example.com t/`.<br>
   Expected: All seven students are displayed in name order.
1. Run `find m/A9900001Z` again, then `view 1`.<br>
   Expected: The name and email change and all tags are cleared. The matriculation number remains A9900001Z.
1. Run cases STUDENT-2 and STUDENT-3 in the invalid-input table below.
1. Close and reopen the app from the same folder. Run `find m/A9900001Z` and `view 1`.<br>
   Expected: Zoe's updated name, email and empty tags are retained.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| STUDENT-1 | `add n/Another Name m/A9900001Z e/zoe@example.com t/tutorial` | Zoe already has matriculation number A9900001Z; matriculation numbers must be unique. | The duplicate is rejected; only Zoe has that number. |
| STUDENT-2 | `edit 1 m/A1234567X` | Alex Yeoh already has matriculation number A1234567X. | The duplicate is rejected and Zoe's details remain unchanged. |
| STUDENT-3 | `edit 1 e/zoe..test@example.com` | The email's local part contains consecutive periods. | The email is rejected and Zoe's details remain unchanged. |

### Searching and filtering students

Start in a fresh test folder with the six original sample students. Run each step in order.

1. Run `enroll 1 c/CS2103T-T10` and `enroll 2 c/CS3230-T01`.<br>
   Expected: Alex Yeoh and Bernice Yu are enrolled in their respective classes.
1. Run `filter cs2103t`, then `find alex`.<br>
   Expected: Only Alex appears. Run `find Bernice`; the list becomes empty because Bernice does not satisfy the active filter.
1. Run `list`, `find Bernice`, then `filter CS3230`.<br>
   Expected: Only Bernice appears, demonstrating that search and filter also combine in this order.
1. Run `filter CS2103T`.<br>
   Expected: No students appear: the new filter replaces CS3230, while the Bernice search remains active.
1. Run `find m/a1234567x`, then `find Bernice`.<br>
   Expected: The first command finds Alex despite the previous criteria. The second finds Bernice, proving the old filter was cleared.
1. Run `list`, then `filter CS2103T-T10 CS3230`.<br>
   Expected: Alex and Bernice appear. Run `list`; all six students return in name order.
1. Run each case in the invalid-input table below.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| SEARCH-1 | `find Alex m/A1234567X` | A name search cannot be combined with a matriculation-number search. | The request is rejected and the six-student list is preserved. |
| SEARCH-2 | `find m/A1234567X m/A2345678Y` | The `m/` prefix is repeated; only one matriculation number is allowed. | The request is rejected and the six-student list is preserved. |
| SEARCH-3 | `filter invalid` | `invalid` matches neither the module-code format nor the class-code format. | The request is rejected and the six-student list is preserved. |

### Unenrolling from one or all classes

Start in a fresh test folder with the original sample students. Run `unenroll 1 all/` first to remove Alex's sample enrollment (academic records remain). Run these steps in order.

1. Run `enroll 1 c/CS2103T-T10` and `enroll 1 c/CS3230-T01`, then `assignment m/A1234567X id/CS2103T:A1 n/Quiz max/10` and `mark m/A1234567X id/CS2103T:A1 mark/8`.<br>
   Expected: Alex Yeoh is enrolled in both classes and has a quiz mark of `8/10`.
1. Run `attendance 1 c/CS2103T-T10 d/28-10-2026` and `participation 1 c/CS2103T-T10 d/28-10-2026 p/2`.<br>
   Expected: Alex has an attendance record and 2 participation points for CS2103T-T10 on 28-10-2026.
1. Run `unenroll 1 c/CS2103T-T10`, then `view 1`.<br>
   Expected: Only CS3230-T01 remains enrolled. The quiz, score and attendance record remain. `participation view 1` still shows 2 points.
1. Run case UNENROLL-1 in the invalid-input table below.
1. Run `unenroll 1 all/`, then `view 1`.<br>
   Expected: No classes remain, but assignments, marks and attendance remain. `participation view 1` still shows the recorded points.
1. Run `mark m/A1234567X id/CS2103T:A1 mark/9`.<br>
   Expected: The retained assignment can still be marked.
1. Run cases UNENROLL-2 to UNENROLL-4 in the invalid-input table below.
1. Close and reopen the app from the same test folder. Run `view 1` and `participation view 1`.<br>
   Expected: No classes are enrolled; the quiz score of 9, attendance record and participation points are retained.
1. Run `enroll m/A1234567X c/CS2103T-T10` and `enroll m/A1234567X c/CS3230-T01`, then `find Bernice`.<br>
   Expected: Alex Yeoh is enrolled in both classes, although Alex is not in the displayed list.
1. Run `unenroll m/A1234567X c/CS2103T-T10`, then `list` and `view 1`.<br>
   Expected: The command succeeds even though Alex was hidden. Only CS3230-T01 remains enrolled.
1. Run `unenroll m/A1234567X all/`.<br>
   Expected: Alex is unenrolled from all classes.
1. Run cases UNENROLL-5 to UNENROLL-7 in the invalid-input table below separately.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| UNENROLL-1 | `unenroll 1 c/CS2103T-T10` | Alex has already been unenrolled from CS2103T-T10. | An error reports that the student is not enrolled in that class; records remain unchanged. |
| UNENROLL-2 | `assignment m/A1234567X id/CS2103T:A2 n/New Quiz max/10` | Alex has no remaining enrollment in CS2103T; module enrollment is required for new work. | The assignment is rejected and no records change. |
| UNENROLL-3 | `unenroll 1 all/` | Alex has no remaining class enrollments to remove (student identified by displayed index). | <samp>Student is not enrolled in any class.</samp> No records change. |
| UNENROLL-4 | `unenroll 1 c/CS3230-T01 all/` | A specific class and `all/` are mutually exclusive forms. | The request fails without changing records. |
| UNENROLL-5 | `unenroll m/A1234567X all/` after removing Alex's remaining enrollments | Alex has no remaining class enrollments to remove (student identified by matriculation number, even though Alex may not be in the displayed list). | <samp>Student is not enrolled in any class.</samp> No records change. |
| UNENROLL-6 | `unenroll m/A9999999Z all/` | No student has the supplied matriculation number. | The request fails without changing records. |
| UNENROLL-7 | `unenroll 1 m/A1234567X all/` | An index and a matriculation number cannot be supplied together. | The request fails without changing records. |

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
      Expected: Students appear in ascending email order. Alex Yeoh appears first, followed by Bernice Yu; Roy Balakrishnan appears last.

   1. Run `list`.<br>
      Expected: Students return to alphabetical name order.

1. Sorting by attendance record count

   1. In a fresh data folder with the original sample students, run `sort attendance desc`.<br>
      Expected: Every student card shows <samp>Attendance records: 2</samp>, and students remain in name order.

   1. Run `enroll 6 c/CS2103T-T10` to enroll Roy Balakrishnan, then `attendance m/A6789012U c/CS2103T-T10 d/07-10-2026`.<br>
      Expected: Roy Balakrishnan moves to the first position and shows <samp>Attendance records: 3</samp>.

   1. Run `list`.<br>
      Expected: Students return to name order and the attendance count line disappears.

1. Sorting by one assignment's mark

   1. Prerequisites: Start in a fresh data folder with the original six sample students. Run `list`, then `enroll 1 c/CS2103T-T10` and `enroll 6 c/CS2103T-T10` to enroll Alex Yeoh (`A1234567X`) and Roy Balakrishnan (`A6789012U`) in `CS2103T`.

   1. Create an assignment and attach it to two students using `assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20` and `assignment m/A6789012U id/CS2103T:A1`. Record `mark m/A1234567X id/CS2103T:A1 mark/0`.<br>
      Expected: One student has a recorded zero, one is unmarked, and at least one other sample student is not assigned.

   1. Run `sort mark id/CS2103T:A1 desc`, then `sort mark id/CS2103T:A1 asc`.<br>
      Expected: Both sorts show Alex Yeoh (<samp>0/20</samp>), Roy Balakrishnan (<samp>Unmarked</samp>), then the <samp>Not assigned</samp> students in ascending name order: Bernice Yu, Charlotte Oliveiro, David Li, and Irfan Ibrahim. Every card shows <samp>CS2103T:A1</samp> with its result. The recorded zero comes first, and Roy appears above Bernice despite their alphabetical order.

   1. Run case SORT-1 in the invalid-input table below.

   1. Record another mark using `mark m/A6789012U id/CS2103T:A1 mark/15`.<br>
      Expected: The list returns to name order and the assignment result line disappears. Running `sort mark id/CS2103T:A1 desc` again shows the student with 15 first.

1. Preserving an assignment-mark sort during attendance, enrollment, and participation updates

   1. Prerequisites: Use a fresh data folder with the original six sample students. Each student already has two September attendance records and one September participation record. These existing records should remain throughout the following steps. Enter each command separately:

      ```text
      enroll 1 c/CS2103T-T10
      enroll 2 c/CS2103T-T10
      assignment m/A2345678Y id/CS2103T:A1 n/Quiz max/10
      mark m/A2345678Y id/CS2103T:A1 mark/5
      sort mark id/CS2103T:A1 desc
      attendance 1 c/CS2103T-T10 d/10-10-2026
      attendance 1 c/CS2103T-T10 d/11-10-2026
      ```

      Expected: Bernice Yu remains at displayed index 1 after each attendance command, with <samp>CS2103T:A1: 5/10</samp> on her student card. Alex Yeoh remains at displayed index 2 with <samp>CS2103T:A1: Not assigned</samp>. Run `view 1` and `view 2` separately to confirm Bernice has attendance records for 10-10-2026 and 11-10-2026 in CS2103T-T10 and Alex has neither of those records. Both students retain their two September attendance records, so Bernice has four attendance records in total and Alex has two.

   1. Run `enroll 1 c/CS2103T-T11`, `unenroll 1 c/CS2103T-T11`, `participation 1 c/CS2103T-T10 d/10-10-2026 p/2`, and `participation remove 1 c/CS2103T-T10 d/10-10-2026` separately.<br>
      Expected: Each update applies to Bernice. Removing the 10-10-2026 participation record preserves her September participation record. The displayed order and assignment results remain unchanged after every command.

   1. Run `attendance unmark 1 c/CS2103T-T10 d/10-10-2026`, then `attendance c/CS2103T-T10 d/12-10-2026`.<br>
      Expected: Bernice's 10 October attendance is removed; both enrolled students receive 12 October attendance. Bernice remains first, and the assignment results remain visible and correct.

   1. Run `find Alex Bernice`, `sort mark id/CS2103T:A1 asc`, then `participation 1 c/CS2103T-T10 d/11-10-2026 p/3`.<br>
      Expected: Only Bernice and Alex remain displayed, in that order, with their assignment results. Bernice receives the participation record.

   1. Run case SORT-2 in the invalid-input table below.

   1. Run `edit 1 m/A2345679Y`.<br>
      Expected: The displayed list returns to name order, shows all six students, and clears assignment results from student cards. Run `sort mark id/CS2103T:A1 desc` again; Bernice returns to displayed index 1 with the new matriculation number and <samp>5/10</samp>.

#### Invalid inputs

Run each case at the point indicated in the scenarios above.

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| SORT-1 | `sort mark id/CS2103T:A9` | No assignment with ID CS2103T:A9 exists in the fresh test data. | TAssist reports that the assignment does not exist; the displayed order and card details remain unchanged. |
| SORT-2 | `enroll 1 c/CS2103T-T10` | Bernice is already enrolled in CS2103T-T10. | The duplicate enrollment is rejected; the displayed order and assignment results remain unchanged. |

### Keeping an open student profile current

Run the following scenarios in order in a test data folder. Keep the details window open and enter commands in the main window unless instructed otherwise.

1. Refreshing details after edits

   1. Prerequisites: Have at least two students. First add `add n/Aaron Profile m/A1111111A e/aaron.profile@u.nus.edu` so the first student has no enrollment or academic history. The first student must not be enrolled in CS2103T-T10 and must have no attendance or participation records for that class. Ensure A9999999Z is unused. Run `list`, then `view 1`.

   2. Run `edit 1 n/Aaron Updated m/A9999999Z e/aaron.updated@example.com t/updated`.<br>
      Expected: The existing details window immediately shows the new name, matriculation number, email and tag, and its title uses the new name. It does not take keyboard focus from the main window.

   3. Run `find m/A9999999Z`, then `enroll 1 c/CS2103T-T10`.<br>
      Expected: Aaron is the only displayed student. The class immediately appears in the open details window.

   4. Run `attendance m/A9999999Z c/CS2103T-T10 d/07-10-2026`.<br>
      Expected: An attendance record for 07-10-2026 and CS2103T-T10 appears in the open window, and the attendance count increases without another `view` command.

   5. Run `attendance unmark m/A9999999Z c/CS2103T-T10 d/07-10-2026`.<br>
      Expected: The attendance record disappears and the attendance count decreases immediately.

   6. Run `unenroll 1 c/CS2103T-T10`.<br>
      Expected: The class disappears from the open details window.

   7. Run `enroll 1 c/CS2103T-T10` again.<br>
      Expected: The class reappears. Leave Aaron enrolled for the next scenario.

   8. Run `participation m/A9999999Z c/CS2103T-T10 d/07-10-2026 p/2`.
      Expected: The open student details window displays a Participation section containing CS2103T-T10, 07-10-2026 and 2 points.

   9. Run `participation m/A9999999Z c/CS2103T-T10 d/07-10-2026 p/3`.
      Expected: The participation record updates immediately to 3 points. The previous score is replaced, not added to it.

   10. Run `participation remove m/A9999999Z c/CS2103T-T10 d/07-10-2026`.
      Expected: The participation record disappears from the open details window, and the Participation section shows None recorded. No additional view command is required.


2. Preserving the selected profile through list changes

   1. With Aaron's profile still open, run `list`, then `sort matric desc`.<br>
      Expected: The list is sorted, and the details window continues to show Aaron.

   2. Run `find` with a name keyword matching only another student.<br>
      Expected: The details window continues to show Aaron, even while Aaron is absent from the displayed list.

   3. Run `attendance m/A9999999Z c/CS2103T-T10 d/07-10-2026`.<br>
      Expected: Aaron's open profile shows the new attendance record and updated count while the student remains hidden from the list.

   4. Edit a different student using their current displayed index.<br>
      Expected: Aaron's details window stays open and continues to show Aaron.

3. Closing the profile after deletion or clearing

   1. Run `find m/A9999999Z`, then `delete 1`.<br>
      Expected: Aaron is deleted and the details window closes, including after the earlier matriculation number edit.

   2. Run `list`, then `view 1` for a remaining student, then `clear`.<br>
      Expected: The student list becomes empty and the details window closes.

4. Keeping a manually closed profile closed

   1. Run `add n/Profile Test m/A9999999Z e/profile.test@example.com`, then `view 1`.<br>
      Expected: The student *Profile Test* is added and their details window opens.

   2. Close the details window with `Esc`, then run `edit 1 n/Profile Updated`.<br>
      Expected: The details window stays closed.

   3. Run `view 1` again.<br>
      Expected: The window opens with the updated name and title.

   4. Close the window with its close button, then run `edit 1 n/Profile Final`.<br>
      Expected: The details window stays closed. Running `view 1` again opens it with the latest name and title.

### Deleting a student

1. Deleting a student while all students are being shown

   1. Prerequisites: List all students using the `list` command, with multiple students in the list.

   1. Test case: `delete 1`<br>
      Expected: The first student is deleted from the list. The status message shows the deleted student's details.

   1. Run each case in the invalid-input table below. For DELETE-4, replace `N` with an integer larger than the current displayed list size.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| DELETE-1 | `delete 0` | Displayed student indices start at 1. | No student is deleted; the status message shows error details. |
| DELETE-2 | `delete` | No student index or matriculation number is supplied. | No student is deleted; the status message shows error details. |
| DELETE-3 | `delete x` | The literal `x` is not a positive integer index. | No student is deleted; the status message shows error details. |
| DELETE-4 | `delete N` | The supplied integer is larger than the displayed list size. | No student is deleted; the status message shows error details. |

### Deleting multiple students or a hidden student

Start in a fresh test folder with the original six sample students. Run these steps in order.

1. Run cases DELETE-MULTI-1 and DELETE-MULTI-2 in the invalid-input table below, then run `list` to confirm all six students remain.
1. Run `delete 1 3`.<br>
   Expected: Alex Yeoh and Charlotte Oliveiro are deleted using their original displayed indices. Bernice Yu remains.
1. Run `undo`.<br>
   Expected: <samp>Undo successful.</samp> is displayed, and all six students return in name order.
1. Run `redo`.<br>
   Expected: <samp>Redo successful.</samp> is displayed, and Alex and Charlotte disappear again.
1. Run `undo`.<br>
   Expected: <samp>Undo successful.</samp> is displayed, and all six students return in name order.
1. Run `find Alex`, then `delete m/A2345678Y`.<br>
   Expected: Bernice is deleted even though only Alex was displayed. Run `list` to confirm Bernice is absent and the other five students remain.
1. Run case DELETE-MULTI-3 in the invalid-input table below.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| DELETE-MULTI-1 | `delete 1 1` | Index 1 is repeated; a student cannot be targeted twice in one deletion. | The whole command fails without deleting anyone; all six students remain. |
| DELETE-MULTI-2 | `delete 1 99` | Index 99 is outside the six-student displayed list. | The whole command fails without deleting anyone, including the student at index 1. |
| DELETE-MULTI-3 | `delete m/A2345678Y` | Bernice has already been deleted, so no student has matriculation number A2345678Y. | An error reports that the student was not found; no additional students are deleted. |

### Undoing and redoing changes

Start in a fresh test folder with the original six sample students. Run these scenarios in order in the same session until instructed to restart. Enter each command separately and check its feedback before entering the next command.

1. Rejecting undo and redo when history is empty

   1. Run `find Alex Bernice`, `sort matric desc`, then `view 1`.<br>
      Expected: Only Bernice and Alex are displayed, in that order, and Bernice's details window opens.
   1. Run cases HISTORY-1 and HISTORY-2 in the invalid-input table below.<br>
      Expected: Only Bernice and Alex remain displayed, in that order, and Bernice's details window stays open. No records change.

1. Restoring successive changes in order

   1. Run `add n/Undo Test m/A9900187Z e/undo187@example.com`, `find m/A9900187Z`, `enroll 1 c/CS2103T-T10`, then `view 1`.<br>
      Expected: The student *Undo Test* exists and is enrolled in CS2103T-T10.
   1. Run `sort attendance`, then `undo`.<br>
      Expected: <samp>Undo successful.</samp> is displayed. Enrollment is undone, but the student *Undo Test* remains. All seven students appear in ascending name order, the attendance records row disappears, and the details window closes.
   1. Run `find m/A9900187Z`, then `view 1`.<br>
      Expected: The student has no enrolled classes.
   1. Run `undo` again.<br>
      Expected: <samp>Undo successful.</samp> is displayed. The addition is undone, leaving the original six students, and the details window closes.
   1. Run case HISTORY-3 in the invalid-input table below.
   1. Run `redo`.<br>
      Expected: <samp>Redo successful.</samp> is displayed, and the student *Undo Test* returns.
   1. Run `find m/A9900187Z`, then `view 1`.<br>
      Expected: The student's email is `undo187@example.com` and no classes are enrolled.
   1. Run `redo` again.<br>
      Expected: <samp>Redo successful.</samp> is displayed. Enrollment in CS2103T-T10 returns, all seven students appear in ascending name order, and the details window closes.
   1. Run `find m/A9900187Z`, then `view 1`.<br>
      Expected: The student *Undo Test* is enrolled in CS2103T-T10.
   1. Run case HISTORY-4 in the invalid-input table below.<br>
      Expected: The filtered list and details window remain unchanged.

1. Preserving redo when commands do not change records

   After each redo below, run `find m/A9900187Z`, then `view 1` to confirm enrollment in CS2103T-T10 is restored.

   1. Run `undo`, `find m/A9900187Z`, `sort matric desc`, then `redo`.<br>
      Expected: Undo reports <samp>Undo successful.</samp> Finding and sorting do not discard redo history. Redo reports <samp>Redo successful.</samp> and restores enrollment in CS2103T-T10.
   1. Run `undo`, then case HISTORY-5 in the invalid-input table below, then `redo`.<br>
      Expected: Undo reports <samp>Undo successful.</samp> Redo reports <samp>Redo successful.</samp> and restores enrollment.
   1. Run `undo`, `find m/A9900187Z`, `edit 1 n/Undo Test`, then `redo`.<br>
      Expected: Undo reports <samp>Undo successful.</samp> Editing the name to its existing value succeeds without changing records. Redo reports <samp>Redo successful.</samp> and restores enrollment.

1. Discarding redo after a new change

   1. Run `undo`, `find m/A9900187Z`, then `edit 1 e/undo.updated@example.com`.<br>
      Expected: Undo reports <samp>Undo successful.</samp> The email changes to `undo.updated@example.com` and the student has no enrolled classes.
   1. Run case HISTORY-6 in the invalid-input table below, then `find m/A9900187Z` and `view 1`.<br>
      Expected: The new email remains and the student has no enrolled classes; the previously undone enrollment cannot be redone.
   1. Run `undo`, `find m/A9900187Z`, then `view 1`.<br>
      Expected: Undo reports <samp>Undo successful.</samp> The email returns to `undo187@example.com` and no classes are enrolled.
   1. Run `redo`, `find m/A9900187Z`, then `view 1`.<br>
      Expected: Redo reports <samp>Redo successful.</samp> The email becomes `undo.updated@example.com` again, and no classes are enrolled.
   1. Run `undo` once more.<br>
      Expected: <samp>Undo successful.</samp> is displayed. The email returns to `undo187@example.com`. Leave this state for the restart scenario: both an earlier change to undo and the email change to redo are available.

1. Keeping restored records while clearing history on restart

   1. Close TAssist and reopen the same JAR from the same test folder. Run `find m/A9900187Z`, then `view 1`.<br>
      Expected: The student *Undo Test* remains saved with email `undo187@example.com` and no enrolled classes.
   1. Run cases HISTORY-7 and HISTORY-8 in the invalid-input table below.<br>
      Expected: The restored records, filtered list and details window remain unchanged.
   1. Run `list`.<br>
      Expected: All seven students remain.

For additional restoration checks involving deleted students, assignment links and marks, follow [Deleting multiple students or a hidden student](#deleting-multiple-students-or-a-hidden-student) and [Viewing marks in a student profile](#viewing-marks-in-a-student-profile).

#### Invalid inputs

Run each case at the point indicated in the scenarios above.

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| HISTORY-1 | `undo` before any records have changed | Finding, sorting and viewing do not create a change to undo. | <samp>No commands to undo.</samp> Records, displayed order and open profile are unchanged. |
| HISTORY-2 | `redo` before any change has been undone | There is no undone change to redo. | <samp>No commands to redo.</samp> Records, displayed order and open profile are unchanged. |
| HISTORY-3 | `undo` after undoing both enrollment and student addition | Every recorded change has already been undone. | <samp>No commands to undo.</samp> The original six students remain. |
| HISTORY-4 | `redo` after redoing both student addition and enrollment | Every undone change has already been redone. | <samp>No commands to redo.</samp> Records, filtered list and open profile are unchanged. |
| HISTORY-5 | `delete 0` after undoing enrollment | Student indices must be positive integers starting from 1. | Deletion is rejected without changing records or discarding redo history. |
| HISTORY-6 | `redo` after changing the email following an undo | A new change discards the previous redo history. | <samp>No commands to redo.</samp> The new email remains and enrollment is not restored. |
| HISTORY-7 | `undo` immediately after restarting TAssist | Undo history is cleared on restart. | <samp>No commands to undo.</samp> Restored records, filtered list and open profile are unchanged. |
| HISTORY-8 | `redo` immediately after restarting TAssist | Redo history is cleared on restart. | <samp>No commands to redo.</samp> Restored records, filtered list and open profile are unchanged. |

### Recording attendance

1. Marking and unmarking attendance

   1. Prerequisites: List all students using the `list` command, then run `enroll 1 c/CS2103T-T10`. The first student has no attendance recorded on 07-10-2026.

   1. Test case: `attendance 1 c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The status message shows <samp>Marked attendance for NAME in CS2103T-T10 on 07-10-2026</samp>, where NAME is the first student's name. `view 1` lists the date and class.

   1. Test case: `attendance unmark 1 c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The status message shows <samp>Unmarked attendance for NAME in CS2103T-T10 on 07-10-2026</samp>. `view 1` no longer lists that record.

   1. Run case ATTENDANCE-1 in the invalid-input table below.

   1. Run cases ATTENDANCE-2 to ATTENDANCE-4 in the invalid-input table below separately.

1. Identifying a student outside the displayed list

   1. Prerequisites: Run `list` and `enroll 2 c/CS2103T-T10`. Note the second student's matriculation number (MATRIC) using `view 2`; that student has no attendance recorded on 07-10-2026. Then use `find` with a keyword that matches the first student only, so the second student is not displayed.

   1. Test case: `attendance m/MATRIC c/CS2103T-T10 d/07-10-2026`<br>
      Expected: Attendance is recorded for the second student, although they are not in the displayed list.

   1. Test case: `attendance unmark m/MATRIC c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The record is removed. The status message names the second student.

1. Rejecting a class the student is not enrolled in

   1. Prerequisites: Run `list`. The first student is enrolled in CS2103T-T10 but not CS2103T-T11.

   1. Run case ATTENDANCE-5 in the invalid-input table below.

1. Removing one of two classes on the same date

   1. Prerequisites: Run `enroll 1 c/CS2103T-T11`. The first student has no attendance recorded on 07-10-2026.

   1. Test case: `attendance 1 c/CS2103T-T10 d/07-10-2026`, then `attendance 1 c/CS2103T-T11 d/07-10-2026`<br>
      Expected: Both succeed. `view 1` shows <samp>4 records</samp> (including the two September sample records), with CS2103T-T10 before CS2103T-T11 for that date.

   1. Run case ATTENDANCE-6 in the invalid-input table below.

   1. Test case: `attendance unmark 1 c/CS2103T-T11 d/07-10-2026`, then `attendance unmark 1 d/07-10-2026`<br>
      Expected: The first command removes only the CS2103T-T11 record. The second infers CS2103T-T10 and shows <samp>Unmarked attendance for NAME in CS2103T-T10 on 07-10-2026</samp>. Neither record remains.

1. Marking a whole class

   1. Prerequisites: Close TAssist and back up `data/addressbook.json` before testing. Move the data file out of the `data` folder and reopen TAssist. Run `list`, then `enroll 1 c/CS2103T-T10`, `enroll 2 c/CS2103T-T10`, and `attendance 1 c/CS2103T-T10 d/07-10-2026`.

   1. Test case: `attendance c/CS2103T-T10 d/07-10-2026`<br>
      Expected: The status message shows <samp>Marked attendance for 1 student in CS2103T-T10 on 07-10-2026</samp>, followed on a second line by <samp>Already recorded, skipped: Alex Yeoh (A1234567X)</samp>.

   1. Run case ATTENDANCE-7 in the invalid-input table below.

#### Invalid inputs

Run each case after its prerequisites in the scenarios above. `NAME` is the selected student's name.

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| ATTENDANCE-1 | `attendance unmark 1 d/07-10-2026` after removing the only record on that date | No attendance record remains to remove. | <samp>No attendance recorded for NAME on 07-10-2026.</samp> No records change. |
| ATTENDANCE-2 | `attendance 1 c/CS2103T-T10 d/28-10-2099` | Attendance cannot be recorded for a future date. | <samp>Attendance date 28-10-2099 is in the future. Use today or an earlier date.</samp> No records change. |
| ATTENDANCE-3 | `attendance c/CS2103T-T10 d/28-10-2099` | Whole-class attendance cannot be recorded for a future date. | <samp>Attendance date 28-10-2099 is in the future. Use today or an earlier date.</samp> No records change. |
| ATTENDANCE-4 | `attendance unmark 1 d/28-10-2099` | Attendance cannot be unmarked for a future date. | <samp>Attendance date 28-10-2099 is in the future. Use today or an earlier date.</samp> No records change. |
| ATTENDANCE-5 | `attendance 1 c/CS2103T-T11 d/07-10-2026` before enrolling the student in CS2103T-T11 | Attendance can only be marked for a class the student is enrolled in. | <samp>NAME is not enrolled in CS2103T-T11.</samp> No attendance record is added. |
| ATTENDANCE-6 | `attendance unmark 1 d/07-10-2026` while both class records exist | The date matches CS2103T-T10 and CS2103T-T11, so the class is needed to identify which record to remove. | <samp>NAME has attendance recorded in more than one class on 07-10-2026: CS2103T-T10, CS2103T-T11. Specify the class with c/MODULE-CLASS.</samp> Both records remain. |
| ATTENDANCE-7 | `attendance c/CS2103T-T99` with no students enrolled in that class | There are no enrolled students whose attendance can be marked. | <samp>No students are enrolled in CS2103T-T99.</samp> No attendance records change. |


### Recording class participation

Start in a fresh test folder with the original sample students. Use a fresh copy of the test data for each scenario.

1. Recording and updating participation points

   1. Prerequisites: Run `find Yeoh` to display Alex Yeoh. Use `view 1` to verify the matriculation number is `A1234567X`. Enroll Alex Yeoh using `enroll 1 c/CS2103T-T10` if they are not already enrolled. Alex Yeoh must have no participation record for CS2103T-T10 on 28-10-2026.
   2. Test case: `participation 1 c/CS2103T-T10 d/28-10-2026 p/2`, followed by `participation view m/A1234567X`.<br>
      Expected: The status message confirms that 2 points were recorded. The history contains `CS2103T-T10 | 28-10-2026 | 2 point(s)`. Attendance and other student details are unchanged.
   3. Test case: `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/3`, followed by `participation view m/A1234567X`.<br>
      Expected: The existing score is replaced. There is one record for that class and date with 3 points, not 5. Other participation records are unchanged.
   4. Test case: Repeat `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/3`, then run `participation view m/A1234567X` again.<br>
      Expected: The score remains 3 and no duplicate record is created.

2. Viewing a student outside the displayed list

   1. Prerequisites: Run `find Yeoh` to display Alex Yeoh. Enroll Alex Yeoh in CS2103T-T10 if they are not already enrolled. Ensure Alex Yeoh has a participation record by running `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/3`. Run `find Balakrishnan` to display Roy Balakrishnan instead, so Alex Yeoh is no longer in the displayed list.
   2. Test case: `participation view m/A1234567X`.<br>
      Expected: Alex Yeoh's participation history is shown in the command result panel, even though Alex Yeoh is not in the displayed list. Entries are ordered by class code and then date. The displayed student list and stored data are unchanged.

3. Rejecting invalid or incomplete input

   1. Prerequisites: Run `find Yeoh` and ensure Alex Yeoh is enrolled in CS2103T-T10. Run `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/3`, then `participation view m/A1234567X` to note the existing records.
   2. Run cases PARTICIPATION-1 to PARTICIPATION-9 in the invalid-input table below, one at a time. After each rejected command, run `participation view m/A1234567X`.<br>
      Expected: The existing score remains 3, and no other participation records change.

4. Rejecting a class the student is not enrolled in

   1. Prerequisites: Run `find Yeoh` to display Alex Yeoh. Ensure Alex Yeoh is enrolled in CS2103T-T10 but not CS2103T-T11. Note Alex Yeoh's current participation history using `participation view m/A1234567X`.
   2. Run case PARTICIPATION-10 in the invalid-input table below.

5. Preserving participation after edits, attendance changes and restart

   1. Prerequisites: Run `find Yeoh` to display Alex Yeoh. Use `view 1` to verify the matriculation number is `A1234567X`. Ensure Alex Yeoh is enrolled in CS2103T-T10. Alex Yeoh must have no attendance record for that class on 29-10-2026.
   2. Test case: `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/3`, followed by `participation view m/A1234567X`.<br>
      Expected: A participation record with 3 points is created for CS2103T-T10 on 28-10-2026.
   3. Test case: `edit 1 n/Participation Test Student`, followed by `participation view m/A1234567X`.<br>
      Expected: Alex Yeoh's name changes to Participation Test Student, but the participation record still contains 3 points for CS2103T-T10 on 28-10-2026. Other participation records are unchanged.
   4. Test case: `attendance m/A1234567X c/CS2103T-T10 d/29-10-2026`, followed by `participation view m/A1234567X`.<br>
      Expected: Attendance is recorded successfully. The participation history is unchanged.
   5. Test case: Close and reopen TAssist, then run `participation view m/A1234567X`.<br>
      Expected: The updated student name and participation history are retained, including the score of 3 for CS2103T-T10 on 28-10-2026.

6. Distinguishing zero points from removing a record
   1. Prerequisites: Run `find Yeoh` to display Alex Yeoh. Ensure Alex Yeoh is enrolled in CS2103T-T10. Run `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/3` and `participation m/A1234567X c/CS2103T-T10 d/29-10-2026 p/1`. View the history using `participation view m/A1234567X` to confirm both records.
   2. Test case: `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/0`, followed by `participation view m/A1234567X`.<br>
      Expected: The 28-10-2026 record remains in the history with 0 points. The 29-10-2026 record still has 1 point.
   3. Test case: `participation remove m/A1234567X c/CS2103T-T10 d/28-10-2026`, followed by `participation view m/A1234567X`.<br>
      Expected: Only the 28-10-2026 participation record is removed. The 29-10-2026 record, attendance and other student details remain unchanged.
   4. Run case PARTICIPATION-11 in the invalid-input table below, then run `participation view m/A1234567X` again.

#### Invalid inputs

Run each case after its prerequisites in the scenarios above. Each scenario uses Alex Yeoh (`A1234567X`).

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| PARTICIPATION-1 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/-1` | Points must contain digits only and be between 0 and 2147483647 inclusive; negative points are not allowed. | The status message explains the valid points format and range; existing participation records are unchanged. |
| PARTICIPATION-2 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/2.5` | Points must be a whole number; decimal values are not allowed. | The command is rejected; existing participation records are unchanged. |
| PARTICIPATION-3 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/two` | Points must contain digits only. | The command is rejected; existing participation records are unchanged. |
| PARTICIPATION-4 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/2147483648` | The value exceeds the maximum of 2147483647 points. | The command is rejected; existing participation records are unchanged. |
| PARTICIPATION-5 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/` | The points value is empty. | The command is rejected; existing participation records are unchanged. |
| PARTICIPATION-6 | `participation m/A1234567X c/CS2103T-T10 d/31-02-2026 p/2` | 31 February is not a valid calendar date. | The status message asks for a valid date in dd-MM-yyyy format; no record is added or changed. |
| PARTICIPATION-7 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026` | The required `p/POINTS` argument is missing. | The status message asks for points using `p/POINTS`; existing participation records are unchanged. |
| PARTICIPATION-8 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2026 p/2 p/3` | The points prefix is repeated. | The repeated prefix is rejected; neither value is applied and existing participation records are unchanged. |
| PARTICIPATION-9 | `participation m/A1234567X c/CS2103T-T10 d/28-10-2099 p/2` | Participation cannot be recorded for a future date. | <samp>Participation date 28-10-2099 is in the future. Use today or an earlier date.</samp> No participation records are added or changed. |
| PARTICIPATION-10 | `participation m/A1234567X c/CS2103T-T11 d/28-10-2026 p/2` while Alex is not enrolled in CS2103T-T11 | Participation can only be recorded for a class the student is enrolled in. | <samp>Alex Yeoh is not enrolled in CS2103T-T11. Enroll the student in this class first.</samp> No student data changes. |
| PARTICIPATION-11 | `participation remove m/A1234567X c/CS2103T-T10 d/28-10-2026` after that record has been removed | No participation record exists for the student, class and date. | The status message reports the missing record; no further data changes occur. |

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
   2. Run cases STATS-1 to STATS-5 in the invalid-input table below, one at a time.
   3. Run `attendance stats c/CS2103T-T10 d/28-10-2026` again.\
      Expected: The original statistics are unchanged after the invalid commands.

6. Checking different dates and classes
   1. Prerequisites: Enroll the first student in CS2103T-T10 and CS2103T-T11. Ensure the student has no attendance records for those classes on 29-10-2026.
   2. Test case: `attendance 1 c/CS2103T-T11 d/29-10-2026`, followed by `attendance stats c/CS2103T-T10 d/29-10-2026`.\
      Expected: The attendance record for CS2103T-T11 is not counted towards CS2103T-T10.
   3. Test case: `attendance stats c/CS2103T-T11 d/29-10-2026`\
      Expected: The first student's attendance is included in the attended count for CS2103T-T11.
   4. Test case: `attendance stats c/CS2103T-T11 d/30-10-2026`\
      Expected: The attendance recorded on 29-10-2026 is not counted for 30-10-2026.

#### Invalid inputs

Run these cases during scenario 5. No case should change student data or the displayed student list.

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| STATS-1 | `attendance stats c/CS2103T-T10` | The required date is missing. | The command is rejected and the required command format is shown. |
| STATS-2 | `attendance stats d/28-10-2026` | The required class code is missing. | The command is rejected and the required command format is shown. |
| STATS-3 | `attendance stats c/CS2103T-T10 d/31-02-2026` | 31 February is not a valid calendar date. | The command is rejected with a date validation error. |
| STATS-4 | `attendance stats c/CS2103T-T10 c/CS2103T-T11 d/28-10-2026` | The class prefix is repeated. | The repeated prefix is rejected. |
| STATS-5 | `attendance stats 1 c/CS2103T-T10 d/28-10-2026` | Class statistics do not accept a student index. | The command is rejected with a format error. |

### Assigning work to a student

1. Assign the same work to two enrolled students.

   1. Prerequisites: Start in a fresh data folder with the original six sample students. Run `list`, then `enroll 1 c/CS2103T-T10` and `enroll 2 c/CS2103T-T10` to enroll Alex Yeoh (`A1234567X`) and Bernice Yu (`A2345678Y`) in `CS2103T`.

   1. Test case: `assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20`<br>
      Expected: <samp>Assigned CS2103T:A1 (out of 20) to student A1234567X.</samp> The assignment is attached without a mark.
   1. Test case: `assignment m/A2345678Y id/CS2103T:A1`<br>
      Expected: <samp>Assigned CS2103T:A1 (out of 20) to student A2345678Y.</samp> The same definition is attached without a mark.
   1. Run cases ASSIGN-1 to ASSIGN-5 in the table below, entering each command separately.
   1. Close and reopen TAssist. Enter `marks id/CS2103T:A1`.<br>
      Expected: Both assigned students appear as <samp>Unmarked</samp>, confirming their links were restored.
   1. Run case ASSIGN-6 in the table below to confirm that the rejected definition was not saved.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| ASSIGN-1 | `assignment m/A1234567X id/CS2103T:A1 n/Assignment One max/20` | An assignment definition with ID `CS2103T:A1` already exists. | The duplicate is reported; existing assignments are unchanged. |
| ASSIGN-2 | `assignment m/A2345678Y id/CS2103T:A1` | The assignment is already attached to Bernice. | The duplicate is reported; existing assignments are unchanged. |
| ASSIGN-3 | `assignment m/A1234567X id/CS2103T:A2 n/Assignment One max/20` | Another assignment in CS2103T already has the name `Assignment One`. | The duplicate is reported; `CS2103T:A2` is not created. |
| ASSIGN-4 | `assignment m/A1234567X id/CS2103T:A3 n/Extreme max/1e1000000` | The maximum's displayed decimal form exceeds 64 characters. | The maximum is rejected as too long for display; `CS2103T:A3` is not created. |
| ASSIGN-5 | `assignment m/A1234567X id/CS2103T:A3 n/Extreme max/100e2147483647` | The maximum's displayed decimal form exceeds 64 characters. | <samp>Maximum mark's displayed decimal form must be at most 64 characters.</samp> is shown. Existing assignments and saved data are unchanged; `CS2103T:A3` is not created. |
| ASSIGN-6 | `marks id/CS2103T:A3` (after reopening) | The rejected assignment definition does not exist. | An unknown-assignment error confirms that the definition was not saved. |

### Identifying students for assignments and marks

Start with the original six sample students in a disposable data folder. Ensure assignment IDs `CS2103T:INDEX151`, `CS2103T:HIDDEN151`, and `CS2103T:REJECT151`, and names `Index Regression`, `Hidden Regression`, and `Rejected Regression` are unused. Run `list`, then `sort name desc`, then `enroll 1 c/CS2103T-T10` and `enroll 2 c/CS2103T-T10` to enroll Roy Balakrishnan and Irfan Ibrahim. This explicit enrollment is needed even when sample students already belong to other modules. Run the steps in order.

1. Run `list`, then `sort name desc`. Note the first student's matriculation number.
1. Enter `assignment 1 id/CS2103T:INDEX151 n/Index Regression max/20`.<br>
   Expected: The definition is created and attached only to the first displayed student.
1. Enter `mark 1 id/CS2103T:INDEX151 mark/10`, then `marks id/CS2103T:INDEX151`.<br>
   Expected: Only that student has a mark of 10/20. Run `undo` then `redo`; the mark is removed then restored.
1. Enter `find Irfan` so Roy is hidden. Enter `assignment 1 id/CS2103T:INDEX151`, then `mark 1 id/CS2103T:INDEX151 mark/5`.<br>
   Expected: The existing definition attaches to the first search result, and only their mark changes.
1. Enter `mark m/A6789012U id/CS2103T:INDEX151 mark/12`.<br>
   Expected: Roy's mark changes despite being hidden.
1. Enter `assignment m/A6789012U id/CS2103T:HIDDEN151 n/Hidden Regression max/20`.<br>
   Expected: The new definition is created and attached to Roy despite being hidden.
1. Run `marks id/CS2103T:INDEX151` and confirm Irfan (`A5678901V`) has <samp>5/20</samp> and Roy (`A6789012U`) has <samp>12/20</samp>. Enter `mark m/A5678901V id/CS2103T:INDEX151 mark/6`.<br>
   Expected: Irfan's mark becomes <samp>6/20</samp> and Roy's remains <samp>12/20</samp>. This accepted change provides a known undo checkpoint for the rejection cases below. Do not restart yet. Use matriculation numbers for the history checks because undo and redo reset the displayed list.
1. Run cases IDENTIFIER-1 to IDENTIFIER-15 in the invalid-input table below. Enter each command in each row separately.<br>
   Expected: Each request fails with the message in the table, without changing students, definitions, links, marks, or undo/redo history. No rejected assignment creates `CS2103T:REJECT151`.
1. Enter `undo`, then `marks id/CS2103T:INDEX151`.<br>
   Expected: Undo succeeds and Irfan's mark returns to <samp>5/20</samp>, while Roy's remains <samp>12/20</samp>. This confirms the rejected commands added no undo history entries.
1. Repeat cases IDENTIFIER-1 to IDENTIFIER-15 without running any successful data-changing commands. Then enter `redo`, followed by `marks id/CS2103T:INDEX151`.<br>
   Expected: Redo succeeds and Irfan's mark returns to <samp>6/20</samp>, while Roy's remains <samp>12/20</samp>. This confirms the rejected commands preserved the pending redo.
1. Enter `mark m/A5678901V id/CS2103T:INDEX151 mark/5`, then `marks id/CS2103T:INDEX151`.<br>
   Expected: Irfan has <samp>5/20</samp> and Roy has <samp>12/20</samp>, restoring the expected marks before checking persistence.
1. Close and reopen TAssist. Run `marks id/CS2103T:INDEX151`.<br>
   Expected: Both students' assignment links persist, with Irfan at <samp>5/20</samp> and Roy at <samp>12/20</samp>. Rejected changes remain absent.

#### Invalid inputs

Run these cases at the points indicated in the steps above. Where a row lists two commands, enter both separately. Every request must be rejected without changing records or undo/redo history. All of these are rejected before the student is looked up, except IDENTIFIER-9, so they do not depend on the displayed list.

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| IDENTIFIER-1 | `assignment id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark id/CS2103T:INDEX151 mark/5` | No student identifier is supplied. | <samp>Specify the student by either INDEX or m/MATRICULATION_NUMBER.</samp> followed by that command's usage, which shows both the index and `m/` worked examples. |
| IDENTIFIER-2 | `assignment 1 m/A6789012U id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark 1 m/A6789012U id/CS2103T:INDEX151 mark/5` | An index and matriculation number cannot be supplied together. | <samp>Specify the student by either INDEX or m/MATRICULATION_NUMBER, not both.</samp> followed by that command's usage, which shows both the index and `m/` worked examples. |
| IDENTIFIER-3 | `assignment m/A6789012U m/A6789012U id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark m/A6789012U m/A6789012U id/CS2103T:INDEX151 mark/5` | The matriculation-number prefix is repeated. | <samp>Multiple values specified for the following single-valued field(s): m/</samp> |
| IDENTIFIER-4 | `assignment 0 id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark 0 id/CS2103T:INDEX151 mark/5` | Displayed student indices start at 1. | <samp>Index must be a positive integer.</samp> |
| IDENTIFIER-5 | `assignment -1 id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark -1 id/CS2103T:INDEX151 mark/5` | A displayed index cannot be negative. | <samp>Index must be a positive integer.</samp> |
| IDENTIFIER-6 | `assignment abc id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark abc id/CS2103T:INDEX151 mark/5` | `abc` is neither an index nor an `m/` matriculation number. | <samp>Specify the student by either INDEX or m/MATRICULATION_NUMBER.</samp> followed by that command's usage, which shows both the index and `m/` worked examples. |
| IDENTIFIER-7 | `assignment 1.5 id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark 1.5 id/CS2103T:INDEX151 mark/5` | A displayed index cannot be a decimal. | <samp>Index must be a positive integer.</samp> |
| IDENTIFIER-8 | `assignment 1 2 id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark 1 2 id/CS2103T:INDEX151 mark/5` | Only one student identifier is allowed; the space makes the identifier malformed. | <samp>Invalid command format!</samp> followed by that command's usage. |
| IDENTIFIER-9 | `assignment 99 id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark 99 id/CS2103T:INDEX151 mark/5` | Index 99 is outside the displayed list. | <samp>The student index provided is invalid.</samp> |
| IDENTIFIER-10 | `assignment A1234567X id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark A1234567X id/CS2103T:INDEX151 mark/5` | A bare matriculation number is missing the `m/` prefix. | <samp>Specify the student by either INDEX or m/MATRICULATION_NUMBER.</samp> followed by that command's usage, which shows both the index and `m/` worked examples. |
| IDENTIFIER-11 | `assignment mat/A1234567X id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark mat/A1234567X id/CS2103T:INDEX151 mark/5` | `mat/` is not a supported prefix or alias for `m/`. | <samp>Invalid command format!</samp> followed by that command's usage. |
| IDENTIFIER-12 | `assignment 2147483648 id/CS2103T:REJECT151 n/Rejected Regression max/20`<br>`mark 2147483648 id/CS2103T:INDEX151 mark/5` | The index is larger than the largest supported integer. | <samp>The student index provided is invalid.</samp> |
| IDENTIFIER-13 | `mark 1 mark/5` | The required `id/` prefix is missing. | <samp>Missing required prefix: id/</samp>, followed by usage containing `mark 1 id/CS2103T:A1 mark/17.5` and `mark m/A1234567X id/CS2103T:A1 mark/17.5`. |
| IDENTIFIER-14 | `mark 1 id/CS2103T:INDEX151` | The required `mark/` prefix is missing. | <samp>Missing required prefix: mark/</samp>, followed by the same two worked examples. |
| IDENTIFIER-15 | `assignment 1` | The required `id/` prefix is missing. | <samp>Invalid command format!</samp>, followed by usage containing `assignment 1 id/CS2103T:A1 n/Assignment One max/20`, its matriculation counterpart, and both shorter existing-work examples. |

### Recording an assignment mark

Run these steps in order without restarting until instructed. Before starting, ensure student `A1234567X` exists and has been assigned `CS2103T:A1` with maximum `20`.

1. Enter `mark m/A1234567X id/CS2103T:A1 mark/0`.<br>
   Expected: TAssist confirms a recorded score of <samp>0/20</samp>.
1. Enter the same command with `mark/20`.<br>
   Expected: TAssist accepts a score equal to the maximum and confirms <samp>20/20</samp>.
1. Enter `mark m/A1234567X id/CS2103T:A1 mark/17.5`.<br>
   Expected: TAssist confirms the score was updated to <samp>17.5/20</samp>.
1. Run cases MARK-1 and MARK-2 in the invalid-input table below separately. After each rejection, enter `marks id/CS2103T:A1`.<br>
   Expected: The mark remains <samp>17.5/20</samp>, and assignment data remains unchanged. Do not restart yet; persistence is checked later.
1. Enter `undo`, then `marks id/CS2103T:A1`.<br>
   Expected: Undo succeeds and the mark returns to <samp>20/20</samp>, confirming the rejected commands added no history entries.
1. Run case MARK-2 again, then `redo`, then `marks id/CS2103T:A1`.<br>
   Expected: <samp>Missing required prefix: id/</samp> followed by `mark` usage, then a successful redo restoring <samp>17.5/20</samp>. This confirms rejection preserves pending redo history and restores the score needed by later steps.
1. Run cases MARK-3 to MARK-12 separately. After each rejection, enter `marks id/CS2103T:A1`.<br>
   Expected: Each command fails with the stated message; the mark remains <samp>17.5/20</samp>.
1. Close and reopen TAssist. Enter `marks id/CS2103T:A1`.<br>
   Expected: The student's score remains <samp>17.5/20</samp>, and the assignment maximum remains <samp>20</samp>.
1. Record the mark with `mark m/A1234567X id/CS2103T:A1 mark/1.7500e1`.<br>
   Expected: TAssist accepts the scientific notation and confirms a normalized score of <samp>17.5/20</samp>.
1. Repeat with `mark/0e2147483647`.<br>
   Expected: TAssist accepts the zero mark and confirms a score of <samp>0/20</samp>.

#### Invalid inputs

Run each case at the point indicated in the steps above. Every rejected command preserves the assignment data and undo/redo history.

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| MARK-1 | `mark m/A1234567X id/CS2103T:A1 mark/21` | The score exceeds the assignment maximum of 20. | <samp>Mark cannot exceed the maximum mark of 20.</samp> The previous score of <samp>17.5/20</samp> remains stored. |
| MARK-2 | `mark m/A1234567X i/CS2103T:A1 mark/5` | The misspelled `i/` does not supply the required `id/` prefix. | <samp>Missing required prefix: id/</samp>, followed by `mark` usage. |
| MARK-3 | `mark m/A1234567X mark/5` | The required `id/` prefix is missing. | <samp>Missing required prefix: id/</samp>, followed by `mark` usage. |
| MARK-4 | `mark m/A1234567X id/CS2103T:A1` | The required `mark/` prefix is missing. | <samp>Missing required prefix: mark/</samp>, followed by `mark` usage. |
| MARK-5 | `mark m/A1234567X` | Both `id/` and `mark/` are missing; `id/` is checked first. | <samp>Missing required prefix: id/</samp>, followed by `mark` usage. |
| MARK-6 | `mark m/ id/CS2103T:A1 mark/5` | The matriculation number is empty. | <samp>Matriculation numbers should only start with an A, followed by 7 digits, and end with a letter</samp> |
| MARK-7 | `mark m/A1234567X id/ mark/5` | The assignment ID is empty. | <samp>Assignment IDs should be MODULE:CODE, where CODE starts with a letter and contains only letters and digits (e.g. CS2103T:A1).</samp> |
| MARK-8 | `mark m/A1234567X id/CS2103T:A1 mark/` | The mark value is empty. | <samp>Mark must be a valid number.</samp> |
| MARK-9 | `mark m/A1234567X id/CS2103T:A1 id/CS2103T:A1 mark/5` | The single-valued `id/` prefix is repeated. | <samp>Multiple values specified for the following single-valued field(s): id/</samp> |
| MARK-10 | `mark m/A1234567X id/CS2103T:A/1 mark/5` | The assignment code contains a slash. | <samp>Assignment IDs should be MODULE:CODE, where CODE starts with a letter and contains only letters and digits (e.g. CS2103T:A1).</samp> The slash-containing value is not reported as an unknown prefix. |
| MARK-11 | `mark m/A1234567X id/CS2103T:A1 mark/1e1000000` | The mark's displayed decimal form exceeds 64 characters. | The score is rejected as too long for display; the previous score remains stored. |
| MARK-12 | `mark m/A1234567X id/CS2103T:A1 mark/100e2147483647` | The mark's displayed decimal form exceeds 64 characters. | <samp>Mark's displayed decimal form must be at most 64 characters.</samp> is shown. The existing mark and saved data are unchanged. |

### Recording marks with a decimal maximum

Prerequisites: Start in a fresh test folder with the original six sample students. Run `list`, then `enroll 1 c/CS2103T-T10` to enroll Alex Yeoh (`A1234567X`) in `CS2103T`. Ensure assignment ID `CS2103T:B1` and assignment name `Lab B1` are unused. Run these steps in order.

1. Enter `assignment m/A1234567X id/CS2103T:B1 n/Lab B1 max/1.025e1`.<br>
   Expected: <samp>Assigned CS2103T:B1 (out of 10.25) to student A1234567X.</samp>
1. Enter `mark m/A1234567X id/CS2103T:B1 mark/10.25`.<br>
   Expected: <samp>Recorded 10.25/10.25 for A1234567X on CS2103T:B1.</samp> A score equal to the maximum is accepted.
1. Run case DECIMAL-1 in the invalid-input table below, then run `marks id/CS2103T:B1` and reopen the app.<br>
   Expected: The assignment maximum and accepted score remain <samp>10.25/10.25</samp>.
1. Run cases DECIMAL-2 and DECIMAL-3 separately.<br>
   Expected: Both are rejected without changing the score or saved records.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| DECIMAL-1 | `mark m/A1234567X id/CS2103T:B1 mark/10.26` | The score exceeds the assignment's decimal maximum of 10.25. | <samp>Mark cannot exceed the maximum mark of 10.25.</samp> The accepted score and assignment maximum remain unchanged. |
| DECIMAL-2 | `mark m/A1234567X id/CS2103T:B1 mark/-1` | Marks must be nonnegative. | The score is rejected; the accepted score and saved records remain unchanged. |
| DECIMAL-3 | `mark m/A1234567X id/CS2103T:B1 mark/abc` | The mark is not a valid number. | The score is rejected; the accepted score and saved records remain unchanged. |

### Viewing assignment marks

Prerequisites: Ensure `CS2103T:A1` is assigned to multiple students, including Alex Yeoh and at least one student whose name does not contain the word `Alex`. Record a mark for at least one assigned student and leave at least one other assigned student unmarked. Run `list` to show all students before starting.

1. After recording marks for `CS2103T:A1`, enter `marks id/CS2103T:A1`.<br>
   Expected: TAssist shows the assignment name and maximum, then every assigned student in matriculation number order. Recorded scores appear as <samp>MARK/MAX_MARK</samp>; students without a score appear as <samp>Unmarked</samp>.
1. Enter `find Alex`. Confirm that Alex Yeoh is displayed and at least one other assigned student is hidden. Then enter `marks id/CS2103T:A1` again.<br>
   Expected: The marks result still contains every assigned student, including those hidden by `find Alex`, with the same scores and unmarked statuses as before. The student list still shows only the students matching `Alex`, in the same order as immediately before the `marks` command.

### Viewing marks in a student profile

Run these steps in order in a test data folder. Ensure matriculation numbers `A9900204Z`, `A9900205Z`, and `A9900206Z`, assignment IDs `CS2103T:TEST204A` and `CS2103T:TEST204B`, and assignment names `Refresh Quiz` and `Refresh Homework` are unused. No pre-existing assignments or marks are needed. Keep the details window open and enter commands in the main window unless instructed otherwise.

1. Enter `add n/Refresh Test m/A9900204Z e/refresh204@example.com` and `add n/Other Profile m/A9900206Z e/other204@example.com`. Enter `find m/A9900204Z`, `enroll 1 c/CS2103T-T10`, then `view 1`.<br>
   Expected: Refresh Test is the only displayed student. The details window shows <samp>None assigned</samp>.
1. Enter `assignment m/A9900204Z id/CS2103T:TEST204A n/Refresh Quiz max/10`.<br>
   Expected: The open window immediately shows the quiz and <samp>Mark: Unmarked</samp>, without another `view`.
1. Enter `mark m/A9900204Z id/CS2103T:TEST204A mark/8.5`, then `mark m/A9900204Z id/CS2103T:TEST204A mark/10`.<br>
   Expected: The visible mark changes to <samp>8.5/10</samp>, then <samp>10/10</samp>, immediately after each successful command. Keyboard focus stays in the main window.
1. Enter `view 1` again without closing the window.<br>
   Expected: The same window shows the latest score, <samp>Mark: 10/10</samp>, with no duplicate assignment rows.
1. Enter `assignment m/A9900204Z id/CS2103T:TEST204B n/Refresh Homework max/20`.<br>
   Expected: The homework immediately appears as <samp>Mark: Unmarked</samp> after the quiz in assignment ID order. The quiz still shows <samp>Mark: 10/10</samp>.
1. Enter `edit 1 m/A9900205Z`.<br>
   Expected: Without another `view`, the open window shows <samp>A9900205Z</samp>, both assignments, and the quiz's <samp>10/10</samp> mark. Enter `marks id/CS2103T:TEST204A` to confirm the saved result is associated with the new number.
1. Enter `undo`.<br>
   Expected: <samp>Undo successful.</samp> is displayed, and the details window closes.
1. Enter `find m/A9900204Z`, then `view 1`.<br>
   Expected: Both assignments and the quiz's <samp>10/10</samp> mark appear under the original number.
1. Enter `redo`.<br>
   Expected: <samp>Redo successful.</samp> is displayed, and the window closes again.
1. Enter `find m/A9900205Z`, then `view 1`.<br>
   Expected: Both assignments and the quiz's <samp>10/10</samp> mark appear under the new number.
1. Enter `find m/A9900206Z`, then `view 1`.<br>
   Expected: The reused window shows Other Profile and <samp>None assigned</samp>, with no rows left over from Refresh Test. Enter `find m/A9900205Z`, then `view 1`; Refresh Test's two assignments and <samp>10/10</samp> mark reappear.
1. Enter `removeassignment id/CS2103T:TEST204B m/A9900205Z`.<br>
   Expected: The homework disappears immediately; the quiz remains with <samp>Mark: 10/10</samp>.
1. Enter `removeassignment id/CS2103T:TEST204A`.<br>
   Expected: The quiz definition is removed and the open profile immediately shows <samp>None assigned</samp>.
1. Close the details window with `Esc`. Enter `assignment m/A9900205Z id/CS2103T:TEST204B`, then `mark m/A9900205Z id/CS2103T:TEST204B mark/0`.<br>
   Expected: Both commands succeed and the window stays closed. Enter `view 1`; the homework appears with <samp>Mark: 0/20</samp>.
1. Enter `delete 1`.<br>
   Expected: The details window closes. Enter `marks id/CS2103T:TEST204B`; its shared definition remains, but its results show <samp>None assigned.</samp>
1. Enter `undo`.<br>
   Expected: <samp>Undo successful.</samp> is displayed, and the deleted student is restored.
1. Enter `find m/A9900205Z`, then `view 1`.<br>
   Expected: The restored student has the homework and <samp>Mark: 0/20</samp>.
1. Close and reopen TAssist, then enter `find m/A9900205Z` and `view 1`.<br>
   Expected: The same assignment and mark remain stored.

### Removing assignments

1. Assign `CS2103T:A1` to two students and record a mark for each. Enter `removeassignment id/CS2103T:A1 m/A1234567X`.<br>
   Expected: TAssist confirms removal from that student. `marks id/CS2103T:A1` still shows the other student's mark, and both student records remain.
1. Run case REMOVE-1 in the table below.
1. Enter `removeassignment id/CS2103T:A1` and close and reopen TAssist.<br>
   Expected: Both students remain.
1. Run case REMOVE-2 in the table below.
1. Create a new assignment with the same ID and name.<br>
   Expected: The assignment is created successfully, because the old definition no longer exists.

#### Invalid inputs

| Case | Invalid input | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| REMOVE-1 | `removeassignment id/CS2103T:A1 m/A1234567X` (after the first removal) | The assignment is no longer attached to this student. | TAssist reports that the assignment is not attached; the other student's mark is unchanged. |
| REMOVE-2 | `marks id/CS2103T:A1` (after removing the definition and reopening) | The assignment definition has been removed. | TAssist reports an unknown assignment; both student records remain. |

### Saving data

1. Dealing with missing/corrupted data files

   1. Close TAssist and back up `data/addressbook.json` before testing.
   1. Move the data file out of the `data` folder and reopen TAssist.<br>
      Expected: Sample student records appear and commands can be executed.
   1. Run cases SAVE-1 to SAVE-10 in the invalid-data table below separately. Complete all three checks for one
      case before moving to the next case:

      1. Close TAssist, restore the valid backup, apply only the change in that row, and make a copy of the
         modified file. Reopen TAssist.<br>
         Expected: The result display reports the file path, failure reason, and recovery instructions.
         The command box is disabled. The data file is unchanged after startup.
      1. Click the command box, attempt to type `list`, and press Enter.<br>
         Expected: The command box does not accept input or execute a command. The failure message remains
         visible; scroll the result display to read the full recovery instructions if necessary. `list` is a valid
         command, but commands are unavailable while restoration has failed.
      1. Close the app using the window's close button, and compare the data file with the copy made
         immediately after modification.<br>
         Expected: Every byte remains unchanged through startup, the command attempt, and shutdown. For the
         two prepended-object cases, this includes the original student records in the second object.

   1. Close TAssist, restore the valid backup, and append only spaces, tabs, and line breaks. Reopen TAssist,
      run `list`, and close and reopen the app.<br>
      Expected: Records load normally, commands remain available, and the saved records survive reopening.
   1. Close TAssist, correct the file or replace it with the valid backup, and reopen TAssist.<br>
      Expected: The saved student and attendance records are restored, the command box is enabled,
      and `list` executes successfully without a restoration failure message.
      Alternatively, delete the file while the app is closed and reopen it to start with sample records.

#### Invalid data

| Case | Invalid input in `data/addressbook.json` | Why it is invalid | Expected result |
| --- | --- | --- | --- |
| SAVE-1 | Remove the JSON object's closing brace. | The file is no longer syntactically valid JSON. | A restoration failure reports the malformed JSON; commands remain blocked and the file is preserved. |
| SAVE-2 | Replace a student's email with `zoe..test@example.com`, keeping the JSON valid. | Consecutive dots are not permitted in the email's local part. | A restoration failure reports the invalid email; commands remain blocked and the file is preserved. |
| SAVE-3 | Replace an attendance record's date with `31-02-2026`, keeping the JSON valid. | 31 February is not a valid calendar date. | A restoration failure reports the invalid date; commands remain blocked and the file is preserved. |
| SAVE-4 | Duplicate one student's attendance record for the same class and date. | A student cannot have two attendance records for the same class and date. | A restoration failure reports the duplicate record; commands remain blocked and the file is preserved. |
| SAVE-5 | Use a date-only attendance entry, e.g., `"attendance": ["07-10-2026"]`. | Attendance entries must contain both a date and a class code, rather than a date-only string. | A restoration failure reports the invalid attendance data; commands remain blocked and the file is preserved. |
| SAVE-6 | Set an assignment mark above that assignment's maximum. | A recorded mark cannot exceed its assignment maximum. | A restoration failure reports the invalid mark; commands remain blocked and the file is preserved. |
| SAVE-7 | Prepend `{"persons":[]}` directly before the valid backup's contents. | The file contains two root JSON values instead of one. | A restoration failure rejects the additional root value; commands remain blocked and both objects are preserved byte for byte. |
| SAVE-8 | Prepend `{"persons":[]}` and a newline before the valid backup's contents. | A newline does not make multiple root JSON values valid. | A restoration failure rejects the additional root value; commands remain blocked and both objects are preserved byte for byte. |
| SAVE-9 | Append `true` to the valid backup. | The Boolean is an additional root JSON value after the saved data. | A restoration failure rejects the additional root value; commands remain blocked and the file is preserved. |
| SAVE-10 | Append `garbage` to the valid backup. | Non-whitespace text after the root JSON value is invalid trailing content. | A restoration failure rejects the trailing content; commands remain blocked and the file is preserved. |
