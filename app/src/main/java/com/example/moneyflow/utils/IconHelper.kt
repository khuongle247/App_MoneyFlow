package com.example.moneyflow.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconHelper {
    private val iconMap = mapOf(
        "wallet" to Icons.Default.Wallet,
        "restaurant" to Icons.Default.Restaurant,
        "shopping_cart" to Icons.Default.ShoppingCart,
        "directions_car" to Icons.Default.DirectionsCar,
        "home" to Icons.Default.Home,
        "shopping_bag" to Icons.Default.ShoppingBag,
        "payments" to Icons.Default.Payments,
        "local_gas_station" to Icons.Default.LocalGasStation,
        "medical_services" to Icons.Default.MedicalServices,
        "school" to Icons.Default.School,
        "movie" to Icons.Default.Movie,
        "fitness_center" to Icons.Default.FitnessCenter,
        "coffee" to Icons.Default.LocalCafe,
        "flight" to Icons.Default.Flight,
        "phone_android" to Icons.Default.PhoneAndroid,
        "build" to Icons.Default.Build,
        "redeem" to Icons.Default.Redeem,
        "celebration" to Icons.Default.Celebration,
        "pets" to Icons.Default.Pets,
        "electrical_services" to Icons.Default.ElectricalServices
    )

    fun getIcon(name: String?): ImageVector {
        return iconMap[name] ?: Icons.Default.Category
    }

    fun getAllIcons(): List<Pair<String, ImageVector>> {
        return iconMap.toList()
    }
}
