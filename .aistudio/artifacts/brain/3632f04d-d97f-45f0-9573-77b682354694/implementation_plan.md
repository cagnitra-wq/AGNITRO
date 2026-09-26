# Chartink Live Scanner & Chronological Stock Tracker

A dedicated live trading companion for NSE/BSE intraday and swing traders that executes custom Chartink scans during market hours, identifies stocks the moment they trigger scan conditions, and displays them chronologically by their first appearance time with live prices and subtle visual alerts.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> **Strict Visual Styling Mandate**:
> - **Background**: 100% pure white (`#FFFFFF`). Absolutely no black or dark mode colors anywhere in the app under any circumstances.
> - **Typography & Text Colors**: Exclusively rendered in **Navy Blue** (primary text, symbols, headers, e.g., `#0A2540`, `#1E3A8A`) and **Vibrant Emerald/Forest Green** (prices, positive changes, timestamps, badges, e.g., `#0D7A42`, `#059669`, `#16A34A`).
> - **Surfaces & Cards**: Pure white containers with crisp subtle navy/teal hairline dividers (`#E2E8F0`, `#E0E7FF`), soft green highlights for newly detected stocks, and clean shadows.

- **Confirmed: Custom Scan Integration**: The app accepts direct Chartink screener URLs (e.g., `chartink.com/screener/bullish-breakout`) or raw Chartink scan query clauses, handling cookies, CSRF tokens, and live payload execution.
- **Confirmed: Silent Visual Alerts**: Newly detected stocks trigger smooth visual highlights (soft mint green pulse, subtle green badge tag, and row highlight) without audio chimes.
- **Confirmed: Chronological 3-Column Display**: Stocks are pinned with their immutable first-seen time and arranged chronologically:
  1. **Stock**: Symbol name and company name in crisp Navy Blue.
  2. **Live Price**: Current trading price in Green / Navy Blue with percentage gain.
  3. **First Appeared**: The exact first-detected timestamp in Navy Blue / Green.

---

### 1. Overview & Core Concept

- **What It Does**: Traders running breakout or momentum scans during active market hours need to identify *which* stock triggered first versus later entrants. This app continually polls custom Chartink scans, timestamps the exact moment a stock first meets the criteria, and pins that timestamp forever.
- **The Core View**: A clean, distraction-free 3-column chronological trading feed:
  1. **Stock / Symbol**: Stock ticker (e.g., `TATAMOTORS`, `RELIANCE`) with company name and percentage change badge in Navy Blue & Green.
  2. **Live Price**: Real-time current traded price in ₹ (INR), rendered cleanly in vibrant Green.
  3. **First Appeared**: The immutable chronological timestamp (e.g., `09:18:24 AM`) representing the first moment the stock satisfied your scanner.
- **Target Audience**: Intraday momentum traders, breakout scalpers, and technical analysts tracking Indian equities on NSE/BSE.

---

### 2. User Experience & Visual Design

#### Visual Identity & Strict Color Palette
- **Primary Background**: Pure White (`#FFFFFF`).
- **Surface & Cards**: Pure White (`#FFFFFF`) with subtle border accents (`#E2E8F0` / `#DCFCE7`).
- **Text Palette (No Black)**:
  - **Deep Navy Blue (`#0A2540`, `#1E3A8A`)**: App title, stock symbols, table headers, company names, secondary details.
  - **Vibrant Green (`#0D7A42`, `#059669`, `#16A34A`)**: Live stock prices, percentage gains, "NEW" tags, live status indicator, active icons.
  - **Soft Mint / Light Green (`#F0FDF4`, `#DCFCE7`)**: Highlight tint for newly surfaced rows and active badges.
- **Typography**: Clean, readable sans-serif typography with tabular monospaced numbers ensuring price digits and timestamps align in columns without jitter during live updates.
- **Visual Alert System**:
  - Newly surfaced stocks highlight with a gentle mint green background wash (`#DCFCE7`) and animated `"NEW"` badge for 10 seconds before settling into the clean white list.

#### Key User Flows
1. **Live Scan Feed (Home)**:
   - Top status bar on white background: Market Status indicator (`LIVE 09:15-15:30 IST` in Green or `OFF-MARKET` in Navy Blue), active interval timer countdown (e.g., `Next scan in 12s`), and manual "Scan Now" button.
   - Scan selector chip row: Quick-switch between custom Chartink scans with Navy Blue & Green pills.
   - 3-Column Header: `STOCK`, `PRICE (₹)`, and `FIRST SEEN` in crisp bold Navy Blue.
   - Chronological stock list: Ordered by time of first trigger (earliest first or latest first toggle).
   - Tap a stock to view quick details: Day High/Low, volume, and 1-tap link to Chartink chart.
2. **Scan Manager Modal**:
   - Add new custom Chartink scans by pasting a URL (e.g. `https://chartink.com/screener/15-min-breakout`) or custom scan clause.
   - Configure polling frequency (10s, 15s, 30s, 60s).
   - Pre-loaded with popular intraday momentum templates.
3. **Session Reset / Simulation Mode**:
   - Clear session detections or toggle between live market polling and off-market simulation mode for after-hours testing.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Strict White Background & Zero Black Enforcement**
  - *Chosen Approach*: `Theme.kt` and all Compose components explicitly use pure white `#FFFFFF` for `background`, `surface`, `surfaceVariant`, `containerColor`, and dialogs. All typography colors are strictly mapped to shades of Navy Blue (`#0A2540`, `#1E3A8A`) and Green (`#0D7A42`, `#059669`). Black (`#000000`) is completely banned from the entire codebase.
  - *Why*: Direct compliance with the user's explicit visual requirement for a pure white, high-contrast Navy & Green financial layout.
- **Decision 2: Direct Chartink Web Session & CSRF Architecture with Resilient Fallback**
  - *Chosen Approach*: OkHttp client with cookie jar and CSRF meta-tag extraction that posts to `chartink.com/screener/process`. Built-in parser handles Chartink's JSON output directly. When markets are closed or network fails, an off-market simulator mode provides realistic NSE stock updates.
  - *Why*: Enables seamless live scanning directly from custom Chartink queries.
- **Decision 3: Immutable First-Seen Timestamps via Room Persistence**
  - *Chosen Approach*: When a scan returns stocks, the Room database verifies if the stock was already recorded for today's session. If already recorded, only the live price is updated; the `firstSeenTimestamp` is strictly immutable.
  - *Why*: Ensures the 3-column chronological feed strictly preserves the exact time each stock first triggered the scan.

---

### 4. Technical Architecture & Data Strategy

```
┌──────────────────────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI (White + Navy + Green)              │
│  ┌──────────────────────┬───────────────────────┬─────────────────────┐  │
│  │  TopBar & Status     │   3-Column Feed View  │   Add Scan Modal    │  │
│  │  (White bg, Navy/    │  (Stock | Price |     │ (White bg, Navy/    │  │
│  │   Green indicators)  │   First Seen Time)    │  Green inputs)      │  │
│  └──────────────────────┴───────────────────────┴─────────────────────┘  │
└────────────────────────────────────▲─────────────────────────────────────┘
                                     │ StateFlow / Events
┌────────────────────────────────────┴─────────────────────────────────────┐
│                          ScanViewModel (MVVM)                            │
│  - Polling Engine (Coroutines & Timer Flow)                              │
│  - Deduplication & Chronological Sorter                                  │
│  - Visual Highlight Manager (soft green fade for new stocks)             │
└───────────────────▲──────────────────────────────────▲───────────────────┘
                    │                                  │
┌───────────────────┴───────────────┐  ┌───────────────┴───────────────────┐
│     ChartinkRepository            │  │        AppDatabase (Room)         │
│  - OkHttp Session + CookieJar     │  │  - ScanEntity (User Scans)        │
│  - CSRF Token Extractor           │  │  - StockDetectionEntity           │
│  - Process Endpoint POST          │  │    (symbol, price, firstSeen,     │
│  - Market Hours & Simulator Engine│  │     scanId, sessionDate)          │
└───────────────────────────────────┘  └───────────────────────────────────┘
```

#### Data Entities

```kotlin
// Scan Entity
@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val urlOrClause: String,
    val intervalSeconds: Int = 15,
    val isSimulation: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

// Stock Detection Entity (First Seen is strictly preserved)
@Entity(tableName = "stock_detections", indices = [Index(value = ["scanId", "symbol", "sessionDate"], unique = true)])
data class StockDetectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scanId: Long,
    val symbol: String,
    val companyName: String,
    val livePrice: Double,
    val percentChange: Double,
    val volume: Long,
    val firstSeenTimestamp: Long, // Immutable time when first discovered
    val lastUpdatedTimestamp: Long,
    val sessionDate: String // YYYY-MM-DD
)
```

---

### Step-by-Step Implementation Sequence

1. **Theme & Palette Setup**: Configure `Color.kt` and `Theme.kt` with pure white (`#FFFFFF`) backgrounds, and strictly Navy Blue (`#0A2540`, `#1E3A8A`) and Green (`#0D7A42`, `#059669`) text and accents—zero black colors.
2. **Data & Storage Layer**: Create `ScanEntity`, `StockDetectionEntity`, `ScanDao`, and `AppDatabase` with Room integration.
3. **Chartink Networking & Polling Engine**: Implement `ChartinkService` with OkHttp cookie management, CSRF extraction, scan execution, and market simulation fallback.
4. **ViewModel & State Layer**: Build `ScanViewModel` to manage periodic polling, immutable first-seen timestamp tracking, soft green visual highlight state, and scan switching.
5. **Terminal UI & 3-Column Table**: Build Compose UI:
   - Status header with Market Hours indicator & poll countdown on crisp white.
   - Scan selector chip row with Navy & Green styling.
   - Fixed 3-column header: `STOCK`, `PRICE (₹)`, and `FIRST SEEN`.
   - Chronological stock rows with soft green glow/badge for newly detected stocks.
   - Add/Edit scan bottom sheet modal with pre-configured templates.
6. **Custom Launcher Icon**: Create custom financial radar/trading launcher icon.
7. **Verification**: Run `compile_applet` to confirm clean build.
