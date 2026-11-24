package com.bachatcom.app

import android.net.Uri
import android.content.Context

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import org.json.JSONArray
import org.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BachatComApp() {

    MaterialTheme {

        val navController = rememberNavController()

        val context =
            androidx.compose.ui.platform.LocalContext.current

        var selectedImageUri by remember {
            mutableStateOf<Uri?>(null)
        }

        var selectedCameraBitmap by remember {
            mutableStateOf<android.graphics.Bitmap?>(null)
        }

        var identifiedProductName by remember {
            mutableStateOf(
                "Product not identified yet"
            )
        }

        val sharedPreferences = remember {

            context.getSharedPreferences(
                "BachatComPreferences",
                Context.MODE_PRIVATE
            )
        }

        // ------------------------------------------------
        // TOTAL SAVINGS
        // ------------------------------------------------

        var totalSavings by remember {

            mutableIntStateOf(
                sharedPreferences.getInt(
                    "total_savings",
                    0
                )
            )
        }

        // ------------------------------------------------
        // PROFILE
        // ------------------------------------------------

        var profileComplete by remember {

            mutableStateOf(
                sharedPreferences.getBoolean(
                    "profile_complete",
                    false
                )
            )
        }

        var profileName by remember {

            mutableStateOf(
                sharedPreferences.getString(
                    "profile_name",
                    ""
                ) ?: ""
            )
        }

        var profileMobile by remember {

            mutableStateOf(
                sharedPreferences.getString(
                    "profile_mobile",
                    ""
                ) ?: ""
            )
        }

        var profileEmail by remember {

            mutableStateOf(
                sharedPreferences.getString(
                    "profile_email",
                    ""
                ) ?: ""
            )
        }

        var profileCity by remember {

            mutableStateOf(
                sharedPreferences.getString(
                    "profile_city",
                    ""
                ) ?: ""
            )
        }

        // ------------------------------------------------
        // SHOPPING HISTORY
        // ------------------------------------------------

        fun loadHistory(): List<ShoppingItem> {

            val savedJson =
                sharedPreferences.getString(
                    "shopping_history_items",
                    null
                )

            if (savedJson != null) {

                return try {

                    val array =
                        JSONArray(savedJson)

                    List(array.length()) { index ->

                        val obj =
                            array.getJSONObject(index)

                        ShoppingItem(
                            id =
                                obj.getString("id"),

                            name =
                                obj.getString("name"),

                            price =
                                obj.getDouble("price"),

                            quantity =
                                obj.getInt("quantity"),

                            date =
                                obj.getString("date")
                        )
                    }

                } catch (_: Exception) {

                    emptyList()
                }
            }

            val oldNames =
                sharedPreferences.getStringSet(
                    "shopping_history",
                    emptySet()
                ) ?: emptySet()

            return oldNames.mapIndexed {
                    index,
                    name ->

                ShoppingItem(
                    id =
                        "old_${index}_${name.hashCode()}",

                    name =
                        name,

                    price =
                        0.0,

                    quantity =
                        1,

                    date =
                        SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                        ).format(Date())
                )
            }
        }

        val shoppingHistory =
            remember {

                mutableStateListOf<ShoppingItem>().apply {

                    addAll(
                        loadHistory()
                    )
                }
            }

        // ------------------------------------------------
        // SAVE SHOPPING HISTORY
        // ------------------------------------------------

        fun saveHistory() {

            val array =
                JSONArray()

            shoppingHistory.forEach { item ->

                array.put(

                    JSONObject()

                        .put(
                            "id",
                            item.id
                        )

                        .put(
                            "name",
                            item.name
                        )

                        .put(
                            "price",
                            item.price
                        )

                        .put(
                            "quantity",
                            item.quantity
                        )

                        .put(
                            "date",
                            item.date
                        )
                )
            }

            sharedPreferences
                .edit()
                .putString(
                    "shopping_history_items",
                    array.toString()
                )
                .apply()
        }

        // ------------------------------------------------
        // SAVE PROFILE
        // ------------------------------------------------

        fun saveProfile(
            name: String,
            mobile: String,
            email: String,
            city: String
        ) {

            profileName = name
            profileMobile = mobile
            profileEmail = email
            profileCity = city

            sharedPreferences
                .edit()

                .putString(
                    "profile_name",
                    name
                )

                .putString(
                    "profile_mobile",
                    mobile
                )

                .putString(
                    "profile_email",
                    email
                )

                .putString(
                    "profile_city",
                    city
                )

                .putBoolean(
                    "profile_complete",
                    true
                )

                .apply()
        }

        // ------------------------------------------------
        // SAVE PURCHASE SAVING
        // ------------------------------------------------

        fun addPurchaseSaving(
            saving: Int
        ) {

            if (saving <= 0) {
                return
            }

            totalSavings += saving

            sharedPreferences
                .edit()
                .putInt(
                    "total_savings",
                    totalSavings
                )
                .apply()
        }

        var selectedTab by remember {

            mutableStateOf(

                if (profileComplete)
                    "Home"
                else
                    "Profile"
            )
        }

        // ------------------------------------------------
        // SCAFFOLD
        // ------------------------------------------------

        Scaffold(

            bottomBar = {

                if (profileComplete) {

                    NavigationBar {

                        NavigationBarItem(

                            selected =
                                selectedTab == "Home",

                            onClick = {

                                selectedTab =
                                    "Home"

                                navController.navigate(
                                    "home"
                                ) {
                                    launchSingleTop =
                                        true
                                }
                            },

                            icon = {
                                Text("🏠")
                            },

                            label = {
                                Text("Home")
                            }
                        )

                        NavigationBarItem(

                            selected =
                                selectedTab == "Compare",

                            onClick = {

                                selectedTab =
                                    "Compare"

                                navController.navigate(
                                    "search"
                                ) {
                                    launchSingleTop =
                                        true
                                }
                            },

                            icon = {
                                Text("🔍")
                            },

                            label = {
                                Text("Compare")
                            }
                        )

                        NavigationBarItem(

                            selected =
                                selectedTab == "Profile",

                            onClick = {

                                selectedTab =
                                    "Profile"

                                navController.navigate(
                                    "profile"
                                ) {
                                    launchSingleTop =
                                        true
                                }
                            },

                            icon = {
                                Text("👤")
                            },

                            label = {
                                Text("Profile")
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->

            androidx.compose.foundation.layout.Box(

                modifier =
                    Modifier.padding(
                        innerPadding
                    )
            ) {

                NavHost(

                    navController =
                        navController,

                    startDestination =
                        if (profileComplete)
                            "home"
                        else
                            "profileSetup"
                ) {

                    // ------------------------------------------------
                    // PROFILE SETUP
                    // ------------------------------------------------

                    composable(
                        "profileSetup"
                    ) {

                        ProfileSetupScreen(

                            onSaveProfile =
                                {
                                        name,
                                        mobile,
                                        email,
                                        city ->

                                    saveProfile(
                                        name,
                                        mobile,
                                        email,
                                        city
                                    )

                                    profileComplete =
                                        true

                                    selectedTab =
                                        "Home"

                                    navController.navigate(
                                        "home"
                                    ) {

                                        popUpTo(
                                            "profileSetup"
                                        ) {
                                            inclusive =
                                                true
                                        }

                                        launchSingleTop =
                                            true
                                    }
                                }
                        )
                    }

                    // ------------------------------------------------
                    // HOME
                    // ------------------------------------------------

                    composable("home") {

                        HomeScreen(

                            onSearchClick = {

                                selectedTab =
                                    "Compare"

                                navController.navigate(
                                    "search"
                                )
                            },

                            onImageClick = {

                                selectedTab =
                                    "Compare"

                                navController.navigate(
                                    "search?openImage=true"
                                )
                            },

                            onBudgetShoppingClick = {

                                selectedTab =
                                    "Home"

                                navController.navigate(
                                    "budget"
                                )
                            },

                            onCategoryClick = {
                                    category ->

                                selectedTab =
                                    "Home"

                                navController.navigate(
                                    "categoryProducts/${Uri.encode(category)}"
                                )
                            }
                        )
                    }

                    // ------------------------------------------------
                    // BUDGET
                    // ------------------------------------------------

                    composable("budget") {

                        BudgetShoppingScreen()
                    }

                    // ------------------------------------------------
                    // CATEGORY PRODUCTS
                    // ------------------------------------------------

                    composable(

                        route =
                            "categoryProducts/{category}",

                        arguments =
                            listOf(

                                navArgument(
                                    "category"
                                ) {
                                    type =
                                        NavType.StringType
                                }
                            )
                    ) { backStackEntry ->

                        val category =
                            backStackEntry
                                .arguments
                                ?.getString(
                                    "category"
                                ) ?: ""

                        CategoryProductsScreen(

                            category =
                                category,

                            onBackClick = {

                                navController
                                    .popBackStack()
                            },

                            onCompareClick = {
                                    productName ->

                                navController.navigate(
                                    "comparison/${Uri.encode(productName)}"
                                )
                            },

                            onAddToShoppingClick = {
                                    product ->

                                val existingItem =
                                    shoppingHistory.find {

                                        it.name.equals(
                                            product.name,
                                            ignoreCase =
                                                true
                                        )
                                    }

                                if (
                                    existingItem == null
                                ) {

                                    shoppingHistory.add(

                                        ShoppingItem(

                                            id =
                                                System.currentTimeMillis()
                                                    .toString(),

                                            name =
                                                product.name,

                                            price =
                                                product.price
                                                    .toDouble(),

                                            quantity =
                                                1,

                                            date =
                                                SimpleDateFormat(
                                                    "dd-MM-yyyy",
                                                    Locale.getDefault()
                                                ).format(
                                                    Date()
                                                )
                                        )
                                    )

                                    saveHistory()
                                }
                            }
                        )
                    }

                    // ------------------------------------------------
                    // PRODUCT SEARCH
                    // ------------------------------------------------

                    composable(

                        route =
                            "search?openImage={openImage}&category={category}",

                        arguments =
                            listOf(

                                navArgument(
                                    "openImage"
                                ) {

                                    type =
                                        NavType.BoolType

                                    defaultValue =
                                        false
                                },

                                navArgument(
                                    "category"
                                ) {

                                    type =
                                        NavType.StringType

                                    defaultValue =
                                        ""

                                    nullable =
                                        false
                                }
                            )
                    ) { backStackEntry ->

                        ProductSearchScreen(

                            openImageOnStart =
                                backStackEntry
                                    .arguments
                                    ?.getBoolean(
                                        "openImage"
                                    ) ?: false,

                            initialCategory =
                                backStackEntry
                                    .arguments
                                    ?.getString(
                                        "category"
                                    ) ?: "",

                            onBackClick = {

                                navController
                                    .popBackStack()

                                selectedTab =
                                    "Home"
                            },

                            // IMPORTANT:
                            // Compare karne par ab
                            // My Shopping mein automatic
                            // product add nahi hoga.

                            onCompareClick = {
                                    productName ->

                                navController.navigate(
                                    "comparison/${Uri.encode(productName)}"
                                )
                            },

                            onProductIdentified = {
                                    name ->

                                identifiedProductName =
                                    name
                            },

                            selectedImageUri =
                                selectedImageUri,

                            onImageUriChange = {
                                selectedImageUri =
                                    it
                            },

                            selectedCameraBitmap =
                                selectedCameraBitmap,

                            onCameraBitmapChange = {
                                selectedCameraBitmap =
                                    it
                            }
                        )
                    }

                    // ------------------------------------------------
                    // PRODUCT COMPARISON
                    // ------------------------------------------------

                    composable(
                        "comparison/{productName}"
                    ) { backStackEntry ->

                        val productName =
                            backStackEntry
                                .arguments
                                ?.getString(
                                    "productName"
                                ) ?: ""

                        ProductComparisonScreen(

                            productName =
                                productName,

                            selectedImageUri =
                                selectedImageUri,

                            selectedCameraBitmap =
                                selectedCameraBitmap,

                            identifiedProductName =
                                identifiedProductName,

                            onPurchaseConfirmed = {
                                    saving ->

                                addPurchaseSaving(
                                    saving
                                )
                            }
                        )
                    }

                    // ------------------------------------------------
                    // PROFILE
                    // ------------------------------------------------

                    composable("profile") {

                        ProfileScreen(

                            onOptionClick = {
                                    option ->

                                navController.navigate(
                                    "profile/${Uri.encode(option)}"
                                )
                            },

                            userName =
                                profileName,

                            userMobile =
                                profileMobile,

                            userEmail =
                                profileEmail,

                            userCity =
                                profileCity,

                            onSaveProfile = {
                                    name,
                                    mobile,
                                    email,
                                    city ->

                                saveProfile(
                                    name,
                                    mobile,
                                    email,
                                    city
                                )
                            },

                            onDeleteAccount = {

                                shoppingHistory.clear()

                                totalSavings =
                                    0

                                sharedPreferences
                                    .edit()
                                    .clear()
                                    .commit()

                                profileName =
                                    ""

                                profileMobile =
                                    ""

                                profileEmail =
                                    ""

                                profileCity =
                                    ""

                                selectedImageUri =
                                    null

                                selectedCameraBitmap =
                                    null

                                identifiedProductName =
                                    "Product not identified yet"

                                profileComplete =
                                    false

                                selectedTab =
                                    "Profile"

                                navController.navigate(
                                    "profileSetup"
                                ) {

                                    popUpTo(
                                        "profile"
                                    ) {
                                        inclusive =
                                            true
                                    }

                                    launchSingleTop =
                                        true
                                }
                            }
                        )
                    }

                    // ------------------------------------------------
                    // MY SHOPPING / MY SAVINGS / SETTINGS
                    // ------------------------------------------------

                    composable(
                        "profile/{option}"
                    ) { backStackEntry ->

                        val option =
                            backStackEntry
                                .arguments
                                ?.getString(
                                    "option"
                                ) ?: ""

                        if (
                            option ==
                            "My Shopping"
                        ) {

                            MyShoppingScreen(

                                shoppingHistory =
                                    shoppingHistory,

                                onAddOrUpdate = {
                                        item ->

                                    val index =
                                        shoppingHistory
                                            .indexOfFirst {

                                                it.id ==
                                                        item.id
                                            }

                                    if (
                                        index >= 0
                                    ) {

                                        shoppingHistory[
                                            index
                                        ] = item

                                    } else {

                                        shoppingHistory
                                            .add(
                                                0,
                                                item
                                            )
                                    }

                                    saveHistory()
                                },

                                onDeleteProduct = {
                                        item ->

                                    shoppingHistory
                                        .removeAll {

                                            it.id ==
                                                    item.id
                                        }

                                    saveHistory()
                                },

                                onUndoDelete = {
                                        item ->

                                    if (
                                        shoppingHistory.none {

                                            it.id ==
                                                    item.id
                                        }
                                    ) {

                                        shoppingHistory
                                            .add(
                                                0,
                                                item
                                            )

                                        saveHistory()
                                    }
                                },

                                onClearAll = {

                                    shoppingHistory
                                        .clear()

                                    saveHistory()
                                }
                            )

                        } else if (
                            option ==
                            "My Savings"
                        ) {

                            MySavingsScreen(

                                totalSavings =
                                    totalSavings
                            )

                        } else {

                            AlertDialog(

                                onDismissRequest = {

                                    navController
                                        .popBackStack()
                                },

                                title = {
                                    Text(option)
                                },

                                text = {

                                    Text(
                                        "$option page will be available here."
                                    )
                                },

                                confirmButton = {

                                    TextButton(

                                        onClick = {

                                            navController
                                                .popBackStack()
                                        }
                                    ) {

                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}