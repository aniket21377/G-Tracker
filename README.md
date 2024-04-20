# G-Tracker App README

## Overview

This README provides an overview of the Sensor application and its implementation details.

### App Setup

Upon installing the G-Tracker application, it will request permission to track your device's orientation using sensors. Once permission is granted, the app starts collecting orientation data and storing it in a SQLite database.

### Components

#### AppDatabase

The `AppDatabase` class in the `com.rockstar.sensor` package is a Room database class used for storing orientation data. Here's a summary:

- It extends RoomDatabase, part of the Room persistence library for SQLite databases on Android.
- Annotated with @Database, specifying the entities it contains and the database version.
- Contains a single abstract function `orientationDataDao()`, providing access to the DAO interface for interacting with the OrientationData entity.

Overall, the `AppDatabase` class serves as the entry point for accessing and managing the SQLite database used to store orientation data in the Sensor app.

#### DatabaseManager

The `DatabaseManager` object in the `com.rockstar.sensor` package provides a singleton instance of the `AppDatabase` class. Here's a summary:

- Ensures only one instance of the database is created and shared across the application.
- Contains a `getDatabase` function that returns an instance of the `AppDatabase`.
- Implements the singleton pattern using double-checked locking to ensure thread safety.

Overall, the `DatabaseManager` object acts as a central point for obtaining a reference to the Room database instance used in the  G-Tracker app.

#### MainActivity

The `MainActivity` in the `com.rockstar.sensor` package demonstrates sensor data collection and display in a Compose UI. Here's a summary of its key components:

- Sensor Management: Registers an accelerometer sensor and listens for changes in sensor values, storing orientation data in a Room database.
- UI Composition: Defines UI elements using Jetpack Compose. Displays the current orientation angles retrieved from sensor data.
- Database Interaction: Uses the `DatabaseManager` to obtain an instance of the Room database and inserts orientation data into the database when sensor values change.
- Navigation: Contains a button to navigate to another activity (MainActivity2) when clicked.
- Graphical Visualization: Fetches historical orientation data from the database and plots it using LineChartWrapper.

#### MainActivity2

The `MainActivity2` class is an AppCompatActivity that displays historical sensor data in a Compose UI. Here's a breakdown of its key features:

- Database Initialization: Initializes the Room database and logs the appropriate message.
- Compose UI: Defines UI elements, including styled text and historical graphs.
- Styled Text: Uses AnnotatedString.Builder to create styled text.
- Back Button: Creates a button to return to the previous activity.
- Navigation: Provides an intent to start MainActivity2.

### OrientationData

The `OrientationData` class represents data collected from sensors, particularly orientation data like pitch, roll, and yaw. Here's a breakdown:

- Annotations: Marked as an entity in the Room database with @Entity.
- Primary Key: The `id` field serves as the primary key.
- Columns: Represents orientation angles in degrees.
- Timestamp: Stores the time when the data was collected.

### OrientationDataDao

The `OrientationDataDao` interface defines the Data Access Object (DAO) for interacting with the OrientationData entity in the Room database. Here are its methods:

- `insert`: Inserts data into the database.
- `getAllOrientationData`: Retrieves all orientation data from the database.

By defining this DAO interface, Room provides a way to perform database operations related to orientation data.
## Conclusion

The  G-Tracker application provides a comprehensive solution for collecting, storing, and visualizing orientation data from sensors. Through its well-structured architecture and intuitive user interface, the app offers the following benefits:

- **Real-time Data Collection**: The app registers accelerometer sensors and captures orientation data in real-time, ensuring accurate and up-to-date information.

- **Historical Data Visualization**: Users can view historical orientation data through graphical visualization, enabling them to track trends and analyze patterns over time.

- **Database Management**: With the help of Room database and DatabaseManager, the app efficiently manages the storage and retrieval of orientation data, ensuring data integrity and reliability.

- **User-friendly Interface**: Utilizing Jetpack Compose, the app presents orientation data in a visually appealing and user-friendly manner, enhancing the overall user experience.

Overall, the  G-Tracker app serves as a valuable tool for various applications requiring orientation data, such as motion tracking, augmented reality, and gaming. With its robust features and seamless functionality, the app meets the needs of both casual users and professionals in the field of sensor data analysis.

