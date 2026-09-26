package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.network.MarketHoursUtil
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.GreenBadgeBg
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.NavyChipBg
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLightBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure

@Composable
fun MarketStatusBar(
    marketStatus: String,
    isPolling: Boolean,
    secondsRemaining: Int,
    isRefreshing: Boolean,
    isSimulationMode: Boolean,
    statusMessage: String,
    onTogglePolling: () -> Unit,
    onManualPoll: () -> Unit,
    onToggleSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMarketOpen = MarketHoursUtil.isMarketOpen()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(WhitePure)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Row: Market hours indicator & Live/Sim Mode switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Market status badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isMarketOpen) GreenBadgeBg else NavyChipBg)
                    .border(1.dp, if (isMarketOpen) GreenBorder else NavyLightBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isMarketOpen) GreenPrimary else NavySecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = marketStatus,
                    color = if (isMarketOpen) GreenPrimary else NavyPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mode toggle button (Live Chartink vs Simulation)
            OutlinedButton(
                onClick = onToggleSimulation,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSimulationMode) GreenBadgeBg else WhitePure,
                    contentColor = if (isSimulationMode) GreenPrimary else NavyPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isSimulationMode) GreenBorder else NavyLightBorder
                    )
                ),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("mode_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Sensors,
                    contentDescription = "Mode switch",
                    tint = if (isSimulationMode) GreenPrimary else NavyPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSimulationMode) "SIMULATION MODE" else "LIVE NETWORK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSimulationMode) GreenPrimary else NavyPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Polling info & Quick Actions Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceTinted)
                .border(1.dp, NavyLightBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Polling timer",
                    tint = NavySecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                if (isPolling) {
                    Text(
                        text = "Next poll: ${secondsRemaining}s",
                        color = NavyDeep,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Polling Paused",
                        color = NavySecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pause / Play toggle
                IconButton(
                    onClick = onTogglePolling,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("toggle_polling_button")
                ) {
                    Icon(
                        imageVector = if (isPolling) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPolling) "Pause polling" else "Resume polling",
                        tint = if (isPolling) NavyPrimary else GreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Manual Scan Now button
                IconButton(
                    onClick = onManualPoll,
                    enabled = !isRefreshing,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("scan_now_button")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan now",
                            tint = GreenAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Subtext / status announcement
        AnimatedVisibility(visible = statusMessage.isNotBlank()) {
            Text(
                text = statusMessage,
                color = GreenPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
