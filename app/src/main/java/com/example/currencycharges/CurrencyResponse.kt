package com.example.currencycharges

class CurrencyResponse(var base: String,
                       var date: String,
                       var rates: ArrayList<CurrencyRate> = arrayListOf())

