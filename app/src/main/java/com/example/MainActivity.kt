package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StockDetectionEntity
import com.example.ui.ScanViewModel
import com.example.ui.components.AddScanBottomSheet
import com.example.ui.components.MarketStatusBar
import com.example.ui.components.ScanSelectorRow
import com.example.ui.components.ScansManagerScreen
import com.example.ui.components.StockDetailDialog
import com.example.ui.components.StockRowItem
import com.example.ui.components.StockTableHeader
import com.example.ui.theme.DividerLight
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.GreenBadgeBg
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyChipBg
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLightBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure

class MainActivity : ComponentActivity() {

    private val viewModel: ScanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ScanViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Live Feed, 1 = My 4 Scans

    val selectedScan by viewModel.selectedScan.collectAsStateWithLifecycle()
    val allScans by viewModel.allScans.collectAsStateWithLifecycle()
    val detectedStocks by viewModel.detectedStocks.collectAsStateWithLifecycle()
    val isPolling by viewModel.isPollingActive.collectAsStateWithLifecycle()
    val secondsRemaining by viewModel.secondsRemaining.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isSortAsc by viewModel.sortChronologicalAsc.collectAsStateWithLifecycle()
    val isSimMode by viewModel.isSimulationMode.collectAsStateWithLifecycle()
    val newlyDetected by viewModel.newlyDetectedSymbols.collectAsStateWithLifecycle()
    val statusMsg by viewModel.statusMessage.collectAsStateWithLifecycle()
    val marketStatus by viewModel.marketStatus.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showAddScanSheet by remember { mutableStateOf(false) }
    var selectedStockForDetail by remember { mutableStateOf<StockDetectionEntity?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(WhitePure),
        containerColor = WhitePure,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("app_header")
                    ) {
                        // Golden Bull Logo
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(WhitePure)
                                .border(1.5.dp, GreenBorder, CircleShape)
                                .padding(2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.golden_bull_logo_1790410162614),
                                contentDescription = "555 Golden Bull Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "555",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyDeep,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GreenBadgeBg)
                                        .border(1.dp, GreenBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "LIVE SCANNER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenPrimary
                                    )
                                }
                            }
                            Text(
                                text = "Chartink Chronological Stock Tracker",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = NavySecondary
                            )
                        }
                    }
                },
                actions = {
                    if (selectedTab == 0) {
                        // Search toggle
                        IconButton(
                            onClick = { showSearchBar = !showSearchBar },
                            modifier = Modifier.testTag("search_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search stocks",
                                tint = if (showSearchBar) GreenPrimary else NavyPrimary
                            )
                        }

                        // Sort order toggle
                        IconButton(
                            onClick = { viewModel.toggleSortOrder() },
                            modifier = Modifier.testTag("sort_order_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = if (isSortAsc) "Sort: Earliest First" else "Sort: Latest First",
                                tint = NavyPrimary
                            )
                        }

                        // Reset session detections
                        IconButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.testTag("reset_session_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear session detections",
                                tint = NavyPrimary
                            )
                        }
                    } else {
                        // Add New Scan
                        IconButton(
                            onClick = { showAddScanSheet = true },
                            modifier = Modifier.testTag("add_scan_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add custom scan",
                                tint = GreenPrimary
                            )
                        }
                    }

                    // Info / Help
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("info_help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About 555 Scanner",
                            tint = NavyPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhitePure,
                    titleContentColor = NavyDeep,
                    actionIconContentColor = NavyPrimary
                )
            )
        },
        bottomBar = {
            // Strictly White Background Navigation Bar (Navy Blue & Green text/icons - Zero Black)
            NavigationBar(
                containerColor = WhitePure,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(width = 1.dp, color = DividerLight)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.TableView,
                            contentDescription = "Live Feed",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Live Feed",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenPrimary,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = GreenBadgeBg,
                        unselectedIconColor = NavySecondary,
                        unselectedTextColor = NavySecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_live_feed")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "My 4 Scans",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "My 4 Scans",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenPrimary,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = GreenBadgeBg,
                        unselectedIconColor = NavySecondary,
                        unselectedTextColor = NavySecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_my_scans")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(WhitePure)
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                // TAB 0: LIVE FEED (3-COLUMN CHRONOLOGICAL TABLE)
                AnimatedVisibility(visible = showSearchBar) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhitePure)
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Filter by symbol or company...", color = NavySecondary, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_input_field"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = WhitePure,
                                unfocusedContainerColor = WhitePure,
                                focusedTextColor = NavyDeep,
                                unfocusedTextColor = NavyDeep,
                                focusedIndicatorColor = GreenPrimary,
                                unfocusedIndicatorColor = NavyLightBorder
                            )
                        )
                    }
                }

                // Market Status, Poller Countdown & Mode Switch
                MarketStatusBar(
                    marketStatus = marketStatus,
                    isPolling = isPolling,
                    secondsRemaining = secondsRemaining,
                    isRefreshing = isRefreshing,
                    isSimulationMode = isSimMode,
                    statusMessage = statusMsg,
                    onTogglePolling = { viewModel.togglePolling() },
                    onManualPoll = { viewModel.triggerManualPoll() },
                    onToggleSimulation = { viewModel.toggleSimulationMode() }
                )

                // Scans Selector Horizontal Chips (Scan 1, Scan 2, Scan 3, Scan 4)
                ScanSelectorRow(
                    scans = allScans,
                    selectedScan = selectedScan,
                    onSelectScan = { viewModel.selectScan(it) },
                    onAddNewScan = { showAddScanSheet = true }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 3-Column Stock Table Header
                StockTableHeader(
                    isSortAscending = isSortAsc,
                    totalCount = detectedStocks.size,
                    onToggleSort = { viewModel.toggleSortOrder() }
                )

                // Stock Table Content List
                if (detectedStocks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceTinted)
                                .border(1.dp, NavyLightBorder, RoundedCornerShape(16.dp))
                                .padding(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(WhitePure)
                                    .border(1.5.dp, GreenBorder, CircleShape)
                                    .padding(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.golden_bull_logo_1790410162614),
                                    contentDescription = "Golden Bull",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "No Stocks Detected Yet",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isSimMode) {
                                    "Simulation active. Tapping 'Poll Now' will trigger the next batch of stocks."
                                } else {
                                    "Waiting for stocks to meet your scan criteria during market hours. You can also switch to 'Simulation Mode' to test."
                                },
                                fontSize = 12.sp,
                                color = NavySecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.triggerManualPoll() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenPrimary,
                                        contentColor = WhitePure
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("empty_poll_now_button")
                                ) {
                                    Text("Poll Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                if (!isSimMode) {
                                    OutlinedButton(
                                        onClick = { viewModel.toggleSimulationMode() },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = NavyPrimary
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(NavyLightBorder)
                                        ),
                                        modifier = Modifier.testTag("empty_switch_sim_button")
                                    ) {
                                        Text("Try Simulation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("stock_table_list"),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(
                            items = detectedStocks,
                            key = { it.symbol }
                        ) { stock ->
                            val isNew = newlyDetected.contains(stock.symbol)
                            StockRowItem(
                                stock = stock,
                                isNewlyDetected = isNew,
                                onClick = { selectedStockForDetail = stock }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            } else {
                // TAB 1: MY 4 SCANS MANAGER SCREEN (Scan 1, Scan 2, Scan 3, Scan 4 ...)
                ScansManagerScreen(
                    scans = allScans,
                    activeScanId = selectedScan?.id,
                    onSelectAndOpenFeed = { scan ->
                        viewModel.selectScan(scan)
                        selectedTab = 0 // Switch to Live Feed
                    },
                    onSaveScan = { updated ->
                        viewModel.updateScan(updated)
                    },
                    onToggleScanActive = { scan, active ->
                        viewModel.toggleScanActive(scan, active)
                    },
                    onManualPoll = { scan ->
                        viewModel.triggerPollForScan(scan)
                    },
                    onAddNewScan = { showAddScanSheet = true }
                )
            }
        }
    }

    // Add Custom Scan Bottom Sheet
    if (showAddScanSheet) {
        AddScanBottomSheet(
            onDismiss = { showAddScanSheet = false },
            onAddScan = { name, urlOrClause, interval, desc, isSim ->
                viewModel.addScan(name, urlOrClause, interval, desc, isSim)
            }
        )
    }

    // Stock Detail Modal
    selectedStockForDetail?.let { stock ->
        StockDetailDialog(
            stock = stock,
            onDismiss = { selectedStockForDetail = null }
        )
    }

    // Reset Session Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = WhitePure,
            title = {
                Text(
                    text = "Clear Session Detections?",
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "This will reset all detected stocks and their first-appearance timestamps for the current scan session.",
                    color = NavySecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearSessionDetections()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor = WhitePure
                    )
                ) {
                    Text("Clear Detections", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text("Cancel", color = NavyPrimary)
                }
            }
        )
    }

    // App Information Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            containerColor = WhitePure,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WhitePure)
                            .border(1.dp, GreenBorder, CircleShape)
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.golden_bull_logo_1790410162614),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About 555 Scanner",
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "555 is a live trading screener that runs your 4 custom Chartink scans during market hours (09:15 - 15:30 IST) and presents stocks in chronological order by their initial trigger time.",
                        color = NavySecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• My 4 Scans Page: Configure Scan 1, Scan 2, Scan 3, and Scan 4 with direct Chartink URLs or query clauses.",
                        color = GreenPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Column 1 (Stock Name): Ticker symbol, company name, and pulsing 'NEW' indicator for newly surfaced stocks.",
                        color = NavyDeep,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Column 2 (Live Price): Real-time market price in ₹ with percentage daily change.",
                        color = NavyDeep,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Column 3 (First Seen): Exact immutable timestamp (hh:mm:ss a) when the stock first satisfied the scan conditions.",
                        color = GreenPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• Visual Alerts: New stocks trigger silent visual glow highlights without sound.",
                        color = NavySecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyPrimary,
                        contentColor = WhitePure
                    )
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
