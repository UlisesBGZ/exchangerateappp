package com.example.exchangerateapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerateapp.R
import com.example.exchangerateapp.data.database.ExchangeRate
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateAdapter(
    private val exchangeRates: List<ExchangeRate>,
    private val lastUpdateDateUtcString: String // Renamed parameter to be clearer about UTC
) : RecyclerView.Adapter<ExchangeRateAdapter.ExchangeRateViewHolder>() {

    inner class ExchangeRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyTextView: TextView = itemView.findViewById(R.id.currencyTextView)
        val rateTextView: TextView = itemView.findViewById(R.id.rateTextView)
        val lastUpdateTextView: TextView = itemView.findViewById(R.id.lastUpdateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRateViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exchange_rate, parent, false)
        return ExchangeRateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExchangeRateViewHolder, position: Int) {
        val currentItem = exchangeRates[position]
        holder.currencyTextView.text = currentItem.currency
        holder.rateTextView.text = String.format("%.4f", currentItem.rate)

        // Format and display the last update date in local time (Moroleón, Mexico)
        try {
            val utcFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC") // Set input timezone to UTC
            val utcDate = utcFormat.parse(lastUpdateDateUtcString)

            val localFormat = SimpleDateFormat("MMM dd, yyyy, hh:mm a z", Locale.getDefault()) // Example format
            localFormat.timeZone = TimeZone.getTimeZone("America/Mexico_City") // Set output timezone to Mexico City (CST/CDT)
            val localDateString = localFormat.format(utcDate)

            holder.lastUpdateTextView.text = "Updated: $localDateString"

        } catch (e: Exception) {
            holder.lastUpdateTextView.text = "Updated: N/A" // Handle parsing errors gracefully
            Log.e("Date Format Error", "Error formatting date: ${e.message}", e) // Log error for debugging
        }
    }

    override fun getItemCount() = exchangeRates.size
}