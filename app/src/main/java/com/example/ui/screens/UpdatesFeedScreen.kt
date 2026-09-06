package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.RewardCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.HotPink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun UpdatesFeedScreen(
    rewards: List<RewardCard>,
    onScratchReward: (RewardCard) -> Unit
) {
    var selectedSkin by remember { mutableStateOf("Neon Cyber") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.NewReleases,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Naya Updates & Teen Perks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Cool new features, card skins & cashback rewards for under-18s",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Artwork Visual
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.img_teen_card),
                    contentDescription = "Teen Virtual Card",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "NEW: Customize Your Teen Virtual Card Skin 💳",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose a cool theme for your contactless UPI card. Selected: $selectedSkin",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Neon Cyber", "Electric Violet", "Gold Champ").forEach { skin ->
                            val isSelected = selectedSkin == skin
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                                    .clickable { selectedSkin = skin }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = skin,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rewards Section
        Text(
            text = "Scratch & Win Cashbacks",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        rewards.forEach { reward ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (!reward.isScratched) {
                            onScratchReward(reward)
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (reward.isScratched) SuccessGreen.copy(alpha = 0.5f) else GoldYellow
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (reward.isScratched) SuccessGreen.copy(alpha = 0.2f) else GoldYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = if (reward.isScratched) SuccessGreen else GoldYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reward.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = reward.description,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = if (reward.isScratched) "₹${reward.cashbackAmount.toInt()} Won" else "SCRATCH NOW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reward.isScratched) SuccessGreen else NeonLime
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Regular Updates & New Features Release Radar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NewReleases,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Release Radar & What's New",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElectricViolet)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "v2.4 LIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ReleaseItem(
            version = "v2.4 (Current)",
            badge = "ACTIVE",
            badgeColor = SuccessGreen,
            title = "Aadhaar e-KYC Minor Onboarding & Voice Soundbox",
            details = "Paperless instant verification for under-18s with parent consent and audio voice alerts on payment."
        )

        ReleaseItem(
            version = "v2.5 (Rolling out)",
            badge = "COMING THIS WEEK",
            badgeColor = NeonCyan,
            title = "Split Canteen Bills with School Buddies",
            details = "Scan 1 QR and automatically split samosa & cold drink costs with friends with 1-tap UPI requests."
        )

        ReleaseItem(
            version = "v2.6 (Next Drop)",
            badge = "IN DEVELOPMENT",
            badgeColor = GoldYellow,
            title = "Teen Savings Vault & Smart Goals",
            details = "Set goals for sneakers, games or books and lock small pocket change into a high-interest minor vault."
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Interactive Community Feature Voting
        Text(
            text = "Vote For Next Feature Drop 🗳️",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Tap to vote for what we should build next for Zip UPI",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        var votedFeatureId by remember { mutableStateOf<Int?>(null) }
        var voteCounts by remember {
            mutableStateOf(mapOf(1 to 482, 2 to 349, 3 to 291, 4 to 198))
        }

        val featurePoll = listOf(
            Triple(1, "🎮 PlayStore & Steam In-Game Topups (Under ₹100)", "No credit card needed"),
            Triple(2, "🍕 Automatic Canteen Bill Splitter QR", "Instant split among friends"),
            Triple(3, "⌚ NFC Smart Band / Ring Tap & Pay", "Pay without taking phone out"),
            Triple(4, "🎧 Regional Language Voice Alerts", "Hindi, Tamil, Marathi & Telugu")
        )

        featurePoll.forEach { (id, title, desc) ->
            val hasVoted = votedFeatureId == id
            val votes = voteCounts[id] ?: 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (votedFeatureId != id) {
                            votedFeatureId = id
                            voteCounts = voteCounts.toMutableMap().apply {
                                this[id] = (this[id] ?: 0) + 1
                            }
                        }
                    }
                    .testTag("vote_feature_$id"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (hasVoted) Color(0xFF192A3A) else DarkSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hasVoted) NeonCyan else DarkCardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = desc,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (hasVoted) NeonCyan else DarkSurfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (hasVoted) "Voted! ($votes)" else "Vote ($votes)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasVoted) Color.Black else NeonCyan
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Financial Literacy for Teens
        Text(
            text = "Teen Money Tips 💡",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        TipCard(
            title = "Never share your 4-Digit UPI PIN with anyone",
            description = "Your UPI PIN is like your house key. Only enter it in official payment screens."
        )

        TipCard(
            title = "Daily Limit Protects Your Wallet",
            description = "Parent spending caps ensure you never accidentally overspend at canteens or shops."
        )

        TipCard(
            title = "Aadhaar e-KYC Verification",
            description = "Aadhaar authentication ensures legal NPCI compliant minor UPI access for under-18s."
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReleaseItem(
    version: String,
    badge: String,
    badgeColor: Color,
    title: String,
    details: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = version,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = details,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TipCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = GoldYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
