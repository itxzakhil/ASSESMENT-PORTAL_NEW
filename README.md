# CodeAssess - Coding Assessment & Course Portal

CodeAssess is an Android assessment portal inspired by CodeTantra. It features dual role support (Admin & Student), interactive code execution, test-case auto-grading, course and assessment management, and student analytics dashboards.

---

## 📱 Features

- **Dual Mode (Role-Based Access)**:
  - **Instructor/Admin**: Create courses, design coding assessments, define hidden and public test cases, inspect student submissions, and view class analytics.
  - **Student**: Enroll in courses, open the coding environment with syntax highlighting, run code against custom inputs, submit for automated test verification, and track progress.
- **In-App Code Runner & Evaluation Engine**:
  - Supports Python, Java, C++, and Kotlin problem solving.
  - Validates output against sample and hidden test cases.
  - Immediate feedback with execution logs, test case diffs, and score calculations.
- **Local Persistence with Room Database**:
  - Full offline capability with pre-seeded assessment questions, test cases, and sample student profiles.
- **Modern Jetpack Compose UI**:
  - Sleek dark theme with Material 3 styling.

---

## 🚀 How to Download and Build the APK

### 1. Download directly from Google AI Studio
- Click on the **Settings** (gear icon / project menu) at the top-right of Google AI Studio.
- Select **"Export APK"** / **"Generate APK"** to download the installable `.apk` file directly to your phone or computer.
- Or select **"Export as ZIP"** to download the entire project source code.

### 2. Build APK using Android Studio / Terminal
If you cloned this repository from Git:
```bash
# Clone the repository
git clone https://github.com/itxzakhil/ASSESMENT-PORTAL.git
cd ASSESMENT-PORTAL

# Build Debug APK
./gradlew assembleDebug
```
The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📤 How to Push Changes to GitHub

If you want to push this project to your GitHub repository:

```bash
# Initialize git (if not already initialized)
git init

# Add remote repository
git remote add origin https://github.com/itxzakhil/ASSESMENT-PORTAL.git

# Stage all files
git add .

# Commit changes
git commit -m "Update CodeAssess Android app with build fixes and README"

# Push to main/master branch
git branch -M main
git push -u origin main --force
```

Alternatively, you can use the **"Push to GitHub"** button inside Google AI Studio's top-bar menu.

---

## 🛠 Tech Stack
- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Coroutines & Flow
- **Database**: Android Jetpack Room
- **Build System**: Gradle (Kotlin DSL)
