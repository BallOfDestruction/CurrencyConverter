package com.example.currencycharges

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionChecker.checkSelfPermission(this, android.Manifest.permission.INTERNET)

        val recycleView = findViewById<RecyclerView>(R.id.currency_recycler_view)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycleView.layoutManager = linearLayoutManager
        val container = CurrencyContainer()

        val adapter = CurrencyAdapter(this, container, recycleView)

        recycleView.adapter = adapter
        container.adapter = adapter


        val loader = CurrencyLoader(container)
        val thread = Thread(loader)
        thread.isDaemon = true
        thread.start()
    }
}
