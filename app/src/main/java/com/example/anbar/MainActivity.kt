package com.example.anbar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anbar.databinding.ActivityMainBinding
import com.example.anbar.databinding.DialogProductBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var db: DbHelper
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        db = DbHelper(this)

        adapter = ProductAdapter(
            onEdit = { showProductDialog(it) },
            onDelete = { confirmDelete(it) }
        )
        b.recycler.layoutManager = LinearLayoutManager(this)
        b.recycler.adapter = adapter

        b.fab.setOnClickListener { showProductDialog(null) }

        b.txtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = reload()
            override fun beforeTextChanged(s: CharSequence?, a: Int, c: Int, d: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, c: Int, d: Int) {}
        })

        reload()
    }

    private fun reload() {
        val list = db.getAll(b.txtSearch.text.toString())
        adapter.submit(list)
        b.txtEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        val totalValue = list.sumOf { it.price * it.quantity }
        b.txtSummary.text = getString(R.string.summary, list.size, Fa.num(totalValue))
    }

    private fun showProductDialog(existing: Product?) {
        val d = DialogProductBinding.inflate(layoutInflater)
        existing?.let {
            d.txtName.setText(it.name)
            d.txtPrice.setText(it.price.toString())
            d.txtQty.setText(it.quantity.toString())
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existing == null) R.string.add_product else R.string.edit_product)
            .setView(d.root)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = d.txtName.text.toString().trim()
                val price = d.txtPrice.text.toString().trim().toLongOrNull() ?: -1L
                val qty = d.txtQty.text.toString().trim().toIntOrNull() ?: -1

                if (name.isEmpty()) {
                    d.tilName.error = getString(R.string.err_name); return@setOnClickListener
                }
                if (price < 0) {
                    d.tilPrice.error = getString(R.string.err_price); return@setOnClickListener
                }
                if (qty < 0) {
                    d.tilQty.error = getString(R.string.err_qty); return@setOnClickListener
                }

                if (existing == null)
                    db.insert(Product(name = name, price = price, quantity = qty))
                else
                    db.update(existing.copy(name = name, price = price, quantity = qty))

                reload()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun confirmDelete(p: Product) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage("«${p.name}» حذف شود؟")
            .setPositiveButton(R.string.delete) { _, _ ->
                db.delete(p.id)
                reload()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
