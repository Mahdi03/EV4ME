package com.aims.ev4me.ui.register_activity.allUsers.part2

data class VehicleInfo(var carMake: String, var carModel: String, var carColor: String, var licensePlateNumber: String) {
    fun toHashMap(): HashMap<String, String> {
        return hashMapOf(
            "carMake" to this.carMake,
            "carModel" to this.carModel,
            "carColor" to this.carColor,
            "licensePlateNumber" to this.licensePlateNumber
        )
    }
}