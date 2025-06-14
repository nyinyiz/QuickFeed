# 📱 QuickFeed App (Android Dev Test)

This is a micro-social media application for Android. It's a lite version of platforms like Twitter/X, allowing users to sign up, log in, create posts with text and images, and view a timeline of all user posts.

The project is built following modern Android development practices, emphasizing a clean, scalable, and testable architecture.

---

## ✨ Features

- **Authentication**: Secure Sign-Up, Login, and Logout functionality using Firebase Authentication.
- **Content Posting**: Users can create new posts with text content and optionally attach a single image.
- **Real-time Timeline**: A main feed screen that displays all posts from all users, updated in real-time using Firebase Firestore.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose for building the UI declaratively
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles
- **Asynchronous Programming**: Kotlin Coroutines & Flow for managing background threads and data streams
- **Dependency Injection**: Hilt for managing dependencies

### 🔗 Backend: Firebase

- **Firebase Authentication**: For user management
- **Firestore Database**: As a real-time NoSQL database for posts and user data
- **Firebase Cloud Storage**: For storing user-uploaded images
- **Firebase App Check**: To protect backend resources from abuse

### 🧱 Project Structure

- **Multi-module architecture** (`:app`, `:data`, `:domain`, `:common`) for better separation of concerns and build times

---

## 🚀 Firebase Setup & How to Run

To run this project, you need to connect it to your own Firebase backend. Follow these steps carefully.

### Step 1: Create a Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com).
2. Click **"Add project"** and follow the on-screen instructions to create a new project.
3. Name it anything (e.g., `SocialApp`).

### Step 2: Add Your Android App to Firebase

1. Inside your Firebase project, click the **Android icon** to add an app.
2. Register your app:
   - **Android package name**: Use the `applicationId` from `app/build.gradle.kts` (e.g., `com.yourname.socialapp`)
   - **App nickname**: Optional
   - **Debug signing certificate SHA-1**: Optional for now
3. Click **Register app**.

### Step 3: Add Firebase Config File

1. Download `google-services.json` from the setup screen.
2. In Android Studio, switch to **Project** view.
3. Place the file inside the `app/` directory.

### Step 4: Enable Firebase Services

#### Authentication

- Go to **Authentication > Sign-in method**.
- Enable **Email/Password** and click **Save**.

#### Firestore Database

- Go to **Firestore Database > Create database**.
- Choose **Test mode** and click **Enable**.

#### Cloud Storage

- Go to **Storage > Get Started**.
- Choose **Test mode**.

### Step 5: Configure App Check (Crucial Step)

#### Get Your SHA-256 Fingerprint

Run the command in the project root directory:

- **macOS/Linux**:
  ```bash
  ./gradlew signingReport
- **Windows**:
  ```bash
  gradlew signingReport

- Copy the **SHA-256** for the **debug** variant.

---

### 🔐 Add Fingerprint to Firebase

1. Go to **Firebase Console > Project Settings** (gear icon ⚙️).
2. Scroll down to **Your apps** section.
3. Click **Add fingerprint** and paste your **SHA-256**.

---

### ✅ Enforce App Check

1. In the Firebase Console, go to **App Check** (under **Build**).
2. Click the **Apps** tab.
3. Under the **Services** section, click **Enforce** for the following:
   - **Authentication**
   - **Cloud Firestore**

---

### ▶️ Step 6: Build and Run

You are all set! 🎉  
Now you can **run the app** on an emulator or a physical device.  
It will connect to your configured **Firebase backend**.

---

## 🏗️ Project Structure

The project uses a **multi-module architecture**:

- `:app`: Main Android app module. Contains UI (Jetpack Compose), navigation, and wires everything together.
- `:domain`: Core business logic. Contains pure Kotlin code like UseCases and models. This module is framework-agnostic.
- `:data`: Handles data sources (e.g., Firebase). Implements repository interfaces defined in `:domain`.
- `:common`: Shared utilities such as coroutine dispatchers, error handling, constants, etc.

---

## 🧪 Testing

- **Unit tests** for the business logic are located in the `:domain` module.
- These ensure that core functionalities work correctly, **independent of UI and data sources**.

