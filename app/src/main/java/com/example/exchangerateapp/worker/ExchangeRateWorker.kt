package com.example.exchangerateapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.exchangerateapp.data.database.AppDatabase
import com.example.exchangerateapp.data.database.ExchangeRate
import com.example.exchangerateapp.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val apiKey = "b3aca349d3227803e2014759"
        val baseCurrency = "USD"
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.exchangeRateDao()

        return try {
            val response = RetrofitClient.api.getLatestRates(apiKey, baseCurrency) //

            if (response.isSuccessful) {
                val exchangeRateResponse = response.body()
                exchangeRateResponse?.conversionRates?.let { ratesMap -> //
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val ratesList = ratesMap.entries.map { (currency, rate) ->
                        ExchangeRate(
                            date = currentDate,
                            base = exchangeRateResponse.baseCode ?: "USD",
                            currency = currency,
                            rate = rate
                        )
                    }

                    withContext(Dispatchers.IO) { //
                        dao.insertAll(ratesList)
                    }
                }
                Result.success()
            } else {
                Log.e("ExchangeRateWorker", "API request failed with code: ${response.code()}")
                Result.failure() //
            }
        } catch (e: HttpException) {
            Log.e("ExchangeRateWorker", "HTTP Exception during API call", e)
            Result.retry()
        } catch (e: Exception) {
            Log.e("ExchangeRateWorker", "Error fetching exchange rates", e)
            Result.failure()
        }
    }
}