package com.example.currencycharges

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CurrencyViewHolder(itemView: View, show: (Currency) -> Unit) : RecyclerView.ViewHolder(itemView), View.OnFocusChangeListener {
    val title = itemView.findViewById<TextView>(R.id.currency_item_title)!!
    val subtitle = itemView.findViewById<TextView>(R.id.currency_item_subtitle)!!
    val editText = itemView.findViewById<EditText>(R.id.currency_item_edit)!!
    lateinit var item: Currency

    private var _show = show


    init {
        editText.onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v == editText) {
            if (hasFocus) {
                _show.invoke(item)
            }
        }
    }
}
