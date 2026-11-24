package com.bachatcom.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MySavingsScreen(
    totalSavings: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F7F3))
            .padding(20.dp)
    ) {

        Text(
            text = "💰 My Savings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "See how much money you have saved.",
            fontSize = 15.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Your Total Savings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "₹$totalSavings",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (totalSavings > 0) {
                    Text(
                        text = "🎉 Great! You are saving money.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )
                } else {
                    Text(
                        text = "No purchase savings recorded yet.",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "💡 How Savings Work",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your saving is recorded only when you confirm that you purchased a product through the selected store.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 21.sp
                )
            }
        }
    }
}