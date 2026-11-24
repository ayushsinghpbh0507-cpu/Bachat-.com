package com.bachatcom.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NavyBlue = Color(0xFF14243A)
private val MintGreen = Color(0xFF75E0B2)
private val DarkGreen = Color(0xFF087F5B)
private val LightMint = Color(0xFFE8FAF2)
private val ScreenBackground = Color(0xFFF4F7FA)
private val TextDark = Color(0xFF203044)
private val TextGray = Color(0xFF68788A)

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onImageClick: () -> Unit,
    onBudgetShoppingClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        // App heading
        Text(
            text = "🛍️ BachatCom",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Compare • Save • Shop Smart",
            fontSize = 14.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Main comparison banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(
                containerColor = NavyBlue
            ),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "Better Prices,\nBigger Savings ✨",
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Compare products across stores before you buy.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFFE1EAF2)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MintGreen,
                        contentColor = NavyBlue
                    )
                ) {
                    Text(
                        text = "Compare Products  →",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Budget shopping
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBudgetShoppingClick() },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightMint
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "🎯 Budget Shopping",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Find products that fit your budget.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onBudgetShoppingClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen
                    )
                ) {
                    Text(
                        text = "Find Products in My Budget  →",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Categories
        Text(
            text = "Shop by Category",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryCard(
                emoji = "📱",
                title = "Mobiles",
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("Mobiles") }
            )

            CategoryCard(
                emoji = "💻",
                title = "Laptops",
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("Laptops") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryCard(
                emoji = "🎧",
                title = "Headphones",
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("Headphones") }
            )

            CategoryCard(
                emoji = "👟",
                title = "Fashion",
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick("Fashion") }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Deals section
        Text(
            text = "🔥 Today's Deals",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyBlue
        )

        Spacer(modifier = Modifier.height(14.dp))

        DealCard(
            productName = "Smartphone",
            price = "₹24,999",
            oldPrice = "₹29,999"
        )

        Spacer(modifier = Modifier.height(12.dp))

        DealCard(
            productName = "Wireless Headphones",
            price = "₹1,999",
            oldPrice = "₹2,999"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Smart savings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "💰 Smart Savings",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Compare available prices, ratings and product details to make an informed choice.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen
                    )
                ) {
                    Text(
                        text = "Start Comparing  →",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CategoryCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 31.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue
            )
        }
    }
}
