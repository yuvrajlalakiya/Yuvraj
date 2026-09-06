package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccount
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HotPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

data class MerchantQrSample(
    val name: String,
    val upiId: String,
    val defaultAmount: String,
    val category: String,
    val iconEmoji: String
)

@Composable
fun ScanPayScreen(
    userAccount: UserAccount?,
    scannedUpiId: String,
    scannedName: String,
    paymentAmount: String,
    paymentCategory: String,
    paymentNote: String,
    onDetailsChange: (upiId: String, name: String, amount: String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onProceedToPay: () -> Unit
) {
    var isFlashOn by remember { mutableStateOf(false) }
    var manualMode by remember { mutableStateOf(false) }

    val merchantSamples = listOf(
        MerchantQrSample("Sharma Kirana Store", "sharmakirana@okicici", "45", "Food & Snacks", "🛒"),
        MerchantQrSample("School Canteen", "schoolcanteen@sbi", "30", "Food & Snacks", "🍔"),
        MerchantQrSample("Chai & Bakery", "chaibakery@paytm", "25", "Food & Snacks", "☕"),
        MerchantQrSample("Friend Rohan", "rohan@zipupi", "100", "Transfer", "👦")
    )

    val quickAmounts = listOf("20", "50", "100", "200", "500")
    val categories = listOf("Food & Snacks", "Shopping", "Stationery", "Gaming", "Transfer")

    val spentToday = userAccount?.spentToday ?: 0.0
    val dailyLimit = userAccount?.dailyLimit ?: 500.0
    val remainingLimit = (dailyLimit - spentToday).coerceAtLeast(0.0)
    val parsedAmt = paymentAmount.toDoubleOrNull() ?: 0.0
    val isExceedingLimit = parsedAmt > remainingLimit

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Scan Any UPI QR Code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Works with BharatPe, Paytm, PhonePe, GPay & Store QRs",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Spending Limit Status Chip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isExceedingLimit) Color(0xFF331114) else DarkSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isExceedingLimit) HotPink else DarkCardBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isExceedingLimit) Icons.Default.Warning else Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (isExceedingLimit) HotPink else NeonLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExceedingLimit) "Amount exceeds daily limit!" else "Daily Limit Protected",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isExceedingLimit) HotPink else Color.White
                    )
                }

                Text(
                    text = "₹${remainingLimit.toInt()} left today",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExceedingLimit) HotPink else NeonLime
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scanner Camera Viewfinder Simulation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF070913))
                .border(2.dp, NeonCyan, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CenterFocusWeak,
                    contentDescription = "Scanner",
                    tint = NeonCyan,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Point Camera at Merchant QR Code",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Auto-scans instant UPI handle",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            // Flash & Gallery overlay buttons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isFlashOn) NeonCyan else DarkSurfaceVariant)
                        .clickable { isFlashOn = !isFlashOn },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .clickable {
                            // Pick sample
                            val sample = merchantSamples.random()
                            onDetailsChange(sample.upiId, sample.name, sample.defaultAmount)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Upload QR from Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select Merchant QR Samples
        Text(
            text = "Tap to simulate scanning a real store QR:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(merchantSamples) { sample ->
                val isSelected = scannedUpiId == sample.upiId
                Card(
                    modifier = Modifier
                        .clickable {
                            onDetailsChange(sample.upiId, sample.name, sample.defaultAmount)
                            onCategoryChange(sample.category)
                        }
                        .testTag("sample_qr_${sample.name.lowercase().replace(" ", "_")}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF0F3B36) else DarkSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonCyan else DarkCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = sample.iconEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = sample.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = sample.upiId,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Details Input Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Payment Recipient",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = scannedName,
                    onValueChange = { onDetailsChange(scannedUpiId, it, paymentAmount) },
                    label = { Text("Merchant / Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_merchant_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = scannedUpiId,
                    onValueChange = { onDetailsChange(it, scannedName, paymentAmount) },
                    label = { Text("UPI ID (e.g. store@okicici)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_upi_id"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Amount (₹)",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                OutlinedTextField(
                    value = paymentAmount,
                    onValueChange = { onAmountChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_payment_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Amount Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickAmounts.forEach { chipAmt ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                                .clickable { onAmountChange(chipAmt) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₹$chipAmt",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = paymentCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ElectricViolet else DarkSurfaceVariant)
                                .clickable { onCategoryChange(cat) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onProceedToPay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_pay_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "PAY ₹${paymentAmount.ifBlank { "0" }} WITH UPI PIN",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
