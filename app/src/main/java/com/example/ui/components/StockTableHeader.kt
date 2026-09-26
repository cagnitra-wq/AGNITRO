package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DividerLight
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SurfaceTinted
import com.example.ui.theme.WhitePure

@Composable
fun StockTableHeader(
    isSortAscending: Boolean,
    totalCount: Int,
    onToggleSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceTinted)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Stock Name
            Text(
                text = "STOCK ($totalCount)",
                color = NavyDeep,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.weight(1.3f)
            )

            // Column 2: Live Price
            Text(
                text = "LIVE PRICE (₹)",
                color = NavyDeep,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1.0f)
            )

            // Column 3: First Appeared Time (with sorting toggle)
            Row(
                modifier = Modifier
                    .weight(1.2f)
                    .clickable { onToggleSort() }
                    .testTag("sort_time_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
            ) {
                Text(
                    text = "FIRST SEEN",
                    color = NavyDeep,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = if (isSortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = if (isSortAscending) "Earliest first" else "Latest first",
                    tint = NavySecondary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Bottom light divider
        Divider(
            color = DividerLight,
            thickness = 1.dp,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
