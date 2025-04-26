/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

// Hàm xử lý IPN từ MoMo
exports.momoIPN = functions.https.onRequest(async (req, res) => {
  const data = req.body;
  const {
    partnerCode,
    orderId,
    requestId,
    amount,
    orderInfo,
    orderType,
    transId,
    resultCode,
    message,
    payType,
    responseTime,
    extraData,
    signature,
    userId, // Đảm bảo gửi userId từ client
  } = data;

  // Kiểm tra chữ ký (signature) để đảm bảo yêu cầu từ MoMo
  const secretKey = "YOUR_MOMO_SECRET_KEY"; // Thay bằng secretKey từ MoMo
  const rawSignature = `accessKey=YOUR_ACCESS_KEY&amount=${amount}&extraData=${extraData}&message=${message}&orderId=${orderId}&orderInfo=${orderInfo}&orderType=${orderType}&partnerCode=${partnerCode}&payType=${payType}&requestId=${requestId}&responseTime=${responseTime}&resultCode=${resultCode}&transId=${transId}`;
  const crypto = require("crypto");
  const calculatedSignature = crypto
    .createHmac("sha256", secretKey)
    .update(rawSignature)
    .digest("hex");

  if (calculatedSignature !== signature) {
    console.error("Invalid signature");
    res.status(400).send("Invalid signature");
    return;
  }

  try {
    const orderRef = admin
      .firestore()
      .collection("users")
      .doc(userId)
      .collection("orders")
      .doc(orderId);

    if (resultCode === 0) {
      // Thanh toán thành công
      await orderRef.update({ status: "completed" });
      console.log(`Order ${orderId} completed`);
    } else {
      // Thanh toán thất bại
      await orderRef.update({ status: "failed" });
      console.log(`Order ${orderId} failed: ${message}`);
    }

    res.status(200).send("OK");
  } catch (error) {
    console.error(`Error processing IPN for order ${orderId}:`, error);
    res.status(500).send("Internal Server Error");
  }
});
