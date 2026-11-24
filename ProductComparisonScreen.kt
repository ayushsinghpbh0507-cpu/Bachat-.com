package com.bachatcom.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductComparisonScreen(
    productName: String,
    selectedImageUri: Uri?,
    selectedCameraBitmap: Bitmap?,
    identifiedProductName: String,
    onPurchaseConfirmed: (Int) -> Unit
) {

    val context = LocalContext.current

    var backendConnected by remember(productName) {
        mutableStateOf(false)
    }

    var productResponse by remember(productName) {
        mutableStateOf<ProductResponse?>(null)
    }

    var isLoading by remember(productName) {
        mutableStateOf(true)
    }

    var errorMessage by remember(productName) {
        mutableStateOf<String?>(null)
    }

    var showProductCard by remember(productName) {
        mutableStateOf(false)
    }

    var showPriceSection by remember(productName) {
        mutableStateOf(false)
    }

    var showStores by remember(productName) {
        mutableStateOf(false)
    }

    // Purchase confirmation dialog
    var showPurchaseDialog by remember {
        mutableStateOf(false)
    }

    var selectedPurchasePrice by remember {
        mutableStateOf(0)
    }

    var selectedPurchaseStore by remember {
        mutableStateOf("")
    }

    // ------------------------------------------------
    // BACKEND SEARCH
    // ------------------------------------------------

    LaunchedEffect(productName) {

        showProductCard = false
        showPriceSection = false
        showStores = false

        if (productName.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        errorMessage = null

        try {

            productResponse =
                RetrofitClient.api.searchProduct(productName)

            backendConnected = true

        } catch (e: Exception) {

            backendConnected = false

            errorMessage =
                "Backend error: ${e.message}"

        } finally {

            isLoading = false
        }
    }

    // ------------------------------------------------
    // PRODUCT CARD ANIMATION
    // ------------------------------------------------

    LaunchedEffect(productName) {

        kotlinx.coroutines.delay(150)

        showProductCard = true
    }

    // ------------------------------------------------
    // PRICE + STORE ANIMATION
    // ------------------------------------------------

    LaunchedEffect(productResponse) {

        if (productResponse != null) {

            kotlinx.coroutines.delay(250)

            showPriceSection = true

            kotlinx.coroutines.delay(350)

            showStores = true
        }
    }

    // ------------------------------------------------
    // STORE DATA
    // ------------------------------------------------

    val stores = productResponse?.stores ?: emptyList()

    val liveStores = stores.filter {
        it.live_data && it.price != null
    }

    val lowestPrice = liveStores
        .mapNotNull { it.price }
        .minOrNull()

    val highestPrice = liveStores
        .mapNotNull { it.price }
        .maxOrNull()

    val priceDifference =
        if (
            lowestPrice != null &&
            highestPrice != null
        ) {
            highestPrice - lowestPrice
        } else {
            0
        }

    // ------------------------------------------------
    // PURCHASE CONFIRMATION DIALOG
    // ------------------------------------------------

    if (showPurchaseDialog) {

        AlertDialog(
            onDismissRequest = {
                showPurchaseDialog = false
            },
            title = {
                Text(
                    text = "Purchase Confirmation"
                )
            },
            text = {

                val possibleSaving =
                    if (highestPrice != null) {
                        highestPrice - selectedPurchasePrice
                    } else {
                        0
                    }

                Column {

                    Text(
                        text =
                            "Did you purchase this product from $selectedPurchaseStore?"
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    if (possibleSaving > 0) {

                        Text(
                            text =
                                "💰 Possible saving: ₹$possibleSaving",
                            fontWeight =
                                FontWeight.Bold,
                            color =
                                Color(0xFF2E7D32)
                        )

                    } else {

                        Text(
                            text =
                                "No saving was calculated for this purchase.",
                            color =
                                Color.Gray
                        )
                    }
                }
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        val saving =
                            if (highestPrice != null) {
                                (highestPrice - selectedPurchasePrice)
                                    .coerceAtLeast(0)
                            } else {
                                0
                            }

                        if (saving > 0) {
                            onPurchaseConfirmed(saving)
                        }

                        showPurchaseDialog = false
                    }
                ) {
                    Text("Yes, Purchased")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showPurchaseDialog = false
                    }
                ) {
                    Text("Not Yet")
                }
            }
        )
    }

    // ------------------------------------------------
    // MAIN SCREEN
    // ------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F7F3))
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        // ------------------------------------------------
        // HEADER
        // ------------------------------------------------

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
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
                .padding(22.dp)
        ) {

            Text(
                text = "BACHATCOM",
                color = Color(0xFFD0F0DC),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Compare & Save",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Find the best price for your product",
                color = Color(0xFFE4F5E9),
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // ------------------------------------------------
            // PRODUCT DETAILS
            // ------------------------------------------------

            AnimatedVisibility(
                visible = showProductCard,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(700)
                    ) +
                            slideInVertically(
                                animationSpec =
                                    tween(700),
                                initialOffsetY = {
                                    -80
                                }
                            )
            ) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color.White
                        ),
                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "YOUR PRODUCT",
                            color =
                                Color(0xFF218451),
                            fontSize = 12.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text = productName,
                            color =
                                Color(0xFF183C2B),
                            fontSize = 22.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        if (
                            identifiedProductName
                                .isNotBlank()
                        ) {

                            Text(
                                text =
                                    "Identified: $identifiedProductName",
                                color =
                                    Color.Gray,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        // PRODUCT IMAGE

                        AnimatedVisibility(
                            visible =
                                selectedImageUri != null ||
                                        selectedCameraBitmap != null,
                            enter =
                                fadeIn(
                                    animationSpec =
                                        tween(1000)
                                )
                        ) {

                            if (
                                selectedImageUri != null
                            ) {

                                coil.compose.AsyncImage(
                                    model =
                                        selectedImageUri,
                                    contentDescription =
                                        "Selected product image",
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(170.dp),
                                    contentScale =
                                        ContentScale.Fit
                                )

                            } else if (
                                selectedCameraBitmap != null
                            ) {

                                Image(
                                    bitmap =
                                        selectedCameraBitmap
                                            .asImageBitmap(),
                                    contentDescription =
                                        "Captured product image",
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(170.dp),
                                    contentScale =
                                        ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(22.dp)
            )

            // ------------------------------------------------
            // BACKEND STATUS
            // ------------------------------------------------

            AnimatedVisibility(
                visible = isLoading,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(500)
                    )
            ) {

                Column {

                    Text(
                        text =
                            "🔍 Searching live store prices...",
                        color =
                            Color(0xFF218451),
                        fontSize = 14.sp,
                        fontWeight =
                            FontWeight.Medium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    LinearProgressIndicator(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                        color =
                            Color(0xFF2E8B57),
                        trackColor =
                            Color(0xFFD8EBDD)
                    )
                }
            }

            AnimatedVisibility(
                visible =
                    backendConnected,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(700)
                    ) +
                            slideInVertically(
                                animationSpec =
                                    tween(700),
                                initialOffsetY = {
                                    30
                                }
                            )
            ) {

                Column {

                    Text(
                        text =
                            "🟢 Live Backend Connected",
                        color =
                            Color(0xFF218451),
                        fontSize = 13.sp,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible =
                    errorMessage != null,
                enter =
                    fadeIn(
                        animationSpec =
                            tween(500)
                    )
            ) {

                Column {

                    Text(
                        text =
                            errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }
            }

            // ------------------------------------------------
            // PRICE COMPARISON
            // ------------------------------------------------

            Text(
                text =
                    "Compare Store Prices",
                color =
                    Color(0xFF183C2B),
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ------------------------------------------------
            // PRICE DIFFERENCE
            // ------------------------------------------------

            AnimatedVisibility(
                visible =
                    showPriceSection &&
                            liveStores.isNotEmpty(),
                enter =
                    fadeIn(
                        animationSpec =
                            tween(800)
                    ) +
                            slideInVertically(
                                animationSpec =
                                    tween(800),
                                initialOffsetY = {
                                    100
                                }
                            )
            ) {

                Column {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(20.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFDDF3E4)
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation =
                                    4.dp
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    "PRICE DIFFERENCE",
                                color =
                                    Color(0xFF176B45),
                                fontSize = 13.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(5.dp)
                            )

                            Text(
                                text =
                                    "₹$priceDifference",
                                color =
                                    Color(0xFF176B45),
                                fontSize = 32.sp,
                                fontWeight =
                                    FontWeight.ExtraBold
                            )

                            Text(
                                text =
                                    "Lowest live price: ₹$lowestPrice",
                                color =
                                    Color(0xFF315C40),
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(22.dp)
                    )
                }
            }

            // ------------------------------------------------
            // AVAILABLE STORES
            // ------------------------------------------------

            Text(
                text =
                    "Available Stores",
                color =
                    Color(0xFF183C2B),
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            // ------------------------------------------------
            // STORE CARDS
            // ------------------------------------------------

            if (showStores) {

                stores.forEachIndexed { index, store ->

                    key(store.name) {

                        var showStoreCard by remember {
                            mutableStateOf(false)
                        }

                        LaunchedEffect(Unit) {

                            kotlinx.coroutines.delay(
                                index * 250L
                            )

                            showStoreCard = true
                        }

                        AnimatedVisibility(
                            visible =
                                showStoreCard,
                            enter =
                                fadeIn(
                                    animationSpec =
                                        tween(600)
                                ) +
                                        slideInVertically(
                                            animationSpec =
                                                tween(600),
                                            initialOffsetY = {
                                                120
                                            }
                                        )
                        ) {

                            Column {

                                if (
                                    store.live_data &&
                                    store.price != null
                                ) {

                                    PriceCard(
                                        shopName =
                                            store.name,
                                        imageUrl =
                                            store.image,
                                        price =
                                            store.price,
                                        rating =
                                            store.rating
                                                ?: "Not available",
                                        isLowest =
                                            store.price ==
                                                    lowestPrice,
                                        onBuyClick = {

                                            val url =
                                                store.product_url

                                            if (
                                                url.isNotBlank()
                                            ) {

                                                // Open store
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(url)
                                                    )
                                                )

                                                // Remember purchase details
                                                selectedPurchasePrice =
                                                    store.price

                                                selectedPurchaseStore =
                                                    store.name

                                                // Show confirmation when user returns
                                                showPurchaseDialog = true
                                            }
                                        }
                                    )

                                } else {

                                    Card(
                                        modifier =
                                            Modifier.fillMaxWidth(),
                                        shape =
                                            RoundedCornerShape(
                                                18.dp
                                            ),
                                        colors =
                                            CardDefaults.cardColors(
                                                containerColor =
                                                    Color.White
                                            ),
                                        elevation =
                                            CardDefaults.cardElevation(
                                                defaultElevation =
                                                    2.dp
                                            )
                                    ) {

                                        Column(
                                            modifier =
                                                Modifier.padding(
                                                    16.dp
                                                )
                                        ) {

                                            Text(
                                                text =
                                                    store.name,
                                                color =
                                                    Color(0xFF183C2B),
                                                fontSize =
                                                    20.sp,
                                                fontWeight =
                                                    FontWeight.Bold
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        6.dp
                                                    )
                                            )

                                            Text(
                                                text =
                                                    "Price not available",
                                                color =
                                                    Color.Gray,
                                                fontSize =
                                                    14.sp
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        10.dp
                                                    )
                                            )

                                            OutlinedButton(
                                                onClick = {

                                                    if (
                                                        store.product_url
                                                            .isNotBlank()
                                                    ) {

                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse(
                                                                    store.product_url
                                                                )
                                                            )
                                                        )
                                                    }
                                                }
                                            ) {

                                                Text(
                                                    text =
                                                        "Open Store"
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ------------------------------------------------
            // NO STORE DATA
            // ------------------------------------------------

            AnimatedVisibility(
                visible =
                    !isLoading &&
                            stores.isEmpty(),
                enter =
                    fadeIn(
                        animationSpec =
                            tween(600)
                    )
            ) {

                Column {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color.White
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation =
                                    2.dp
                            )
                    ) {

                        Text(
                            text =
                                "No store data available right now.",
                            modifier =
                                Modifier.padding(16.dp),
                            color =
                                Color.Gray
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )
                }
            }

            // ------------------------------------------------
            // BACKEND MESSAGE
            // ------------------------------------------------

            if (
                productResponse != null
            ) {

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text =
                        productResponse?.message
                            ?: "",
                    color =
                        Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(
                text =
                    "Live prices are fetched from available store data.",
                color =
                    Color.Gray,
                fontSize = 12.sp
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }
    }
}