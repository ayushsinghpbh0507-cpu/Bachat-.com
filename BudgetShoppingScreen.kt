
package com.bachatcom.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class BudgetProduct(
    val name: String,
    val price: Double
)

@Composable
fun BudgetShoppingScreen() {
    val context = LocalContext.current

    val prefs = remember {
        context.getSharedPreferences(
            "BachatComBudget",
            Context.MODE_PRIVATE
        )
    }

    var productName by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    fun loadProducts(key: String): List<BudgetProduct> {
        return try {
            val array = JSONArray(prefs.getString(key, "[]"))
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                BudgetProduct(
                    item.getString("name"),
                    item.getDouble("price")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val basket = remember {
        mutableStateListOf<BudgetProduct>().apply {
            addAll(loadProducts("basket"))
        }
    }

    val watchlist = remember {
        mutableStateListOf<BudgetProduct>().apply {
            addAll(loadProducts("watchlist"))
        }
    }

    fun saveProducts(key: String, products: List<BudgetProduct>) {
        val array = JSONArray()
        products.forEach { product ->
            array.put(
                JSONObject()
                    .put("name", product.name)
                    .put("price", product.price)
            )
        }
        prefs.edit().putString(key, array.toString()).apply()
    }

    val totalBudget = budget.toDoubleOrNull() ?: 0.0
    val totalCost = basket.sumOf { it.price }
    val remaining = totalBudget - totalCost

    fun openShoppingSite(site: String, query: String) {
        val encoded = Uri.encode(query)
        val url = when (site) {
            "Amazon" -> "https://www.amazon.in/s?k=$encoded"
            else -> "https://www.flipkart.com/search?q=$encoded"
        }

        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            )
        } catch (_: Exception) {
            message = "Website open nahi ho saki."
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "🎯 Budget Shopping",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Text("Smart shopping karein aur apna budget manage karein.")
        }

        item {
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Product ka naam") },
                placeholder = { Text("Jaise Mobile, Shoes") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Total shopping budget (₹)") },
                placeholder = { Text("Jaise 10000") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = productPrice,
                onValueChange = {
                    productPrice = it.filter { c -> c.isDigit() || c == '.' }
                },
                label = { Text("Product ki estimated price (₹)") },
                placeholder = { Text("Jaise 1500") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Button(
                onClick = {
                    val price = productPrice.toDoubleOrNull()
                    if (productName.isNotBlank() && price != null && price > 0) {
                        basket.add(BudgetProduct(productName.trim(), price))
                        saveProducts("basket", basket)
                        message = "Product basket mein add ho gaya."
                        productName = ""
                        productPrice = ""
                    } else {
                        message = "Product ka naam aur valid price enter karein."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text("＋ Add to Shopping Basket")
            }

            Button(
                onClick = {
                    val amount = budget.toDoubleOrNull()
                    if (productName.isNotBlank() && amount != null && amount > 0) {
                        openShoppingSite(
                            "Amazon",
                            "${productName.trim()} under Rs $amount"
                        )
                    } else {
                        message = "Product name aur valid budget enter karein."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search on Amazon →")
            }

            OutlinedButton(
                onClick = {
                    val amount = budget.toDoubleOrNull()
                    if (productName.isNotBlank() && amount != null && amount > 0) {
                        openShoppingSite(
                            "Flipkart",
                            "${productName.trim()} under Rs $amount"
                        )
                    } else {
                        message = "Product name aur valid budget enter karein."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search on Flipkart →")
            }

            OutlinedButton(
                onClick = {
                    if (productName.isNotBlank()) {
                        val amount = budget.toDoubleOrNull()
                        val query = if (amount != null && amount > 0) {
                            "${productName.trim()} alternatives under Rs $amount"
                        } else {
                            "cheaper alternatives to ${productName.trim()}"
                        }
                        openShoppingSite("Amazon", query)
                    } else {
                        message = "Pehle product ka naam enter karein."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔎 Find Cheaper Alternatives")
            }

            if (message.isNotBlank()) {
                Text(message, color = Color(0xFF2E7D32))
            }
        }

        item {
            HorizontalDivider()
            Text(
                "💰 Budget Tracker",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Text("Total budget: ₹${"%.2f".format(totalBudget)}")
            Text("Basket total: ₹${"%.2f".format(totalCost)}")
            Text(
                "Remaining: ₹${"%.2f".format(remaining)}",
                color = if (remaining < 0) Color.Red else Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )

            if (totalBudget > 0 && remaining < 0) {
                Text(
                    "⚠️ Aapka planned basket budget se ₹${"%.2f".format(-remaining)} zyada hai!",
                    color = Color.Red
                )
            } else if (totalBudget > 0 && remaining == 0.0 && basket.isNotEmpty()) {
                Text("Aapka poora budget planned hai.")
            }

            Text(
                "Note: Basket total estimated prices par based hai; ye confirmed purchases nahi hain.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        item {
            HorizontalDivider()
            Text(
                "🛒 Smart Shopping Basket",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (basket.isEmpty()) {
            item { Text("Abhi basket khaali hai. Product aur price add karein.") }
        } else {
            itemsIndexed(basket) { index, product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(product.name, fontWeight = FontWeight.Bold)
                        Text("Estimated price: ₹${"%.2f".format(product.price)}")
                        Row {
                            TextButton(
                                onClick = {
                                    openShoppingSite(
                                        "Amazon",
                                        "${product.name} under Rs ${product.price}"
                                    )
                                }
                            ) {
                                Text("Search")
                            }
                            TextButton(
                                onClick = {
                                    basket.removeAt(index)
                                    saveProducts("basket", basket)
                                    message = "Basket se product remove ho gaya."
                                }
                            ) {
                                Text("Remove", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        item {
            HorizontalDivider()
            Text(
                "📉 Price Watchlist",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Product ko uski estimated price ke saath save karein. " +
                        "Price changes abhi automatically track nahi hote.",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Button(
                onClick = {
                    val price = productPrice.toDoubleOrNull()
                    if (productName.isNotBlank() && price != null && price > 0) {
                        watchlist.add(BudgetProduct(productName.trim(), price))
                        saveProducts("watchlist", watchlist)
                        message = "Product watchlist mein save ho gaya."
                    } else {
                        message = "Watchlist ke liye product name aur valid price enter karein."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("☆ Save to Price Watchlist")
            }
        }

        if (watchlist.isEmpty()) {
            item { Text("Watchlist abhi khaali hai.") }
        } else {
            itemsIndexed(watchlist) { index, product ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold)
                            Text("Saved price: ₹${"%.2f".format(product.price)}")
                        }
                        TextButton(
                            onClick = {
                                watchlist.removeAt(index)
                                saveProducts("watchlist", watchlist)
                                message = "Watchlist se product remove ho gaya."
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "Website par final price, delivery charges aur availability check karein. " +
                        "Search results budget ke andar hone ki guarantee nahi hai.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}