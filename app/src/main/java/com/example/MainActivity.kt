package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.UiEvent
import com.example.ui.ZipViewModel
import com.example.ui.components.DigitalReceiptDialog
import com.example.ui.components.PinModalDialog
import com.example.ui.components.VoiceSoundBanner
import com.example.ui.components.ZipBottomNav
import com.example.ui.components.ZipTopBar
import com.example.ui.screens.AadhaarKycScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyQrScreen
import com.example.ui.screens.RequestAllowanceScreen
import com.example.ui.screens.ScanPayScreen
import com.example.ui.screens.UpdatesFeedScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ZipPayTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: ZipViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZipPayTheme {
                ZipPayAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ZipPayAppContent(viewModel: ZipViewModel) {
    val context = LocalContext.current

    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val scannedUpiId by viewModel.scannedUpiId.collectAsStateWithLifecycle()
    val scannedName by viewModel.scannedName.collectAsStateWithLifecycle()
    val paymentAmount by viewModel.paymentAmount.collectAsStateWithLifecycle()
    val paymentCategory by viewModel.paymentCategory.collectAsStateWithLifecycle()
    val paymentNote by viewModel.paymentNote.collectAsStateWithLifecycle()

    val enteredPin by viewModel.enteredPin.collectAsStateWithLifecycle()
    val showPinModal by viewModel.showPinModal.collectAsStateWithLifecycle()

    val allowanceAmount by viewModel.allowanceAmount.collectAsStateWithLifecycle()
    val allowanceNote by viewModel.allowanceNote.collectAsStateWithLifecycle()

    val kycStep by viewModel.kycStep.collectAsStateWithLifecycle()
    val aadhaarInput by viewModel.aadhaarInput.collectAsStateWithLifecycle()
    val otpInput by viewModel.otpInput.collectAsStateWithLifecycle()
    val selectedBank by viewModel.selectedBank.collectAsStateWithLifecycle()
    val newUpiPin by viewModel.newUpiPin.collectAsStateWithLifecycle()

    val selectedTxn by viewModel.selectedTxn.collectAsStateWithLifecycle()
    val voiceAlertMessage by viewModel.voiceAlertMessage.collectAsStateWithLifecycle()

    // Handle toast events
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is UiEvent.PaymentSuccess -> {
                    Toast.makeText(
                        context,
                        "🎉 Payment of ₹${event.amount.toInt()} to ${event.txnTitle} Successful!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            ZipBottomNav(
                currentTab = currentTab,
                onTabSelected = { tab -> viewModel.navigateTo(tab) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            // Voice sound alert banner
            VoiceSoundBanner(
                message = voiceAlertMessage,
                onDismiss = { viewModel.clearVoiceAlert() }
            )

            // Top Bar
            ZipTopBar(
                userAccount = userAccount,
                onMyQrClick = { viewModel.navigateTo("my_qr") },
                onKycClick = { viewModel.navigateTo("kyc") }
            )

            // Dynamic Screen Content
            Box(modifier = Modifier.weight(1f)) {
                when (currentTab) {
                    "home" -> HomeScreen(
                        userAccount = userAccount,
                        transactions = transactions,
                        rewards = rewards,
                        onNavigate = { tab -> viewModel.navigateTo(tab) },
                        onSelectTxn = { txn -> viewModel.selectTransaction(txn) },
                        onScratchReward = { reward -> viewModel.scratchRewardCard(reward) },
                        onUpdateDailyLimit = { limit -> viewModel.updateDailyLimit(limit) }
                    )

                    "scan" -> ScanPayScreen(
                        userAccount = userAccount,
                        scannedUpiId = scannedUpiId,
                        scannedName = scannedName,
                        paymentAmount = paymentAmount,
                        paymentCategory = paymentCategory,
                        paymentNote = paymentNote,
                        onDetailsChange = { upi, name, amt -> viewModel.setScannedDetails(upi, name, amt) },
                        onAmountChange = { amt -> viewModel.updatePaymentAmount(amt) },
                        onCategoryChange = { cat -> viewModel.updatePaymentCategory(cat) },
                        onNoteChange = { note -> viewModel.updatePaymentNote(note) },
                        onProceedToPay = { viewModel.openPinModal() }
                    )

                    "kyc" -> AadhaarKycScreen(
                        userAccount = userAccount,
                        kycStep = kycStep,
                        aadhaarInput = aadhaarInput,
                        otpInput = otpInput,
                        selectedBank = selectedBank,
                        newUpiPin = newUpiPin,
                        onKycStepChange = { step -> viewModel.setKycStep(step) },
                        onAadhaarChange = { num -> viewModel.updateAadhaarInput(num) },
                        onOtpChange = { otp -> viewModel.updateOtpInput(otp) },
                        onBankChange = { bank -> viewModel.updateSelectedBank(bank) },
                        onPinChange = { pin -> viewModel.updateNewUpiPin(pin) },
                        onCompleteKyc = { name, age, handle ->
                            viewModel.completeAadhaarKycAndLinkBank(name, age, handle)
                        }
                    )

                    "updates" -> UpdatesFeedScreen(
                        rewards = rewards,
                        onScratchReward = { reward -> viewModel.scratchRewardCard(reward) }
                    )

                    "history" -> HistoryScreen(
                        transactions = transactions,
                        onSelectTxn = { txn -> viewModel.selectTransaction(txn) }
                    )

                    "my_qr" -> MyQrScreen(
                        userAccount = userAccount
                    )

                    "request_allowance" -> RequestAllowanceScreen(
                        allowanceAmount = allowanceAmount,
                        allowanceNote = allowanceNote,
                        onAmountChange = { amt -> viewModel.updateAllowanceAmount(amt) },
                        onNoteChange = { note -> viewModel.updateAllowanceNote(note) },
                        onRequestSubmit = { viewModel.requestAllowanceFromParent() }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showPinModal) {
        val amtVal = paymentAmount.toDoubleOrNull() ?: 0.0
        PinModalDialog(
            recipientName = scannedName,
            amount = amtVal,
            enteredPin = enteredPin,
            onPinChange = { pin -> viewModel.updateEnteredPin(pin) },
            onDismiss = { viewModel.closePinModal() },
            onSubmit = { viewModel.submitPaymentWithPin() }
        )
    }

    if (selectedTxn != null) {
        DigitalReceiptDialog(
            transaction = selectedTxn!!,
            onDismiss = { viewModel.selectTransaction(null) }
        )
    }
}
