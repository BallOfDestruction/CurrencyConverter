package com.example.currencycharges

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException

class CurrencyContainer {
    var baseCurrency: Currency? = null
    val currencyList = arrayListOf<Currency>()
    var adapter: CurrencyAdapter? = null

    fun updateItemsByRoute(currencyResponse: CurrencyResponse) {
        updateBaseCurrency(currencyResponse.base)

        for (item in currencyResponse.rates) {
            addItem(item)
        }

        for (item in currencyList) {
            val similarItem = currencyResponse.rates.firstOrNull{ currency -> currency.name.equals(item.name, true) }
            if(similarItem == null) {
                var index = currencyList.indexOf(item)
                currencyList.removeAt(index)
                adapter?.notifyItemRemoved(index + 1)
            }
        }
    }

    private fun updateBaseCurrency(base: String) {
        if (baseCurrency == null) {
            baseCurrency = Currency(base, 1f)
            adapter?.activity?.runOnUiThread {
                adapter?.notifyItemInserted(0)
            }
        }
    }

    private fun addItem(item: CurrencyRate) {
        val similarItem = currencyList.firstOrNull { currency -> currency.name.equals(item.name, true) }
        if (similarItem == null) {
            currencyList.add(Currency(item.name, item.value))
            adapter?.activity?.runOnUiThread {
                adapter?.notifyItemInserted(currencyList.size)
            }
        } else {
            similarItem.value = item.value
            val index = currencyList.indexOf(similarItem)

            val holder = adapter?.recyclerView?.findViewHolderForAdapterPosition(index + 1)
            if (holder is CurrencyViewHolder) {
                val amount = baseCurrency?.value ?: 0f
                adapter?.setFormattedValue(holder.editText, amount * item.value)
            }
        }
    }

    fun updateMainValue(value: String) {
        val floatValue: Float = try {
            val format = DecimalFormat("0.#", DecimalFormatSymbols.getInstance(adapter?.locale))
            format.parse(value).toFloat()
        } catch (e: ParseException) {
            0f
        } finally {
        }

        updateMainValue(floatValue)
    }

    private fun updateMainValue(value: Float) {
        baseCurrency?.value = value
        adapter?.activity?.runOnUiThread {
            adapter?.notifyItemRangeChanged(1, currencyList.size)
        }
    }
}

