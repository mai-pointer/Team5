package com.example.didaktikapp

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(private val images: MutableList<Pair<Int, String>>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        val viewHolder = ImageViewHolder(view)

        // Habilitar el arrastre de la imagen
        viewHolder.imageView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val data = View.DragShadowBuilder(v)
                v.startDragAndDrop(null, data, v, 0)
                true
            } else {
                false
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Asignar un identificador único y el texto a cada imagen
        val (imageResource, identifier) = images[position]
        holder.imageView.setImageResource(imageResource)
        holder.imageView.tag = identifier
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun removeImage(position: Int) {
        images.removeAt(position)
        //notifyItemRemoved(position)
    }
}
