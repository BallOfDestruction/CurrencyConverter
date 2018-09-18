package com.example.currencycharges

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import java.io.StringReader

class CurrencyLoader(private val mapContainer: CurrencyContainer) : Runnable {
    private val threadDelay = 1000L
    private var baseUrl = "https://revolut.duckdns.org/latest"
    private val deserializer = Klaxon()

    override fun run() {
        while (true) {
            var urlField = ""
            if (mapContainer.baseCurrency != null) {
                urlField = "?base=${mapContainer.baseCurrency?.name}"
            }

            val requestToServer = "$baseUrl$urlField".httpGet()

            requestToServer.responseString { _, _, result ->

                if (result.component2() == null) {
                    val str = result.get()
                    setJsonDate(str)
                }
            }

            Thread.sleep(threadDelay)
        }
    }

    private fun setJsonDate(json: String) {
        val stringReader = StringReader(json)
        val jsonObject = deserializer.parseJsonObject(stringReader)

        val base = jsonObject["base"]
        val date = jsonObject["date"]
        val rate = jsonObject["rates"] as JsonObject

        val currencyResponse = CurrencyResponse(base.toString(), date.toString())
        for (item in rate) {
            currencyResponse.rates.add(CurrencyRate(item.key, item.value.toString().toFloat()))
        }
        mapContainer.updateItemsByRoute(currencyResponse)
    }
}