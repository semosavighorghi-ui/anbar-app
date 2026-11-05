package com.example.anbar

import java.util.Locale

data class Product(
    val id: Long = 0,
    val name: String,
    val price: Long,
    val quantity: Int
)

object Fa {
    private val faDigits = "۰۱۲۳۴۵۶۷۸۹"

    fun num(n: Long): String =
        String.format(Locale.US, "%,d", n)
            .map { c -> if (c in '0'..'9') faDigits[c - '0'] else c }
            .joinToString("")
}
