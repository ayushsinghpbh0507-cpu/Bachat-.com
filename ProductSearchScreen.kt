package com.bachatcom.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

private val BachatGreen = Color(0xFF08784D)
private val BachatDarkGreen = Color(0xFF075638)
private val BachatLightGreen = Color(0xFFEAF7EF)
private val BachatBackground = Color(0xFFF7FAF8)

@Composable
fun ProductSearchScreen(
    openImageOnStart: Boolean = false,
    initialCategory: String = "",
    onBackClick: () -> Unit,
    onCompareClick: (String) -> Unit,
    onProductIdentified: (String) -> Unit,
    selectedImageUri: Uri?,
    onImageUriChange: (Uri?) -> Unit,
    selectedCameraBitmap: Bitmap?,
    onCameraBitmapChange: (Bitmap?) -> Unit
) {
    val context = LocalContext.current

    var productName by remember(initialCategory) {
        mutableStateOf(initialCategory)
    }

    var productLink by remember {
        mutableStateOf("")
    }

    var identifiedProductName by remember {
        mutableStateOf("Product not identified yet")
    }

    var isIdentifying by remember {
        mutableStateOf(false)
    }

    var identificationFailed by remember {
        mutableStateOf(false)
    }

    fun identifyProduct(bitmap: Bitmap) {
        isIdentifying = true
        identificationFailed = false
        identifiedProductName = "Identifying image..."

        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(
            ImageLabelerOptions.DEFAULT_OPTIONS
        )

        labeler.process(image)
            .addOnSuccessListener { labels ->
                identifiedProductName = labels
                    .maxByOrNull { it.confidence }
                    ?.text ?: "Product not identified"

                identificationFailed =
                    identifiedProductName == "Product not identified"

                onProductIdentified(identifiedProductName)
                isIdentifying = false
            }
            .addOnFailureListener {
                identifiedProductName = "Identification failed"
                identificationFailed = true
                isIdentifying = false
            }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onImageUriChange(uri)
            onCameraBitmapChange(null)
            isIdentifying = true
            identificationFailed = false
            identifiedProductName = "Identifying image..."

            try {
                val image = InputImage.fromFilePath(context, uri)
                val labeler = ImageLabeling.getClient(
                    ImageLabelerOptions.DEFAULT_OPTIONS
                )

                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        identifiedProductName = labels
                            .maxByOrNull { it.confidence }
                            ?.text ?: "Product not identified"

                        identificationFailed =
                            identifiedProductName == "Product not identified"

                        onProductIdentified(identifiedProductName)
                        isIdentifying = false
                    }
                    .addOnFailureListener {
                        identifiedProductName = "Identification failed"
                        identificationFailed = true
                        isIdentifying = false
                    }
            } catch (_: Exception) {
                identifiedProductName = "Could not open image"
                identificationFailed = true
                isIdentifying = false
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onCameraBitmapChange(bitmap)
            onImageUriChange(null)
            identifyProduct(bitmap)
        }
    }

    LaunchedEffect(openImageOnStart) {
        if (openImageOnStart) {
            galleryLauncher.launch("image/*")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BachatBackground)
    ) {
        // Top header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(BachatDarkGreen, BachatGreen)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text(
                    text = "‹ Back",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "BachatCom",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Compare • Save • Shop Smart",
                    color = Color(0xFFD7F3E3),
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Page title
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Smart Product Search",
                    color = BachatDarkGreen,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Enter product details, add an image or link, " +
                            "and get ready to compare prices.",
                    color = Color(0xFF5D7067),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            // Product details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Product Name") },
                        placeholder = {
                            Text("e.g. iPhone 15, Nike Shoes")
                        },
                        leadingIcon = {
                            Text("🏷️", fontSize = 21.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BachatGreen,
                            focusedLabelColor = BachatGreen,
                            cursorColor = BachatGreen
                        )
                    )

                    OutlinedTextField(
                        value = productLink,
                        onValueChange = { productLink = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Product Link (Optional)") },
                        placeholder = {
                            Text("Paste product URL here")
                        },
                        leadingIcon = {
                            Text("🔗", fontSize = 21.sp)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BachatGreen,
                            focusedLabelColor = BachatGreen,
                            cursorColor = BachatGreen
                        )
                    )
                }
            }

            // Image upload section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BachatLightGreen
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Add Product Image",
                        color = BachatDarkGreen,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Upload a photo or take a picture",
                        color = Color(0xFF557467),
                        fontSize = 14.sp
                    )

                    Button(
                        onClick = {
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BachatGreen
                        )
                    ) {
                        Text(
                            text = "▧  Upload from Gallery",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            cameraLauncher.launch(null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BachatDarkGreen
                        )
                    ) {
                        Text(
                            text = "📷  Take Photo",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Selected image preview
            if (selectedImageUri != null || selectedCameraBitmap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Selected Product",
                            color = BachatDarkGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (selectedImageUri != null) {
                            coil.compose.AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected product image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else if (selectedCameraBitmap != null) {
                            Image(
                                bitmap = selectedCameraBitmap.asImageBitmap(),
                                contentDescription = "Captured product image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        HorizontalDivider(color = Color(0xFFE3ECE6))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (isIdentifying) "⏳" else if (
                                    identificationFailed
                                ) "⚠️" else "✨",
                                fontSize = 24.sp
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when {
                                        isIdentifying -> "Identifying Image"
                                        identificationFailed ->
                                            "Identification Incomplete"
                                        identifiedProductName !=
                                                "Product not identified yet" ->
                                            "Image Label Identified"
                                        else -> "Image Selected"
                                    },
                                    color = BachatGreen,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = identifiedProductName,
                                    color = Color(0xFF5D7067),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Open product link
            OutlinedButton(
                onClick = {
                    val uri = Uri.parse(productLink.trim())
                    if (
                        uri.scheme in listOf("http", "https") &&
                        !uri.host.isNullOrBlank()
                    ) {
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, uri)
                            )
                        } catch (_: Exception) {
                            android.widget.Toast.makeText(
                                context,
                                "Could not open this link",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        android.widget.Toast.makeText(
                            context,
                            "Enter a valid product URL starting with https://",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BachatGreen
                )
            ) {
                Text(
                    text = "🌐  Open Product Link",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Main compare button
            Button(
                onClick = {
                    val result = when {
                        productName.isNotBlank() -> productName.trim()

                        identifiedProductName !=
                                "Product not identified yet" &&
                                identifiedProductName !=
                                "Product not identified" &&
                                identifiedProductName !=
                                "Identification failed" &&
                                identifiedProductName !=
                                "Identifying image..." &&
                                !identificationFailed ->
                            identifiedProductName

                        productLink.isNotBlank() ->
                            productLink.trim()

                        else -> ""
                    }

                    if (result.isNotBlank()) {
                        onCompareClick(result)
                    } else {
                        android.widget.Toast.makeText(
                            context,
                            "Enter a product name, link, or select an image",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BachatDarkGreen
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "⌕  Compare Product   →",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Helpful tip
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE7F5EB)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("💡", fontSize = 24.sp)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Pro Tip",
                            color = BachatDarkGreen,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Use a clear product photo or enter the " +
                                    "product name for a better comparison.",
                            color = Color(0xFF557467),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            Text(
                text = "Save Money  •  Shop Smart  •  BachatCom",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = BachatGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



