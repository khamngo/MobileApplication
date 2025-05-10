import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();
const messaging = admin.messaging();

export const sendOrderNotification = functions.firestore
  .document("orders/{orderId}")
  .onCreate(async (snap) => {
    try {
      const order = snap.data();
      const userId = order.userId;
      const orderId = order.orderId;

      const userDoc = await db.collection("users").doc(userId).get();
      const fcmToken = userDoc.data()?.fcmToken;

      if (!fcmToken) {
        console.log(`No FCM token found for user ${userId}`);
        return null;
      }

      const message = {
        notification: {
          title: "Order Placed",
          body: `Your order #${orderId} has been placed successfully!`,
        },
        token: fcmToken,
      };

      await messaging.send(message);
      console.log(`Notification sent to user ${userId} for order ${orderId}`);

      await db.collection("users")
        .doc(userId)
        .collection("notifications")
        .add({
          title: "Order Placed",
          body: `Your order #${orderId} has been placed successfully!`,
          timestamp: admin.firestore.Timestamp.now(),
        });

      return null;
    } catch (error) {
      console.error("Error sending notification:", error);
      return null;
    }
  });