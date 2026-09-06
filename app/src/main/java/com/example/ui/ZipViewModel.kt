package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RewardCard
import com.example.data.Transaction
import com.example.data.UserAccount
import com.example.data.ZipDatabase
import com.example.data.ZipRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class PaymentSuccess(val txnTitle: String, val amount: Double, val refNo: String) : UiEvent()
}

class ZipViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ZipRepository

    val userAccount: StateFlow<UserAccount?>
    val transactions: StateFlow<List<Transaction>>
    val rewards: StateFlow<List<RewardCard>>

    // Navigation & Screen tab state
    private val _currentTab = MutableStateFlow("home") // "home", "scan", "kyc", "updates", "history", "my_qr", "request_allowance"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Scanner / Payment State
    private val _scannedUpiId = MutableStateFlow("sharmakirana@okicici")
    val scannedUpiId: StateFlow<String> = _scannedUpiId.asStateFlow()

    private val _scannedName = MutableStateFlow("Sharma Kirana Store")
    val scannedName: StateFlow<String> = _scannedName.asStateFlow()

    private val _paymentAmount = MutableStateFlow("45")
    val paymentAmount: StateFlow<String> = _paymentAmount.asStateFlow()

    private val _paymentCategory = MutableStateFlow("Food & Snacks")
    val paymentCategory: StateFlow<String> = _paymentCategory.asStateFlow()

    private val _paymentNote = MutableStateFlow("Cold drink")
    val paymentNote: StateFlow<String> = _paymentNote.asStateFlow()

    private val _enteredPin = MutableStateFlow("")
    val enteredPin: StateFlow<String> = _enteredPin.asStateFlow()

    private val _showPinModal = MutableStateFlow(false)
    val showPinModal: StateFlow<Boolean> = _showPinModal.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    // Allowance Request
    private val _allowanceAmount = MutableStateFlow("200")
    val allowanceAmount: StateFlow<String> = _allowanceAmount.asStateFlow()

    private val _allowanceNote = MutableStateFlow("Need for school books & canteen snacks")
    val allowanceNote: StateFlow<String> = _allowanceNote.asStateFlow()

    // Aadhaar KYC State
    private val _kycStep = MutableStateFlow(1) // 1: Info, 2: Aadhaar OTP, 3: Bank Link, 4: Set PIN
    val kycStep: StateFlow<Int> = _kycStep.asStateFlow()

    private val _aadhaarInput = MutableStateFlow("5849-2049-8812")
    val aadhaarInput: StateFlow<String> = _aadhaarInput.asStateFlow()

    private val _otpInput = MutableStateFlow("7890")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _selectedBank = MutableStateFlow("HDFC Minor Savings")
    val selectedBank: StateFlow<String> = _selectedBank.asStateFlow()

    private val _newUpiPin = MutableStateFlow("1234")
    val newUpiPin: StateFlow<String> = _newUpiPin.asStateFlow()

    // Transaction Details Modal
    private val _selectedTxn = MutableStateFlow<Transaction?>(null)
    val selectedTxn: StateFlow<Transaction?> = _selectedTxn.asStateFlow()

    // Voice sound simulation
    private val _voiceAlertMessage = MutableStateFlow<String?>(null)
    val voiceAlertMessage: StateFlow<String?> = _voiceAlertMessage.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        val database = ZipDatabase.getDatabase(application)
        repository = ZipRepository(database.zipDao())

        userAccount = repository.userAccount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserAccount()
        )

        transactions = repository.transactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        rewards = repository.rewards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(tab: String) {
        _currentTab.value = tab
    }

    fun setScannedDetails(upiId: String, name: String, amount: String = "50") {
        _scannedUpiId.value = upiId
        _scannedName.value = name
        _paymentAmount.value = amount
    }

    fun updatePaymentAmount(amount: String) {
        _paymentAmount.value = amount
    }

    fun updatePaymentCategory(category: String) {
        _paymentCategory.value = category
    }

    fun updatePaymentNote(note: String) {
        _paymentNote.value = note
    }

    fun openPinModal() {
        _enteredPin.value = ""
        _showPinModal.value = true
    }

    fun closePinModal() {
        _showPinModal.value = false
    }

    fun updateEnteredPin(pin: String) {
        if (pin.length <= 4) {
            _enteredPin.value = pin
        }
    }

    fun submitPaymentWithPin() {
        val user = userAccount.value ?: return
        if (_enteredPin.value != user.upiPin) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowToast("Incorrect 4-Digit UPI PIN! Default pin is 1234"))
            }
            return
        }

        _showPinModal.value = false
        _isProcessingPayment.value = true

        viewModelScope.launch {
            val amountVal = _paymentAmount.value.toDoubleOrNull() ?: 0.0
            val (success, message) = repository.processPayment(
                recipientTitle = _scannedName.value,
                recipientUpiId = _scannedUpiId.value,
                amount = amountVal,
                category = _paymentCategory.value,
                note = _paymentNote.value
            )

            _isProcessingPayment.value = false

            if (success) {
                // Voice note simulation sound text
                _voiceAlertMessage.value = "ZipPay Payment of ₹${amountVal.toInt()} to ${_scannedName.value} Successful!"
                _eventFlow.emit(UiEvent.PaymentSuccess(_scannedName.value, amountVal, "4029" + (100000..999999).random()))
                _currentTab.value = "history"
            } else {
                _eventFlow.emit(UiEvent.ShowToast(message))
            }
        }
    }

    fun clearVoiceAlert() {
        _voiceAlertMessage.value = null
    }

    fun requestAllowanceFromParent() {
        val amountVal = _allowanceAmount.value.toDoubleOrNull() ?: 0.0
        if (amountVal <= 0) return

        viewModelScope.launch {
            val msg = repository.addAllowance(amountVal, _allowanceNote.value)
            _eventFlow.emit(UiEvent.ShowToast("Parent Approved Allowance! $msg"))
            _currentTab.value = "home"
        }
    }

    fun updateAllowanceAmount(amount: String) {
        _allowanceAmount.value = amount
    }

    fun updateAllowanceNote(note: String) {
        _allowanceNote.value = note
    }

    // Aadhaar KYC methods
    fun setKycStep(step: Int) {
        _kycStep.value = step
    }

    fun updateAadhaarInput(num: String) {
        _aadhaarInput.value = num
    }

    fun updateOtpInput(otp: String) {
        _otpInput.value = otp
    }

    fun updateSelectedBank(bank: String) {
        _selectedBank.value = bank
    }

    fun updateNewUpiPin(pin: String) {
        if (pin.length <= 4) {
            _newUpiPin.value = pin
        }
    }

    fun completeAadhaarKycAndLinkBank(
        name: String,
        age: Int,
        customUpiHandle: String
    ) {
        val currentUser = userAccount.value ?: UserAccount()
        val updatedUser = currentUser.copy(
            name = name,
            age = age,
            upiId = if (customUpiHandle.endsWith("@zipupi")) customUpiHandle else "$customUpiHandle@zipupi",
            aadhaarNumber = _aadhaarInput.value,
            isAadhaarVerified = true,
            bankName = _selectedBank.value,
            isBankLinked = true,
            upiPin = _newUpiPin.value.ifBlank { "1234" }
        )

        viewModelScope.launch {
            repository.saveUser(updatedUser)
            _eventFlow.emit(UiEvent.ShowToast("Aadhaar e-KYC & Minor Bank Linked Successfully! 🎉"))
            _currentTab.value = "home"
        }
    }

    fun updateDailyLimit(newLimit: Double) {
        val currentUser = userAccount.value ?: return
        viewModelScope.launch {
            repository.updateUser(currentUser.copy(dailyLimit = newLimit))
            _eventFlow.emit(UiEvent.ShowToast("Daily spending limit updated to ₹${newLimit.toInt()}"))
        }
    }

    fun scratchRewardCard(reward: RewardCard) {
        viewModelScope.launch {
            repository.scratchReward(reward)
            _eventFlow.emit(UiEvent.ShowToast("🎉 You won ₹${reward.cashbackAmount.toInt()} Cashback added to wallet!"))
        }
    }

    fun selectTransaction(txn: Transaction?) {
        _selectedTxn.value = txn
    }
}
