package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserAccount::class, Transaction::class, RewardCard::class],
    version = 1,
    exportSchema = false
)
abstract class ZipDatabase : RoomDatabase() {
    abstract fun zipDao(): ZipDao

    companion object {
        @Volatile
        private var INSTANCE: ZipDatabase? = null

        fun getDatabase(context: Context): ZipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZipDatabase::class.java,
                    "zip_pay_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial defaults in background thread
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).zipDao()
                            seedInitialData(dao)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: ZipDao) {
            val user = UserAccount(
                id = 1,
                name = "Aarav Sharma",
                upiId = "aarav@zipupi",
                age = 16,
                aadhaarNumber = "5849-2049-8812",
                isAadhaarVerified = true,
                bankName = "HDFC Minor Savings",
                bankAccountNumber = "XXXX4920",
                isBankLinked = true,
                balance = 1250.0,
                dailyLimit = 500.0,
                spentToday = 85.0,
                monthlyLimit = 5000.0,
                upiPin = "1234",
                parentMobile = "+91 9876543210",
                streakDays = 5,
                avatarEmoji = "⚡"
            )
            dao.insertOrUpdateUser(user)

            // Seed initial transactions
            val initialTxns = listOf(
                Transaction(
                    title = "Sharma Kirana Store",
                    upiId = "sharmakirana@okicici",
                    amount = 45.0,
                    type = "PAYMENT",
                    category = "Food & Snacks",
                    note = "Cold drink & chips",
                    timestamp = System.currentTimeMillis() - 3600000 * 2
                ),
                Transaction(
                    title = "Allowance from Mom",
                    upiId = "mom@sbi",
                    amount = 500.0,
                    type = "ALLOWANCE",
                    category = "Allowance",
                    note = "Weekly pocket money ❤️",
                    timestamp = System.currentTimeMillis() - 3600000 * 24
                ),
                Transaction(
                    title = "Stationery World",
                    upiId = "stationery@paytm",
                    amount = 40.0,
                    type = "PAYMENT",
                    category = "Stationery",
                    note = "Class notebook",
                    timestamp = System.currentTimeMillis() - 3600000 * 48
                )
            )
            initialTxns.forEach { dao.insertTransaction(it) }

            // Seed initial rewards
            val initialRewards = listOf(
                RewardCard(
                    title = "Scanner Streak Cashback",
                    description = "Pay via QR 3 times this week",
                    cashbackAmount = 25.0,
                    isScratched = false
                ),
                RewardCard(
                    title = "Aadhaar e-KYC Welcome Reward",
                    description = "Verified minor account via Aadhaar",
                    cashbackAmount = 50.0,
                    isScratched = true
                )
            )
            initialRewards.forEach { dao.insertReward(it) }
        }
    }
}
