package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_account")
data class UserAccount(
    @PrimaryKey val id: Int = 1,
    val name: String = "Aarav Sharma",
    val upiId: String = "aarav@zipupi",
    val age: Int = 16,
    val aadhaarNumber: String = "5849-2049-8812",
    val isAadhaarVerified: Boolean = true,
    val bankName: String = "HDFC Minor Savings",
    val bankAccountNumber: String = "XXXX4920",
    val isBankLinked: Boolean = true,
    val balance: Double = 1250.0,
    val dailyLimit: Double = 500.0,
    val spentToday: Double = 85.0,
    val monthlyLimit: Double = 5000.0,
    val upiPin: String = "1234",
    val parentMobile: String = "+91 9876543210",
    val streakDays: Int = 5,
    val avatarEmoji: String = "⚡"
)
