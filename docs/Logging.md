---
  layout: default.md
  title: "Logging guide"
---

# Logging guide

* The project uses the `java.util.logging` package for logging.
* `LogsCenter` manages logging levels and destinations.
* A class obtains a `Logger` with `LogsCenter.getLogger(Class)`, which logs messages at the configured level.
* Log messages are written to the console and to rotating log files (`addressbook.log.0`, `addressbook.log.1`, and so on) in the home folder: the folder containing the JAR file, or the working directory when the app is not run from a JAR (e.g., `./gradlew run`). If the log file cannot be opened, for example because the folder is read-only, a warning is printed and logging continues on the console only.
* The output logging level can be controlled by changing the `LOG_LEVEL` constant in the `LogsCenter` class.
* **When choosing a level for a log message**, follow the conventions given in [_[se-edu/guides] Java: Logging conventions_](https://se-education.org/guides/conventions/java/logging.html).
