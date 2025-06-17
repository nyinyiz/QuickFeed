# 📱 QuickFeed App

QuickFeed is a lightweight Android social media app for user sign-up, login, text/image posts, and a real-time timeline.

## 📸 Screenshots


## ✨ Features
- **Authentication**: Sign-up, login, logout via Firebase Authentication.
- **Content Posting**: Post text with optional single images.
- **Real-time Timeline**: Live feed of all user posts using Firebase Firestore.

## 🛠️ Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM, Clean Architecture
- **Async**: Coroutines & Flow
- **DI**: Hilt
- **Backend**: 
  - Firebase (Authentication, Firestore)
  - Supabase (Cloud Storage)
  - Firebase App Check
- **Structure**: Multi-module (`:app`, `:data`, `:domain`, `:common`)

## 🚀 Setup & Run

### 1. Create Firebase Project
- Go to [Firebase Console](https://console.firebase.google.com).
- Click **Add project**, name it (e.g., `QuickFeed`).

### 2. Register Android App
- In Firebase, add an Android app.
- Use `applicationId` from `app/build.gradle.kts` (e.g., `com.yourname.quickfeed`).
- Download `google-services.json` and place it in `app/`.

### 3. Set Up Supabase
- Go to [Supabase Dashboard](https://supabase.com/dashboard).
- Create a new project.
- In **Settings > API**, copy:
  - **URL** (Supabase URL)
  - **anon public** key (Supabase Anon Key)
- Add to `local.properties` in the project root:
  ```
  SUPABASE_URL=your_supabase_url
  SUPABASE_ANON_KEY=your_supabase_anon_key
  ```

### 4. Enable Firebase Services
- **Authentication**: Enable **Email/Password** in **Authentication > Sign-in method**.
- **Firestore**: Create database in **Test mode** at **Firestore Database > Create database**.

### 5. Configure App Check
- Run `./gradlew signingReport` to get **SHA-256** (debug variant).
- In Firebase **Project Settings**, add **SHA-256** under **Your apps**.
- In **App Check > Apps**, enforce for **Authentication** and **Cloud Firestore**.

### 6. Build & Run
- Open in Android Studio, build, and run on an emulator/device.

## 🏗️ Project Structure
- **:app**: UI (Compose), navigation, app wiring.
- **:domain**: Business logic, use cases, models.
- **:data**: Firebase/Supabase data sources, repository implementations.
- **:common**: Shared utilities (dispatchers, error handling).

## 🧪 Testing
- **Unit Tests**: Located in the `:domain` module to validate core business logic, independent of UI and data sources.
- **Execution**: Run tests using the following command in the project root:
- ```./gradlew test```


## 👤 Author

Created by **Nyi Nyi Zaw** (nyinyizaw.dev@gmail.com)
