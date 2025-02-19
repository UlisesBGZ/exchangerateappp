package com.example.exchangerateapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.exchangerateapp.data.database.ExchangeRate
import com.example.exchangerateapp.data.network.RetrofitClient
import com.example.exchangerateapp.ui.ExchangeRateAdapter
import com.example.exchangerateapp.worker.ExchangeRateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExchangeRateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        loadExchangeRates()


        schedulePeriodicUpdates()
    }

    private fun loadExchangeRates() {
        Log.d("MainActivity", "loadExchangeRates() started")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiKey = "b3aca349d3227803e2014759"
                val baseCurrency = "USD"

                Log.d("MainActivity", "Before API call")
                val response = RetrofitClient.api.getLatestRates(apiKey, baseCurrency)
                Log.d("MainActivity", "After API call, response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val exchangeRatesResponse = response.body()
                    Log.d("MainActivity", "API response body: $exchangeRatesResponse")
                    exchangeRatesResponse?.conversionRates?.let { ratesMap ->
                        Log.d("MainActivity", "Conversion rates map: $ratesMap")
                        val exchangeRateList = ratesMap.entries.map { entry ->
                            ExchangeRate(
                                date = exchangeRatesResponse.timeLastUpdateUtc ?: "N/A",
                                base = baseCurrency,
                                currency = entry.key,
                                rate = entry.value
                            )
                        }
                        Log.d("MainActivity", "Exchange rate list size: ${exchangeRateList.size}")

                        withContext(Dispatchers.Main) {
                            adapter = ExchangeRateAdapter(exchangeRateList)
                            recyclerView.adapter = adapter
                            Log.d("MainActivity", "Adapter updated on UI thread")
                        }
                    }
                } else {
                    Log.e("API Error", "Error fetching exchange rates: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Exception during API call: ${e.message}", e)
            }
        }
        Log.d("MainActivity", "loadExchangeRates() finished")
    }

    private fun schedulePeriodicUpdates() {
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<ExchangeRateWorker>(
                15,
                TimeUnit.MINUTES
            )
                .build()

        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)
        Log.d("MainActivity", "Periodic ExchangeRateWorker scheduled for every 15 minutes (minimum)")
    }
}