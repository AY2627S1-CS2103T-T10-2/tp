---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

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
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

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

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

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
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
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

* has a need to manage a significant number of contacts
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage contacts faster than with a typical mouse-driven GUI application.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …                 | So that I can…                                                        |
|----------|--------------------------------------------|------------------------------|------------------------------------------------------------------------|
| `* * *`  | new user                                   | see usage instructions       | refer to instructions when I forget how to use the App                 |
| `* * *`  | user                                       | add a new person             |                                                                        |
| `* * *`  | user                                       | delete a person              | remove entries that I no longer need                                   |
| `* * *`  | user                                       | find a person by name        | locate details of persons without having to go through the entire list |
| `* *`    | user                                       | hide private contact details | minimize chance of someone else seeing them by accident                |
| `*`      | user with many persons in the address book | sort persons by name         | locate a person easily                                                 |

*{More to be added}*

### Use cases

These use cases follow the feature names and purposes in the feature specification. Restoring saved records is also included to cover the corresponding user story. For all use cases, the **System** is TAssist, the **Actor** is a university Computer Science tutor, and **MSS** means Main Success Scenario. Unless stated otherwise, TAssist is running.

#### UC01: Add a student

**MSS**

1. Tutor requests to add a student, supplying a name, matriculation number, and email address.
2. TAssist validates the details, adds the student to TAssist, and confirms the addition.

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

1. Tutor requests to delete a student using their matriculation number.
2. TAssist deletes the matching student from TAssist and confirms the deletion.

Use case ends.

**Extensions**

* 1a. The request has invalid syntax or the matriculation number is missing or malformed.

  * 1a1. TAssist explains the expected input without deleting a student.

  Use case resumes at step 1.

* 1b. No student has the supplied matriculation number.

  * 1b1. TAssist reports that the student does not exist and deletes nothing.

  Use case resumes at step 1.

#### UC03: Search students

**MSS**

1. Tutor requests to search for students by name.
2. TAssist displays the number of matching students and their list entries, including all students with the same matching name.

Use case ends.

**Extensions**

* 1a. The request has invalid syntax or the search name is missing or invalid.

  * 1a1. TAssist explains the error without changing any records.

  Use case resumes at step 1.

* 2a. No students match the name.

  * 2a1. TAssist reports zero results and displays an empty result list.

  Use case ends.

#### UC04: Record attendance

**MSS**

1. Tutor requests to record a student's attendance, supplying their matriculation number and a date.
2. TAssist records the student as present on that date and confirms the record.

Use case ends.

**Extensions**

* 1a. Tutor omits the date.

  * 1a1. TAssist uses the current date.

  Use case resumes at step 2.

* 1b. The request has invalid syntax or the matriculation number is missing, malformed, or does not belong to an existing student.

  * 1b1. TAssist explains the error without adding an attendance record.

  Use case resumes at step 1.

* 1c. The date is not a valid calendar date in the required `dd-mm-yyyy` format.

  * 1c1. TAssist explains the required date format without adding an attendance record.

  Use case resumes at step 1.

* 1d. Attendance has already been recorded for that student on that date.

  * 1d1. TAssist reports the existing record without adding a duplicate.

  Use case ends.

#### UC05: List all students

**MSS**

1. Tutor requests to list all students.
2. TAssist displays each stored student exactly once in the order maintained by TAssist and confirms that all students have been listed.

Use case ends.

**Extensions**

* 1a. Tutor supplies extra or unexpected input with the request.

  * 1a1. TAssist explains the expected input without changing any records.

  Use case resumes at step 1.

* 2a. TAssist has no stored student records.

  * 2a1. TAssist displays an empty list and reports that no student records were found.

  Use case ends.

* 2b. The stored student records cannot be loaded or are corrupted, or the student list cannot be displayed.

  * 2b1. TAssist reports the error.

  Use case ends.

#### UC06: View a student's profile

**Guarantees:** The current student list, its order, any active search or filter, and stored information remain unchanged. If the request fails, the previously displayed profile also remains unchanged.

**MSS**

1. Tutor requests to view a student's profile using the student's index in the currently displayed list.
2. TAssist displays the selected student's stored information, grouped into student particulars, class membership, and attendance, with attendance ordered from most recent to oldest, and confirms whose profile is shown.

Use case ends.

**Extensions**

* 1a. The index is missing, more than one index is supplied, or the index format is invalid.

  * 1a1. TAssist explains the expected input and keeps the previously displayed profile, student list, and stored information unchanged.

  Use case resumes at step 1.

* 1b. No students are currently displayed.

  * 1b1. TAssist asks the tutor to list students or change the search or filter, leaving the previously displayed profile and stored information unchanged.

  Use case ends.

* 1c. The index exceeds the size of the displayed list.

  * 1c1. TAssist reports the valid index range and keeps the previously displayed profile, student list, and stored information unchanged.

  Use case resumes at step 1.

* 2a. An optional particular, class membership, or attendance information is not available.

  * 2a1. TAssist indicates which information is unavailable in the relevant section and confirms whose profile is shown.

  Use case ends.

#### UC07: Restore student records when reopening the app

**Precondition:** The tutor has added a student or recorded attendance in an earlier session, and the change was saved successfully.

**MSS**

1. Tutor closes TAssist and reopens it.
2. TAssist restores the saved student and attendance records.

Use case ends.

**Extensions**

* 2a. No saved data file exists.

  * 2a1. TAssist starts with an empty student list.

  Use case ends.

* 2b. The saved data file is unreadable or invalid.

  * 2b1. TAssist reports the restoration error and does not overwrite the file.

  Use case ends.


### Non-Functional Requirements

These requirements apply to the intended product, including future features where relevant. They specify quality targets and product constraints, not current test results. Compatibility, packaging, storage, and display requirements follow the applicable [CS2103T project constraints](https://nus-cs2103-ay2627-s1.github.io/website/admin/tp-constraints.html). The numerical performance and learnability thresholds below are proposed product acceptance targets, not course-mandated thresholds.

Performance checks use a desktop with at least two CPU cores, 8 GB RAM, an SSD, and Java 25, with no other resource-intensive applications running. The test dataset contains 1,000 students with 40 attendance records on distinct dates per student. For versions supporting module and class memberships or assignment marks, extend the dataset to distribute the students across 10 modules and 40 classes or include 10 assignment marks per student, as applicable. The test report shall record the actual hardware, OS, runtime versions, and dataset used.

1. **Platform compatibility:** The App shall run on Windows, Linux, and macOS with Java 25 as the only installed Java version. The same release shall support the documented workflows on all three platforms; any bundled native libraries shall support the platform being tested.
2. **Portability and distribution:** The App shall be distributed as a single JAR of at most 100 MB, with its required libraries bundled; if that is not feasible, the JAR and required files shall be packaged in one ZIP of at most 100 MB. Given Java 25, a tutor shall be able to launch it from a writable folder without an installer or additional software installation.
3. **Local, editable storage:** Persistent application data shall be held in local, human-editable text files, without a database management system. With the App closed, a valid edit to a documented student field using a text editor shall be reflected on the next launch. This assumes one tutor using one running instance with exclusive access to the data files.
4. **Capacity and responsiveness:** With the test dataset loaded, at least 95 of 100 executions of each specified operation shall display the result within 2 seconds of submission, after five warm-up executions per operation. These operations are adding a student, deleting a student, searching students, recording attendance, listing all students, and viewing a student's profile (UC01–UC06). The same target shall apply to student editing, module and class assignment, class filtering, assignment mark recording, and grade sorting when supported. Measurements include saving for operations that change data and exclude tutor input time. Restoring records on launch is covered by the startup performance requirement. Bulk file import is outside this latency target.
5. **Startup performance:** With the test dataset saved locally, the App shall restore the records and accept a command within 10 seconds of launch in at least 9 of 10 launches on the test desktop.
6. **Keyboard operability:** All student-management operations shall be completable using the keyboard alone after launch, including operations added in later iterations. Routine record operations shall accept all required input in one command, without requiring separate prompts for each field.
7. **Learnability:** At least 4 of 5 first-time users who are comfortable with CLI applications shall, with the User Guide available, complete a prepared exercise of adding a student, listing all students, searching students, viewing a student's profile, recording attendance, and deleting a student within 15 minutes without assistance. The exercise shall supply all required student particulars and attendance dates.
8. **Error tolerance:** Invalid user input shall not crash the App or change existing records. Error feedback shall identify the offending input or missing information and explain how to correct it. Check missing required parameters, unexpected parameters, invalid student details, duplicate matriculation numbers, invalid displayed indices, nonexistent students, invalid dates, and duplicate attendance for the same student and date. Later commands shall meet the same standard for their own parameters.
9. **Persistence reliability:** After the App reports a data-changing operation as successful, normal shutdown and relaunch shall preserve the resulting records, field values, and relationships, including additions, deletions, and recorded attendance. Verify a save-and-relaunch cycle for each supported data-changing operation. A simulated save failure shall be reported visibly and shall not be presented as a successful operation.
10. **Preservation on restoration failure:** A saved file that cannot be read or validated shall remain unmodified during the failed restoration attempt and subsequent normal shutdown. Verify this by comparing its contents before and after the attempt; the App shall report the failure rather than claim that records were restored.
11. **Offline availability:** All local student-management operations, including saving and restoring records, shall work with networking disabled and shall not depend on a remote server. Accessing online help or an external email service is outside this requirement.
12. **Display usability:** At 1920 x 1080 and higher resolutions with 100% or 125% scaling, command input, feedback, and student information shall remain readable without overlapping controls or inaccessible content. At 1280 x 720 and higher resolutions with 150% scaling, all functions shall remain accessible, allowing scrolling or resizing where necessary.

### Glossary

| Term | Definition |
|------|------------|
| Student record | The stored information for one student, including particulars, module and class memberships, and associated academic records. |
| Student particulars | The identifying and contact details held in a student record. The MVP stores name, matriculation number, and email address. |
| Matriculation number | The student's unique identifier. In the MVP, it is case-insensitive and consists of `A`, seven digits, and a final letter. It identifies the student for deletion and attendance recording. |
| Displayed index | A student's temporary position in the currently displayed list or search results. Profile viewing uses this index; it is not a permanent identifier. |
| Student profile | The view of one student's particulars, available class memberships, and attendance records. It displays stored memberships but does not assign students to classes. Later versions may also show assignment and grade information. |
| Module | A university course identified by a module code, such as CS2103T. |
| Class / class section | A tutorial or lab group within a module. A class identifier is interpreted together with its module code. |
| Class session | One occurrence of a tutorial or lab. This term is relevant to later class-aware attendance features; the MVP attendance command identifies an attendance record by student and date only. |
| Attendance record | In the MVP, a record that a student was present on a particular calendar date. The same student cannot have two records for the same date. No record does not mean the student was absent. |
| Assignment | An assessed piece of work within a module, identified separately from other assignments in that module. |
| Assignment mark | A student's numeric score on an assignment's marking scale. An unrecorded mark is not a zero mark. |
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
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
