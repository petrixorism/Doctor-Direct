package uz.ravshanbaxranov.doctordirect.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.ravshanbaxranov.doctordirect.R

import uz.ravshanbaxranov.doctordirect.data.model.remote.User
import uz.ravshanbaxranov.doctordirect.databinding.DoctorItemBinding


class DoctorsAdapter : ListAdapter<User, DoctorsAdapter.Holder>(ArticleCallback) {

    private var onCLickListener: ((User, ImageView) -> Unit)? = null


    object ArticleCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.uid == newItem.uid && oldItem.firstName == newItem.firstName

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.uid == newItem.uid &&
                    oldItem.avatarUrl == newItem.avatarUrl &&
                    oldItem.firstName == newItem.firstName &&
                    oldItem.lastName == newItem.lastName &&
                    oldItem.available == newItem.available &&
                    oldItem.availableTime == newItem.availableTime &&
                    oldItem.role == newItem.role
    }

    inner class Holder(private val binding: DoctorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            val item = getItem(bindingAdapterPosition)

            Glide.with(binding.avatarIv)
                .load(item.avatarUrl)
                .placeholder(R.drawable.img_1)
                .into(binding.avatarIv)

            binding.nameTv.text = item.firstName + " " + item.lastName
            binding.specialityTv.text = item.speciality
            binding.availableTimeTv.text = item.availableTime
            if (item.available) {
                binding.isAvailableTv.setTextColor(Color.GREEN)
                binding.isAvailableTv.setText(R.string.available)
            } else {
                binding.isAvailableTv.setTextColor(Color.RED)
                binding.isAvailableTv.setText(R.string.unavailable)
            }


        }

        init {
            binding.root.setOnClickListener {
                onCLickListener!!.invoke(getItem(absoluteAdapterPosition), binding.avatarIv)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(DoctorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind()


    fun setOnCLickListener(block: ((User, ImageView) -> Unit)) {
        onCLickListener = block
    }


}
