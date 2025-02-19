package com.example.exchangerateapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeRate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Changed id type to Int and corrected default value
    val date: String,
    val base: String,
    val currency: String,
    val rate: Double
)