package cn.funcoding.fungallery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import cn.funcoding.fungallery.R
import cn.funcoding.fungallery.model.MediaStoreImage
import com.bumptech.glide.Glide

class GalleryAdapter(private val onClick: (MediaStoreImage) -> Unit) :
    ListAdapter<MediaStoreImage, ImageViewHolder>(MediaStoreImage.DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.gallery_layout, parent, false)
        return ImageViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val mediaStoreImage = getItem(position)
        holder.rootView.tag = mediaStoreImage

        Glide.with(holder.imageView)
            .load(mediaStoreImage.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }

}