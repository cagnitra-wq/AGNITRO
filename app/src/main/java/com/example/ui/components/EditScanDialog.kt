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
import androidx.compose.material.icons.filled.Link
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
import com.example.data.model.ScanEntity
import com.example.ui.theme.DividerLight
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.GreenBadgeBg
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLightBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScanDialog(
    scan: ScanEntity,
    slotTitle: String,
    onDismiss: () -> Unit,
    onSaveScan: (updatedScan: ScanEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var scanName by remember { mutableStateOf(scan.name) }
    var scanUrlOrClause by remember { mutableStateOf(scan.urlOrClause) }
    var selectedInterval by remember { mutableStateOf(scan.intervalSeconds) }
    var description by remember { mutableStateOf(scan.description) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenBadgeBg)
                            .border(1.dp, GreenBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = slotTitle.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Connect Custom Scan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_edit_scan_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Paste your custom Chartink screener URL or raw scan clause below.",
                fontSize = 12.sp,
                color = NavySecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Scan Name
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
                placeholder = { Text("e.g. Scan 1: Breakout 15m", color = NavySecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_scan_name_input"),
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

            // Chartink URL or Query Clause Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CHARTINK URL OR SCAN CLAUSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                    letterSpacing = 0.5.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "URL or Query",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = scanUrlOrClause,
                onValueChange = { scanUrlOrClause = it },
                placeholder = {
                    Text(
                        "Paste Chartink URL (e.g. https://chartink.com/screener/...) or ( {cash} ( ... ) )",
                        color = NavySecondary,
                        fontSize = 12.sp
                    )
                },
                minLines = 4,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_scan_url_input"),
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

            // Save Button
            Button(
                onClick = {
                    if (scanName.isNotBlank() && scanUrlOrClause.isNotBlank()) {
                        onSaveScan(
                            scan.copy(
                                name = scanName.trim(),
                                urlOrClause = scanUrlOrClause.trim().replace("http://", "https://"),
                                intervalSeconds = selectedInterval,
                                description = description.ifBlank { "Custom Chartink scan" }
                            )
                        )
                        onDismiss()
                    }
                },
                enabled = scanName.isNotBlank() && scanUrlOrClause.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_edited_scan_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = WhitePure,
                    disabledContainerColor = DividerLight,
                    disabledContentColor = NavySecondary
                )
            ) {
                Text(
                    text = "Save & Connect Scan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
