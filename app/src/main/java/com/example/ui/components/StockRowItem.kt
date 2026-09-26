package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockDetectionEntity
import com.example.data.network.MarketHoursUtil
import com.example.ui.theme.DividerLight
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.GreenBadgeBg
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.HighlightPulse
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLightBorder
import com.example.ui.theme.NavyMuted
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.PriceNegativeBorder
import com.example.ui.theme.PriceNegativeLightBg
import com.example.ui.theme.PriceNegativeRed
import com.example.ui.theme.SurfaceGreenTint
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure
import java.util.Locale

@Composable
fun StockRowItem(
    stock: StockDetectionEntity,
    isNewlyDetected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for newly detected stocks (Visual highlight without sounds)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val rowBgColor by animateColorAsState(
        targetValue = if (isNewlyDetected) SurfaceGreenTint else WhitePure,
        label = "bg_color"
    )

    val isPositive = stock.percentChange >= 0
    val formattedTime = MarketHoursUtil.formatIstTime(stock.firstSeenTimestamp)
    val formattedPrice = String.format(Locale.ENGLISH, "₹%,.2f", stock.livePrice)
    val formattedChange = String.format(
        Locale.ENGLISH,
        "%s%.2f%%",
        if (isPositive) "+" else "",
        stock.percentChange
    )

    // Real-time price tick flash (green for uptick, red for downtick)
    var previousPrice by remember { mutableDoubleStateOf(stock.livePrice) }
    var tickDirection by remember { mutableStateOf(0) } // 1: up, -1: down, 0: neutral

    LaunchedEffect(stock.livePrice) {
        if (previousPrice > 0.0 && stock.livePrice != previousPrice) {
            tickDirection = if (stock.livePrice > previousPrice) 1 else -1
            delay(1200)
            tickDirection = 0
        }
        previousPrice = stock.livePrice
    }

    val priceBgColor by animateColorAsState(
        targetValue = when (tickDirection) {
            1 -> GreenBadgeBg
            -1 -> PriceNegativeLightBg
            else -> Color.Transparent
        },
        label = "price_tick_bg"
    )

    val priceTextColor by animateColorAsState(
        targetValue = when (tickDirection) {
            1 -> GreenPrimary
            -1 -> PriceNegativeRed
            else -> NavyDeep
        },
        label = "price_tick_text"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(rowBgColor)
            .border(
                width = if (isNewlyDetected) 2.dp else 1.dp,
                color = if (isNewlyDetected) HighlightPulse.copy(alpha = pulseAlpha) else NavyLightBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("stock_row_${stock.symbol}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Stock Name & Symbol (with visual "NEW" highlight badge)
            Column(
                modifier = Modifier.weight(1.3f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.symbol,
                        color = NavyDeep,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )

                    if (isNewlyDetected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GreenBadgeBg)
                                .border(1.dp, GreenBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "NEW",
                                color = GreenPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = stock.companyName,
                    color = NavySecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Column 2: Live Price & % Change Pill (with real-time tick animation)
            Column(
                modifier = Modifier.weight(1.0f),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(priceBgColor)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = formattedPrice,
                        color = priceTextColor,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isPositive) GreenBadgeBg else PriceNegativeLightBg)
                        .border(
                            1.dp,
                            if (isPositive) GreenBorder else PriceNegativeBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = formattedChange,
                        color = if (isPositive) GreenPrimary else PriceNegativeRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Column 3: First Appeared Time
            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GreenBadgeBg)
                        .border(1.dp, GreenBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = formattedTime,
                        color = GreenPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "First trigger",
                    color = NavyMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
