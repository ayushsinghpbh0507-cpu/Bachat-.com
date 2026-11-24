package com.bachatcom.app

import android.os.Bundle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.Image
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BachatComApp()
        }
    }
}
@Composable
fun CategoryButton(name: String) {
    OutlinedButton(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF2E7D32)
        )
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun PriceCard(
    shopName: String,
    imageUrl: String?,
    price: Int,
    rating: String,
    isLowest: Boolean,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = if (isLowest) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                androidx.compose.ui.graphics.Color(0xFF2E7D32)
            )
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            if (!imageUrl.isNullOrBlank()) {
                coil.compose.AsyncImage(
                    model = imageUrl,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = shopName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text("💰 ₹$price")

            if (isLowest) {
                Text(
                    text = "🏆 Lowest Price",
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color(0xFF2E7D32)
                )
            }

            Text("⭐ $rating Rating")
            Text("🚚 Free Delivery")

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = onBuyClick) {
                Text("Buy Now")
            }
        }
    }
}

@Composable
fun androidx.compose.foundation.layout.RowScope.CategoryButton(name: String) {

    OutlinedButton(
        onClick = {},
        modifier = Modifier.weight(1f)
    ) {
        Text(name)
    }
}


@Composable
fun DealCard(
    productName: String,
    price: String,
    oldPrice: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFE8F5E9),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🛍️", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = productName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = price,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Text(
                    text = "MRP: $oldPrice",
                    color = Color.Gray
                )

                Text(
                    text = "⭐ 4.4 Rating",
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "💚 Great Deal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
data class StoreProduct(
    val name: String,
    val price: Int,
    val category: String,
    val emoji: String,
    val description: String
)

val sampleProducts = listOf(
    StoreProduct("Samsung Galaxy Phone", 14999, "Mobiles", "📱", "Android smartphone"),
    StoreProduct("Redmi Smartphone", 9999, "Mobiles", "📱", "Budget smartphone"),
    StoreProduct("iPhone", 49999, "Mobiles", "📱", "Apple smartphone"),

    StoreProduct("HP Laptop", 42999, "Laptops", "💻", "Laptop for study and work"),
    StoreProduct("Dell Laptop", 45999, "Laptops", "💻", "Everyday laptop"),

    StoreProduct("Wireless Headphones", 1499, "Headphones", "🎧", "Wireless audio"),
    StoreProduct("Bluetooth Headphones", 999, "Headphones", "🎧", "Bluetooth audio"),

    StoreProduct("Casual T-Shirt", 499, "Fashion", "👕", "Casual clothing"),
    StoreProduct("Sports Shoes", 1499, "Fashion", "👟", "Comfortable shoes")
)

@Composable
fun CategoryProductsScreen(
    category: String,
    onBackClick: () -> Unit,
    onCompareClick: (String) -> Unit,
    onAddToShoppingClick: (StoreProduct) -> Unit
) {
    var searchText by remember(category) {
        mutableStateOf("")
    }

    val products = sampleProducts.filter {
        it.category.equals(category, ignoreCase = true) &&
                it.name.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F7F4))
    ) {
        // Professional green header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF145A38),
                            Color(0xFF2E8B57)
                        )
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(20.dp)
        ) {
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFD8F3E2)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF26784D),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    text = "‹",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }



            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "BACHATCOM",
                color = Color(0xFFD8F3E2),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$category Collection",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Smart shopping. Better savings.",
                color = Color(0xFFE1F5E8),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Search $category...")
                },
                leadingIcon = {
                    Text("🔍")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color(0xFF183C2B),
                    unfocusedTextColor = Color(0xFF183C2B)
                )
            )
        }

        // Product list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Products",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF183C2B)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${products.size} items",
                color = Color(0xFF28784D),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No products found.\nTry another search.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                products.forEach { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(94.dp)
                                        .background(
                                            Color(0xFFEAF5ED),
                                            RoundedCornerShape(18.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = product.emoji,
                                        fontSize = 44.sp
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.width(14.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = product.name,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF183C2B)
                                    )

                                    Spacer(
                                        modifier = Modifier.height(5.dp)
                                    )

                                    Text(
                                        text = product.description,
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )

                                    Spacer(
                                        modifier = Modifier.height(9.dp)
                                    )

                                    Text(
                                        text = "₹${product.price}",
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF21834A)
                                    )

                                    Text(
                                        text = "Sample price",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Button(
                                onClick = {
                                    onCompareClick(product.name)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF207647)
                                )
                            ) {
                                Text(
                                    text = "⚖ Compare Product",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    onAddToShoppingClick(product)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF207647)
                                )
                            ) {
                                Text(
                                    text = "＋ Add to My Shopping",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
