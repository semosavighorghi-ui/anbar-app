package com.example.anbar

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, "anbar.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE products(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "price INTEGER NOT NULL," +
                "quantity INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }

    fun insert(p: Product): Long {
        val cv = ContentValues().apply {
            put("name", p.name)
            put("price", p.price)
            put("quantity", p.quantity)
        }
        return writableDatabase.insert("products", null, cv)
    }

    fun update(p: Product): Int {
        val cv = ContentValues().apply {
            put("name", p.name)
            put("price", p.price)
            put("quantity", p.quantity)
        }
        return writableDatabase.update("products", cv, "id=?", arrayOf(p.id.toString()))
    }

    fun delete(id: Long): Int =
        writableDatabase.delete("products", "id=?", arrayOf(id.toString()))

    fun getAll(search: String = ""): List<Product> {
        val list = mutableListOf<Product>()
        val cursor = if (search.isBlank())
            writableDatabase.rawQuery("SELECT * FROM products ORDER BY id DESC", null)
        else
            writableDatabase.rawQuery(
                "SELECT * FROM products WHERE name LIKE ? ORDER BY id DESC",
                arrayOf("%$search%")
            )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Product(
                        id = it.getLong(0),
                        name = it.getString(1),
                        price = it.getLong(2),
                        quantity = it.getInt(3)
                    )
                )
            }
        }
        return list
    }
}
