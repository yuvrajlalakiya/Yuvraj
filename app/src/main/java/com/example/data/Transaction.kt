package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val upiId: String,
    val amount: Double,
    val type: String, // "PAYMENT", "ALLOWANCE", "CASHBACK"
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS", // "SUCCESS", "PENDING", "FAILED"
    val category: String = "General", // "Food & Snacks", "Gaming", "Shopping", "Stationery", "Allowance"
    val refNo: String = "4029" + (100000..999999).random().toString(),
    val note: String = ""
)
