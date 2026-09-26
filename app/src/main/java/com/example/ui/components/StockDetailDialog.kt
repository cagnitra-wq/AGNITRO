package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockDetectionEntity
import com.example.data.network.MarketHoursUtil
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.GreenBadgeBg
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLightBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.PriceNegativeBorder
import com.example.ui.theme.PriceNegativeLightBg
import com.example.ui.theme.PriceNegativeRed
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailDialog(
    stock: StockDetectionEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isPositive = stock.percentChange >= 0
    val formattedFirstSeen = MarketHoursUtil.formatIstTime(stock.firstSeenTimestamp)
    val formattedLastUpdated = MarketHoursUtil.formatIstTime(stock.lastUpdatedTimestamp)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WhitePure,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhitePure)
                .padding(20.dp)
        ) {
            // Header: Symbol, Company & Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stock.symbol,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                    Text(
                        text = stock.companyName,
                        fontSize = 13.sp,
                        color = NavySecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_stock_detail")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Price Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceTinted)
                    .border(1.dp, NavyLightBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE MARKET PRICE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavySecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = String.format(Locale.ENGLISH, "₹%,.2f", stock.livePrice),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = NavyDeep
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isPositive) GreenBadgeBg else PriceNegativeLightBg)
                        .border(
                            1.dp,
                            if (isPositive) GreenBorder else PriceNegativeBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = String.format(
                            Locale.ENGLISH,
                            "%s%.2f%%",
                            if (isPositive) "+" else "",
                            stock.percentChange
                        ),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isPositive) GreenPrimary else PriceNegativeRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "FIRST APPEARED",
                    value = formattedFirstSeen,
                    subtext = "Initial scan trigger",
                    valueColor = GreenPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "LAST TICK TIME",
                    value = formattedLastUpdated,
                    subtext = "Recent price update",
                    valueColor = NavyDeep,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "DAY HIGH",
                    value = if (stock.dayHigh > 0) String.format(Locale.ENGLISH, "₹%,.2f", stock.dayHigh) else "-",
                    subtext = "Intraday peak",
                    valueColor = GreenAccent,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "DAY LOW",
                    value = if (stock.dayLow > 0) String.format(Locale.ENGLISH, "₹%,.2f", stock.dayLow) else "-",
                    subtext = "Intraday trough",
                    valueColor = PriceNegativeRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            MetricCard(
                title = "VOLUME TRADED",
                value = String.format(Locale.ENGLISH, "%,d shares", stock.volume),
                subtext = "Total cumulative session volume",
                valueColor = NavyDeep,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick External Chart Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val chartinkUrl = "https://chartink.com/stocks/${stock.symbol.lowercase()}.html"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(chartinkUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("open_chartink_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = WhitePure,
                        contentColor = NavyPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(NavyLightBorder)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Chartink",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }

                Button(
                    onClick = {
                        val tvUrl = "https://in.tradingview.com/chart/?symbol=NSE%3A${stock.symbol}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tvUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("open_tradingview_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor = WhitePure
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = WhitePure,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TradingView",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhitePure
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceTinted)
            .border(1.dp, NavyLightBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NavySecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = NavySecondary
            )
        }
    }
}
