package com.example.foodorderingapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class MoMoRequest(
    val partnerCode: String,
    val requestId: String,
    val orderId: String,
    val amount: Long,
    val orderInfo: String,
    val redirectUrl: String,
    val ipnUrl: String,
    val requestType: String,
    val extraData: String,
    val lang: String,
    val signature: String
)

@Serializable
data class MoMoResponse(
    val payUrl: String,
    val resultCode: Int,
    val message: String
)