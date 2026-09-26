package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DividerLight
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.GreenBadgeBg
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.NavyChipBg
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLightBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure

data class ScanPreset(
    val name: String,
    val clause: String,
    val desc: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScanBottomSheet(
    onDismiss: () -> Unit,
    onAddScan: (name: String, urlOrClause: String, interval: Int, desc: String, isSim: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var scanName by remember { mutableStateOf("") }
    var scanUrlOrClause by remember { mutableStateOf("") }
    var selectedInterval by remember { mutableStateOf(15) }
    var description by remember { mutableStateOf("") }
    var isSimulated by remember { mutableStateOf(false) }

    val presets = listOf(
        ScanPreset(
            name = "15-Min ORB Breakout",
            clause = "( {cash} ( [0] 15 minute close > [0] 15 minute max( 20 , [0] 15 minute high ) and [0] 15 minute volume > 50000 ) )",
            desc = "Opening range breakout with volume confirmation"
        ),
        ScanPreset(
            name = "RSI 60+ SuperTrend",
            clause = "( {cash} ( [0] 5 minute rsi( 14 ) > 60 and [0] 5 minute close > [0] 5 minute supertrend( 7 , 3 ) ) )",
            desc = "Intraday momentum crossover scanner"
        ),
        ScanPreset(
            name = "Volume Shockers (>3x)",
            clause = "( {cash} ( [0] 15 minute volume > [0] 15 minute sma( volume, 20 ) * 3 and [0] 15 minute close > [0] 15 minute open ) )",
            desc = "Stocks trading at 3x average volume with green candle"
        ),
        ScanPreset(
            name = "20 EMA Crossover",
            clause = "( {cash} ( [0] 5 minute close > [0] 5 minute ema( close, 20 ) and [-1] 5 minute close <= [-1] 5 minute ema( close, 20 ) ) )",
            desc = "Fresh breakout above 20 exponential moving average"
        )
    )

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add Custom Chartink Scan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                    Text(
                        text = "Paste a screener URL or enter a query clause",
                        fontSize = 12.sp,
                        color = NavySecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_add_scan_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets row
            Text(
                text = "POPULAR PRESETS (TAP TO LOAD)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                presets.forEach { preset ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceTinted)
                            .border(1.dp, NavyLightBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                scanName = preset.name
                                scanUrlOrClause = preset.clause
                                description = preset.desc
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = preset.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDeep
                                )
                                Text(
                                    text = preset.desc,
                                    fontSize = 10.sp,
                                    color = NavySecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Name Input
            Text(
                text = "SCAN NAME",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = scanName,
                onValueChange = { scanName = it },
                placeholder = { Text("e.g. My Breakout Scan", color = NavySecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scan_name_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = WhitePure,
                    unfocusedContainerColor = WhitePure,
                    focusedTextColor = NavyDeep,
                    unfocusedTextColor = NavyDeep,
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = NavyLightBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // URL or Clause Input
            Text(
                text = "CHARTINK URL OR SCAN CLAUSE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = scanUrlOrClause,
                onValueChange = { scanUrlOrClause = it },
                placeholder = {
                    Text(
                        "https://chartink.com/screener/... or ( {cash} ( ... ) )",
                        color = NavySecondary,
                        fontSize = 12.sp
                    )
                },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scan_url_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = WhitePure,
                    unfocusedContainerColor = WhitePure,
                    focusedTextColor = NavyDeep,
                    unfocusedTextColor = NavyDeep,
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = NavyLightBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Polling interval selector
            Text(
                text = "POLLING INTERVAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDeep,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10, 15, 30, 60).forEach { interval ->
                    val isSelected = selectedInterval == interval
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NavyPrimary else WhitePure)
                            .border(
                                1.dp,
                                if (isSelected) NavyDeep else NavyLightBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedInterval = interval },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${interval}s",
                            color = if (isSelected) WhitePure else NavyPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (scanName.isNotBlank() && scanUrlOrClause.isNotBlank()) {
                        onAddScan(
                            scanName.trim(),
                            scanUrlOrClause.trim(),
                            selectedInterval,
                            description.ifBlank { "Custom Chartink scan" },
                            isSimulated
                        )
                        onDismiss()
                    }
                },
                enabled = scanName.isNotBlank() && scanUrlOrClause.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_scan_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = WhitePure,
                    disabledContainerColor = DividerLight,
                    disabledContentColor = NavySecondary
                )
            ) {
                Text(
                    text = "Save & Start Live Tracking",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
