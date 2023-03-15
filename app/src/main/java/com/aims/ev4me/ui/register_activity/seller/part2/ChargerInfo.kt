package com.aims.ev4me.ui.register_activity.seller.part2

data class ChargerInfo(var chargerName: String, var chargerType: ChargerType) {
    var chargerUID: String = ""
    enum class ChargerType {
        NO_LEVEL, LEVEL1, LEVEL2
    }

    fun toHashMap(): HashMap<String, String> {
        return hashMapOf(
            "chargerUID" to chargerUID,
            "chargerName" to chargerName,
            "chargerType" to chargerType.name //This will return the string version of the enum to store in the database
            //Use ChargerType.valueOf(String): ChargerType to get the Enum value back when pulling from database
        )
    }
}