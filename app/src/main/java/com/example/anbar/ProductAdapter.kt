package com.example.anbar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anbar.databinding.ItemProductBinding

class ProductAdapter(
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    private var items: List<Product> = emptyList()

    inner class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.b.txtName.text = p.name
        holder.b.txtPrice.text = "قیمت: ${Fa.num(p.price)} تومان"
        holder.b.txtQty.text = "موجودی: ${Fa.num(p.quantity.toLong())}"
        holder.b.btnEdit.setOnClickListener { onEdit(p) }
        holder.b.btnDelete.setOnClickListener { onDelete(p) }
    }

    fun submit(list: List<Product>) {
        items = list
        notifyDataSetChanged()
    }
}
