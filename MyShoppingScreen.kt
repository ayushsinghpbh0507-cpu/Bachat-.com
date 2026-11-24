
package com.bachatcom.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ShoppingItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val date: String
)

@Composable
fun MyShoppingScreen(
    shoppingHistory: List<ShoppingItem>,
    onAddOrUpdate: (ShoppingItem) -> Unit,
    onDeleteProduct: (ShoppingItem) -> Unit,
    onClearAll: () -> Unit,
    onUndoDelete: (ShoppingItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var search by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<String?>(null) }
    var deletedItem by remember { mutableStateOf<ShoppingItem?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("All") }

    val dateFormat = remember {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    }

    val visibleItems = shoppingHistory.filter { item ->
        val matchesSearch = item.name.contains(search, ignoreCase = true)
        val matchesFilter = when (filter) {
            "Under ₹500" -> item.price < 500
            "₹500–₹2000" -> item.price in 500.0..2000.0
            "Above ₹2000" -> item.price > 2000
            else -> true
        }
        matchesSearch && matchesFilter
    }

    val total = shoppingHistory.sumOf { it.price * it.quantity }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "🛍️ My Shopping",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )

        Spacer(Modifier.height(6.dp))
        Text("Apni shopping ko aasani se manage karein.", color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Shopping Expense", color = Color.DarkGray)
                Text(
                    "₹${"%.2f".format(total)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text("Total products: ${shoppingHistory.size}")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it.filter { c -> c.isDigit() || c == '.' }
            },
            label = { Text("Price (₹)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = {
                quantity = it.filter(Char::isDigit)
            },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                val parsedPrice = price.toDoubleOrNull()
                val parsedQuantity = quantity.toIntOrNull()

                if (
                    name.isNotBlank() &&
                    parsedPrice != null && parsedPrice >= 0 &&
                    parsedQuantity != null && parsedQuantity > 0
                ) {
                    val oldItem = shoppingHistory.find { it.id == editingId }

                    val item = ShoppingItem(
                        id = editingId ?: System.currentTimeMillis().toString(),
                        name = name.trim(),
                        price = parsedPrice,
                        quantity = parsedQuantity,
                        date = oldItem?.date ?: dateFormat.format(Date())
                    )

                    onAddOrUpdate(item)
                    name = ""
                    price = ""
                    quantity = "1"
                    editingId = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingId == null) "＋ Add Product" else "Save Changes")
        }

        if (editingId != null) {
            TextButton(
                onClick = {
                    name = ""
                    price = ""
                    quantity = "1"
                    editingId = null
                }
            ) {
                Text("Cancel Edit")
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("🔍 Search product") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filter: $filter",
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { showFilter = true }) {
                Text("Choose")
            }
        }

        if (shoppingHistory.isNotEmpty()) {
            OutlinedButton(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All History")
            }
        }

        Spacer(Modifier.height(8.dp))

        if (visibleItems.isEmpty()) {
            Text(
                if (shoppingHistory.isEmpty()) {
                    "🛒 Abhi shopping history khaali hai."
                } else {
                    "Koi matching product nahi mila."
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                item.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Price: ₹${"%.2f".format(item.price)}")
                            Text("Quantity: ${item.quantity}")
                            Text(
                                "Total: ₹${"%.2f".format(item.price * item.quantity)}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text("Date: ${item.date}", color = Color.Gray)

                            Row {
                                TextButton(
                                    onClick = {
                                        name = item.name
                                        price = item.price.toString()
                                        quantity = item.quantity.toString()
                                        editingId = item.id
                                    }
                                ) {
                                    Text("✏️ Edit")
                                }

                                TextButton(
                                    onClick = {
                                        deletedItem = item
                                        onDeleteProduct(item)
                                    }
                                ) {
                                    Text("Delete", color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        deletedItem?.let { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Product deleted", modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        onUndoDelete(item)
                        deletedItem = null
                    }
                ) {
                    Text("Undo")
                }
            }
        }
    }

    if (showFilter) {
        AlertDialog(
            onDismissRequest = { showFilter = false },
            title = { Text("Filter by price") },
            text = {
                Column {
                    listOf("All", "Under ₹500", "₹500–₹2000", "Above ₹2000")
                        .forEach { option ->
                            TextButton(
                                onClick = {
                                    filter = option
                                    showFilter = false
                                }
                            ) {
                                Text(option)
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilter = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all history?") },
            text = { Text("Saare shopping records delete ho jayenge.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        deletedItem = null
                        editingId = null
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
