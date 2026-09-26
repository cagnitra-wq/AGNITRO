package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScanEntity
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

@Composable
fun ScansManagerScreen(
    scans: List<ScanEntity>,
    activeScanId: Long?,
    onSelectAndOpenFeed: (ScanEntity) -> Unit,
    onSaveScan: (ScanEntity) -> Unit,
    onToggleScanActive: (ScanEntity, Boolean) -> Unit,
    onManualPoll: (ScanEntity) -> Unit,
    onAddNewScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scanToEdit by remember { mutableStateOf<Pair<ScanEntity, String>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WhitePure)
    ) {
        // Page Title & Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My 4 Scans",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDeep
                )
                Text(
                    text = "Connect custom Chartink screener URLs to Scan 1, 2, 3, and 4",
                    fontSize = 12.sp,
                    color = NavySecondary
                )
            }

            // Quick Add button
            Button(
                onClick = onAddNewScan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = WhitePure
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("add_scan_header_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = WhitePure,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Scan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePure
                )
            }
        }

        // List of 4 Scans (Scan 1, Scan 2, Scan 3, Scan 4 ...)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            itemsIndexed(
                items = scans,
                key = { _, item -> item.id }
            ) { index, scan ->
                val slotTitle = "Scan ${index + 1}"
                val isCurrentlyActive = scan.id == activeScanId

                ScanSlotCard(
                    scan = scan,
                    slotTitle = slotTitle,
                    isCurrentInFeed = isCurrentlyActive,
                    onOpenFeed = { onSelectAndOpenFeed(scan) },
                    onEdit = { scanToEdit = Pair(scan, slotTitle) },
                    onToggleActive = { active -> onToggleScanActive(scan, active) },
                    onPoll = { onManualPoll(scan) }
                )
            }
        }
    }

    // Edit modal
    scanToEdit?.let { (scan, slotTitle) ->
        EditScanDialog(
            scan = scan,
            slotTitle = slotTitle,
            onDismiss = { scanToEdit = null },
            onSaveScan = { updated ->
                onSaveScan(updated)
                scanToEdit = null
            }
        )
    }
}

@Composable
fun ScanSlotCard(
    scan: ScanEntity,
    slotTitle: String,
    isCurrentInFeed: Boolean,
    onOpenFeed: () -> Unit,
    onEdit: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onPoll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WhitePure)
            .border(
                width = if (isCurrentInFeed) 2.dp else 1.dp,
                color = if (isCurrentInFeed) GreenPrimary else NavyLightBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
            .testTag("scan_card_${scan.id}")
    ) {
        Column {
            // Header Row: Slot Badge (SCAN 1, 2, 3, 4), Name & Active Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenBadgeBg)
                            .border(1.dp, GreenBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = slotTitle.uppercase(),
                            color = GreenPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = scan.name,
                        color = NavyDeep,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Switch(
                    checked = scan.isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = WhitePure,
                        checkedTrackColor = GreenPrimary,
                        uncheckedThumbColor = NavySecondary,
                        uncheckedTrackColor = SurfaceTinted
                    ),
                    modifier = Modifier.testTag("toggle_scan_active_${scan.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connected Chartink URL / Clause Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceTinted)
                    .border(1.dp, NavyLightBorder, RoundedCornerShape(8.dp))
                    .clickable { onEdit() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = if (scan.urlOrClause.startsWith("http")) GreenPrimary else NavySecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (scan.urlOrClause.startsWith("http")) "CHARTINK URL" else "SCAN QUERY CLAUSE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavySecondary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = scan.urlOrClause,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = NavyDeep,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit scan source",
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata info & action bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NavyChipBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Poll: ${scan.intervalSeconds}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NavyDeep
                        )
                    }

                    if (isCurrentInFeed) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GreenBadgeBg)
                                .border(1.dp, GreenBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE IN FEED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenPrimary
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Manual Poll Icon
                    IconButton(
                        onClick = onPoll,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceTinted)
                            .border(1.dp, NavyLightBorder, CircleShape)
                            .testTag("poll_scan_slot_${scan.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Poll now",
                            tint = GreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Open 3-Column Feed Button
                    Button(
                        onClick = onOpenFeed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrentInFeed) GreenPrimary else NavyPrimary,
                            contentColor = WhitePure
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("view_feed_button_${scan.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableView,
                            contentDescription = null,
                            tint = WhitePure,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Live Feed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhitePure
                        )
                    }
                }
            }
        }
    }
}
