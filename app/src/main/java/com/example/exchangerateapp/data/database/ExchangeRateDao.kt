package com.example.exchangerateapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExchangeRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ExchangeRate>)

    @Query("SELECT * FROM exchange_rates WHERE date = :date")
    suspend fun getRatesByDate(date: String): List<ExchangeRate>

    @Query("SELECT * FROM exchange_rates")  // 🔹 Agregado
    suspend fun getAllRates(): List<ExchangeRate>  // 🔹 Agregado
}
