# 🍔 Food Ordering Application

A **mobile food ordering application** built with **Kotlin** and **Jetpack Compose** for Android. The app integrates with **Firebase** for authentication, Firestore database, and cloud messaging.

---

## 📱 Features

- **🔐 User Authentication**: Firebase Authentication for login & registration  
- **🛒 Cart Management**: Add, update, and remove cart items (stored in Firestore)  
- **📦 Order Placement**: Calculate subtotal, discounts, taxes, shipping fees, and store orders in Firestore  
- **🏠 Shipping Address**: Save and validate shipping addresses  
- **🏷️ Promotional Codes**: Apply discounts with codes (e.g., `"FREESHIP"`, `"5%OFF"`)  
- **🔔 Notifications**: Local notifications saved in Firestore as `NotificationItem`  
- **🔄 Real-time Sync**: Automatically update cart and order info using Firestore listeners  

---

## 🚀 Tech Stack

- **Kotlin + Jetpack Compose**  
- **Firebase (Authentication, Firestore, Cloud Messaging)**  
- **MVVM Architecture**  
- **ViewModel + LiveData**  
- **Local Notifications**  

---

## ⚙️ Prerequisites

| Tool                | Required Version     |
|---------------------|----------------------|
| Android Studio      | Arctic Fox or newer  |
| Kotlin              | Latest plugin        |
| Java (JDK)          | 11 or higher         |
| Node.js & npm       | v18.x or v20.x ✅ (*avoid v22*) |
| Firebase Account    | ✅                   |
| Internet Connection | ✅ Required          |

---

## 📦 Installation

### 🔁 Step 1: Clone the Repository

```bash
git clone https://github.com/khamngo/MobileApplication.git
```

---

### 🔧 Step 2: Firebase Setup

1. **Create Firebase Project**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Click **"Add Project"**, name it (e.g., `FoodOrderingApp`)
   - Enable:
     - Authentication
     - Firestore Database
     - Cloud Messaging

2. **Download Config File**
   - Go to Project Settings → General  
   - Download `google-services.json`  
   - Place it inside the `app/` directory

3. **Set Firestore Rules**

```js
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /orders/{orderId} {
      allow write: if request.auth != null;
    }
    match /carts/{userId}/items/{itemId} {
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /users/{userId}/shippingAddress/{addressId} {
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    match /users/{userId}/notifications/{notificationId} {
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---

### 🧱 Step 3: Configure the Project

#### 1. Open in Android Studio
- Open the project directory
- Wait for Gradle sync to complete

#### 2. Add Required Dependencies

In `app/build.gradle`:

```kotlin
dependencies {
    // Firebase & Google
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.google.android.material:material:1.9.0")
    // Ktor
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    // Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Compose
    implementation("androidx.compose.material3:material3-window-size-class:1.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("com.google.accompanist:accompanist-pager:0.32.0")
    // Credential APIs
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
}
```

#### 3. Add Permissions in `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

### ▶️ Step 4: Run the App

1. Click **Build > Make Project**
2. Launch using a connected device or emulator

---

## 🧑‍💻 Usage Guide

| Feature         | Action                                         |
|-----------------|------------------------------------------------|
| 🔐 Login/Register | Register or login using Firebase Auth        |
| 🛒 Add to Cart   | Browse food items and add them to cart        |
| 💳 Checkout      | Enter shipping address, apply promo code, order |
| 🔔 Notifications | View local notifications (order updates)     |

---

## 🗃️ Firestore Structure

```
carts/{userId}/items             // Cart items
users/{userId}/shippingAddress   // User addresses
orders/{orderId}                 // Order details
users/{userId}/notifications     // Local notifications
```

---

## 🛠️ Troubleshooting

| Problem                     | Solution |
|-----------------------------|----------|
| ❌ `ENOENT` on deploy        | Use Node.js v18 or v20. Run `npm cache clean --force` |
| ❌ Firebase Auth not working | Check `google-services.json` placement |
| 🔒 Firestore access denied   | Re-check Firestore rules |
| 🔕 Notifications missing     | Ensure `POST_NOTIFICATIONS` permission granted on Android 13+ |

---

## 🧩 Contributing

1. Fork this repo
2. Create a feature branch  
   ```bash
   git checkout -b feature/new-feature
   ```
3. Commit and push your changes  
   ```bash
   git commit -m "Add new feature"
   git push origin feature/new-feature
   ```
4. Open a Pull Request

---

## 📬 Contact

For questions or support:  
📧 [hoangquyle11@gmail.com](mailto:hoangquyle11@gmail.com)

---

## 🎨 Figma Design

🔗 [Figma Link](https://www.figma.com/design/nL7WPVvpkNuyt2ISamGSgg/Ung-Dung?node-id=0-1&t=d76lABNB3dorc4Jw-1)
