package com.onurerdem.shoppingapp.feature.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.onurerdem.shoppingapp.data.model.ProductsItem
import com.onurerdem.shoppingapp.data.model.ProductsItemDTO
import com.onurerdem.shoppingapp.databinding.ItemProductLayoutBinding

class HomeProductAdapter(private val listener: OnShoppingCartClickListener) :
    ListAdapter<ProductsItemDTO, HomeProductAdapter.ProductViewHolder>(ProductDiffUtil()) {
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
        fun bind(data: ProductsItemDTO, listener: OnShoppingCartClickListener) {
            binding.dataHolder = data

            binding.cardViewProduct.setOnClickListener {
                listener.onProductClick(data)
            }
            binding.listener = listener
            binding.executePendingBindings()
        }
    }

    class ProductDiffUtil : DiffUtil.ItemCallback<ProductsItemDTO>() {
        override fun areItemsTheSame(oldItem: ProductsItemDTO, newItem: ProductsItemDTO): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductsItemDTO, newItem: ProductsItemDTO): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnShoppingCartClickListener {
    fun onAddShoppingCartClick(id: Int?)
    fun onRemoveShoppingCartClick(productsItem: ProductsItemDTO)
    fun onProductClick(product: ProductsItemDTO)
}