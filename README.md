# Campus Connect

Campus Connect is a mobile application designed to improve university campus life by providing students and staff with a centralized platform for managing profiles, browsing events, receiving announcements, and customizing preferences using tags. With this app, users can stay connected, discover upcoming events, and manage their schedules efficiently.

## Features

### 1. User Profiles
- Create a personalized user profile with fields such as:
  - Name, email, phone number, and user type (e.g., student or staff).
  - Upload a profile picture directly from the device's gallery.
  - Login functionality to access personalized features.

### 2. Event Management
- Browse a list of upcoming university events.
- RSVP to events and add them to the calendar.

### 3. Announcements
- Receive campus-wide announcements, such as schedule changes or new policies.

### 4. Tags and Customization
- Add tags to events for categorization (e.g., sports, academics, entertainment).
- Subscribe to tags for customized notifications.

## Technologies Used
- **Language**: Kotlin
- **Database**: SQLite (integrated with Android’s Room API for efficient local storage).
- **User Interface**: XML layouts with Android Views and ViewGroups.
- **Image Handling**: Gallery access and image compression for profile photos.
- **Android Architecture**: MVVM (Model-View-ViewModel) for clear separation of concerns.

## Setup Instructions

### 1. Prerequisites
- Android Studio (latest version).
- Android Device or Emulator with API Level 21 (Lollipop) or higher.

### 2. Clone the Repository
```bash
git clone https://github.com/your-username/campus-connect.git
```
### 3. Open the Project
- Launch Android Studio.
- Open the project folder.

### 4. Build the Project
- Click on `Build > Make Project`.
- Sync Gradle if prompted.

### 5. Run the App
- Connect your Android device or start an emulator.
- Click the `Run` button in Android Studio.

## How to Use the App

### Sign Up:
- Fill in your details on the sign-up screen, including uploading a profile picture.
- Click the `Sign Up` button to register.

### Login:
- Use your email and password to log in to the app.

### Explore Events:
- View and RSVP to upcoming events.
- Filter events by tags for a personalized experience.

### Announcements:
- Access the latest announcements from the university.

## Project Structure
```bash
CampusConnect/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/icsa/campus_connect/
│   │   │   │   ├── activities/          # Activity classes (e.g., SignUpActivity, LoginActivity)
│   │   │   │   ├── database/            # SQLite Database Helper
│   │   │   │   ├── repository/          # Repository classes for database operations
│   │   │   │   ├── models/              # Data models (e.g., UserModel, EventModel)
│   │   ├── res/
│   │   │   ├── layout/                  # XML layout files
│   │   │   ├── drawable/                # Image assets and custom styles
│   │   │   ├── values/                  # Strings, colors, and themes
│   ├── build.gradle                     # Gradle build script
├── README.md                            # Project documentation
```

## Future Enhancements

- **Push Notifications**: Notify users of upcoming events or new announcements in real time.
- **Event Analytics**: Show statistics like event attendance trends and user participation.
- **Integration with University Systems**: Sync events and announcements directly with university platforms.

## Contributors
- **Ann Wangari**
  - **Role**: Programmer 1
  - **Focus**: Profile and Authentication modules.

- **Arnold Oguda**
  - **Role**: Programmer 4
  - **Focus**: API and Database Endpoint integration.

(Additional team members can be added here)

