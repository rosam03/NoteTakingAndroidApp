
# Note Taking Android App

### Requirements
- Android Studio 2.2
- Android API 15+

### Dependencies
- Open source material design floating buttons: https://github.com/markushi/android-circlebutton . See Gradle files for dependency and implementation.

### Features
- Adds a new note, edits an existing note, and deletes a single or all notes
- Orders notes by most recently created/edited
- Previews note text from main activity

### Specs
- Material design theme supporting multiple screens with Relative Layout
- Saves note data to mobile persistent storage via relational database (SQLite)
- Backs up data to prevent data loss on app reinstall/update
- Uses a content provider for a standardized interface to access and manage data across the app
- Implements a loader to elegantly load data asynchronously in a background thread

