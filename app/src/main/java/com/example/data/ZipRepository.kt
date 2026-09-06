package com.example.data

import kotlinx.coroutines.flow.Flow

class ZipRepository(private val zipDao: ZipDao) {

    val userAccount: Flow<UserAccount?> = zipDao.getUserAccountFlow()
    val transactions: Flow<List<Transaction>> = zipDao.getAllTransactions()
    val rewards: Flow<List<RewardCard>> = zipDao.getAllRewards()

    suspend fun getUserDirect(): UserAccount? = zipDao.getUserAccountDirect()

    suspend fun updateUser(user: UserAccount) {
        zipDao.updateUser(user)
    }

    suspend fun saveUser(user: UserAccount) {
        zipDao.insertOrUpdateUser(user)
    }

    suspend fun processPayment(
        recipientTitle: String,
        recipientUpiId: String,
        amount: Double,
        category: String,
        note: String
    ): Pair<Boolean, String> {
        val user = zipDao.getUserAccountDirect() ?: return Pair(false, "User account not initialized")

        if (amount <= 0) {
            return Pair(false, "Invalid payment amount")
        }

        if (user.balance < amount) {
            return Pair(false, "Insufficient balance! Your current balance is ₹${user.balance}")
        }

        if (user.spentToday + amount > user.dailyLimit) {
            val remaining = (user.dailyLimit - user.spentToday).coerceAtLeast(0.0)
            return Pair(
                false,
                "Daily spending limit reached! You can spend up to ₹${user.dailyLimit}/day. Remaining limit today: ₹$remaining. Ask parents to increase limit."
            )
        }

        // Execute payment
        val updatedUser = user.copy(
            balance = user.balance - amount,
            spentToday = user.spentToday + amount
        )
        zipDao.updateUser(updatedUser)

        val txn = Transaction(
            title = recipientTitle,
            upiId = recipientUpiId,
            amount = amount,
            type = "PAYMENT",
            category = category,
            note = note,
            status = "SUCCESS"
        )
        zipDao.insertTransaction(txn)

        return Pair(true, "Payment of ₹$amount to $recipientTitle successful!")
    }

    suspend fun addAllowance(amount: Double, note: String): String {
        val user = zipDao.getUserAccountDirect() ?: return "User error"
        val updatedUser = user.copy(balance = user.balance + amount)
        zipDao.updateUser(updatedUser)

        val txn = Transaction(
            title = "Allowance / Pocket Money",
            upiId = "parent@upi",
            amount = amount,
            type = "ALLOWANCE",
            category = "Allowance",
            note = note,
            status = "SUCCESS"
        )
        zipDao.insertTransaction(txn)
        return "₹$amount allowance added to Zip Wallet!"
    }

    suspend fun scratchReward(reward: RewardCard) {
        if (reward.isScratched) return
        val updatedReward = reward.copy(isScratched = true)
        zipDao.updateReward(updatedReward)

        // Add cashback to user balance
        val user = zipDao.getUserAccountDirect()
        if (user != null) {
            zipDao.updateUser(user.copy(balance = user.balance + reward.cashbackAmount))
            zipDao.insertTransaction(
                Transaction(
                    title = "Cashback Reward",
                    upiId = "reward@zipupi",
                    amount = reward.cashbackAmount,
                    type = "CASHBACK",
                    category = "Reward",
                    note = reward.title,
                    status = "SUCCESS"
                )
            )
        }
    }
}
