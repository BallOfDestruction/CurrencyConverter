package com.example.currencycharges

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*

class CurrencyAdapter(val activity: MainActivity, val container: CurrencyContainer, val recyclerView: RecyclerView) :
        RecyclerView.Adapter<CurrencyViewHolder>(),
        TextWatcher {

    val locale = (Locale.getDefault() ?: Locale.US)!!

    override fun onViewDetachedFromWindow(holder: CurrencyViewHolder) {
        super.onViewDetachedFromWindow(holder)

        if (holder.item == container.baseCurrency) {
            activity.hideKeyboard(holder.editText)
        }
    }

    override fun onViewAttachedToWindow(holder: CurrencyViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (holder.item == container.baseCurrency) {
            holder.editText.showKeyboard()
        }
    }

    override fun onBindViewHolder(viewHolder: CurrencyViewHolder, position: Int) {
        val item: Currency = if (position == 0)
            container.baseCurrency ?: throw NullPointerException()
        else
            container.currencyList[position - 1]

        viewHolder.editText.removeTextChangedListener(this)

        viewHolder.title.text = item.name
        viewHolder.subtitle.text = item.name
        viewHolder.item = item

        val value: Float
        value = if (position == 0) {
            container.baseCurrency?.value ?: throw java.lang.NullPointerException()
        } else {
            item.value * (container.baseCurrency?.value ?: throw NullPointerException())
        }
        setFormattedValue(viewHolder.editText, value)

        if (position == 0) {
            viewHolder.editText.addTextChangedListener(this)
        }
    }

    fun setFormattedValue(editText: EditText, value: Float) {
        val stringValue: String = String.format(locale, "%.2f", value)
        activity.runOnUiThread {
            editText.setText(stringValue)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return CurrencyViewHolder(view, ::swapItems)
    }

    override fun getItemCount(): Int {
        return if (container.baseCurrency == null) 0 else container.currencyList.size + 1
    }

    private fun swapItems(item: Currency) {
        val baseItem = container.baseCurrency ?: return

        val positionItemIndex = container.currencyList.indexOf(item)
        if (positionItemIndex == -1)
            return

        val currencyItem = container.currencyList[positionItemIndex]
        container.currencyList.removeAt(positionItemIndex)
        container.currencyList.add(0, baseItem)
        container.baseCurrency = currencyItem
        notifyItemMoved(positionItemIndex + 1, 0)

        val newHolder = recyclerView.findViewHolderForAdapterPosition(0)
        if(newHolder != null) {
            if(newHolder is CurrencyViewHolder){
                newHolder.editText.addTextChangedListener(this)
            }
        }

        val oldHolder = recyclerView.findViewHolderForAdapterPosition(positionItemIndex + 1)
        if(oldHolder != null){
            if(oldHolder is CurrencyViewHolder) {
                oldHolder.editText.removeTextChangedListener(this)
            }
        }
    }


    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val text =  s.toString()
        try {
            val format = DecimalFormat("0.#", DecimalFormatSymbols.getInstance(locale))
            format.parse(text).toFloat()
        } catch (e: ParseException) {

        } finally {
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val viewHolderAtZero = recyclerView.findViewHolderForAdapterPosition(0)
        if (viewHolderAtZero is CurrencyViewHolder) {
            val text = viewHolderAtZero.editText.text
            container.updateMainValue(text.toString())
        }
    }
}