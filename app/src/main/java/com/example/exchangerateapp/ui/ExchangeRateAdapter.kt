package com.example.exchangerateapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerateapp.R
import com.example.exchangerateapp.data.database.ExchangeRate

class ExchangeRateAdapter(private val rates: List<ExchangeRate>) : RecyclerView.Adapter<ExchangeRateAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCurrency: TextView = itemView.findViewById(R.id.txtCurrency)
        val txtRate: TextView = itemView.findViewById(R.id.txtRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exchange_rate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rate = rates[position]
        holder.txtCurrency.text = rate.currency
        holder.txtRate.text = rate.rate.toString()
    }

    override fun getItemCount(): Int = rates.size
}

