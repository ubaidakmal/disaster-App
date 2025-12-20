package com.bc230420212.app.data.model

/**
 * DISASTER TYPE ENUM
 * 
 * This enum defines all the types of disasters that can be reported in the app.
 * Each disaster type has a display name that will be shown in the dropdown.
 */
enum class DisasterType(val displayName: String) {
    FLOOD("Flood"),
    FIRE("Fire"),
    EARTHQUAKE("Earthquake"),
    ACCIDENT("Accident"),
    STORM("Storm"),
    LANDSLIDE("Landslide"),
    OTHER("Other")
}

