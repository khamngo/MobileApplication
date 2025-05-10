# Food Ordering Application
## Overview
The Food Ordering Application is a mobile application built using Kotlin and Jetpack Compose for Android, integrated with Firebase for backend services. This application allows users to browse food items, add them to a cart, apply promotional codes, manage shipping addresses, and place orders. Notifications are handled locally within the app, and data is stored and synchronized using Firestore. The app leverages a ViewModel architecture to manage state and business logic efficiently.
## Key Features
- User Authentication: Uses Firebase Authentication to manage user login and registration
- Cart Management: Add, update, and clear cart items stored in Firestore
- Order Placement: Calculate subtotal, discounts, taxes, and shipping fees, then save orders to Firestore
- Shipping Address: Load, validate, and save shipping addresses from Firestore
- Promotional Codes: Apply discounts based on predefined promo codes (e.g., "Free Shipping", "5% off for orders above 5$")
- Notifications: Save and display local notifications (e.g., order confirmation) as NotificationItem objects in Firestore
- Real-time Updates: Sync cart and order data in real-time using Firestore listeners
## Prerequisites
- Android Studio: Latest version (e.g., Arctic Fox or higher) with Kotlin plugin.
- Node.js and npm: Version 18.x or 20.x (avoid Node.js v22 due to potential compatibility issues)
- Firebase Account: Access to Firebase Console for project setup
- Java Development Kit (JDK): Version 11 or higher
- Internet Connection: Required for Firebase integration and deployment
## Usage
- Login/Register: Use Firebase Authentication to log in or register
- Add to Cart: Browse food items and add them to the cart
- Checkout: Enter shipping details, apply promo codes, and place an order
- View Notifications: Check the notifications section to see order updates saved as NotificationItem in Firestore.
### Firestore Collections
- carts/{userId}/items: Stores cart items with fields like name, price, quantity.
- users/{userId}/shippingAddress/default: Stores the default shipping address.
- orders/{orderId}: Stores order details including userId, items, total, etc.
- users/{userId}/notifications/{notificationId}: Stores notifications with NotificationItem data class fields.
## Troubleshooting
- Error: ENOENT on deploy: Ensure Node.js version is 18.x or 20.x. Run npm cache clean --force and reinstall dependencies
- Firebase Authentication Issues: Verify google-services.json is correctly placed
- Firestore Permissions: Check Firestore Rules in the Firebase Console
- Notification Not Showing: Ensure POST_NOTIFICATIONS permission is granted on Android 13+
## Contributing
- Fork the repository
- Create a feature branch (git checkout -b feature/new-feature)
- Commit changes (git commit -m 'Add new feature')
- Push to the branch (git push origin feature/new-feature)
- Open a Pull Request
## Contact
For support or questions, contact hoangquyle11@gmail.com
## Link Figma Design
https://www.figma.com/design/nL7WPVvpkNuyt2ISamGSgg/Ung-Dung?node-id=0-1&t=d76lABNB3dorc4Jw-1
