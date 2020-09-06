package com.example.myartisticroom.notifications

import com.example.myartisticroom.notifications.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String
)