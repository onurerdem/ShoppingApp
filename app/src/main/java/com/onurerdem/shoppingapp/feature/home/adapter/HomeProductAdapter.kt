package com.onurerdem.shoppingapp.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.databinding.ItemProductLayoutBinding

class HomeProductAdapter(private val listener: OnShoppingCartClickListener) :
    ListAdapter<ProductsItem, HomeProductAdapter.ProductViewHolder>(ProductDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ItemProductLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ProductViewHolder(private val binding: ItemProductLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ProductsItem, listener: OnShoppingCartClickListener) {
            binding.dataHolder = data
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    class ProductDiffUtil : DiffUtil.ItemCallback<ProductsItem>() {
        override fun areItemsTheSame(oldItem: ProductsItem, newItem: ProductsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductsItem, newItem: ProductsItem): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnShoppingCartClickListener {
    fun onAddShoppingCartClick(id: Int?)
    fun onRemoveShoppingCartClick(productsItem: ProductsItem)
}