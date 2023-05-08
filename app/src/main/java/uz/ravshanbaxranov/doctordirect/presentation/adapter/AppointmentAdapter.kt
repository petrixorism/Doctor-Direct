package uz.ravshanbaxranov.doctordirect.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.ravshanbaxranov.doctordirect.data.model.remote.Appointment
import uz.ravshanbaxranov.doctordirect.databinding.ItemAppointmentBinding


class AppointmentAdapter(private val role: String = "user") :
    ListAdapter<Appointment, AppointmentAdapter.Holder>(ArticleCallback) {

    private var onCLickListener: ((Appointment) -> Unit)? = null


    object ArticleCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
            oldItem.id == newItem.id && oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean =
            oldItem.id == newItem.id &&
                    oldItem.status == newItem.status &&
                    oldItem.doctor == newItem.doctor &&
                    oldItem.patient == newItem.patient
    }

    inner class Holder(private val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            val item = getItem(bindingAdapterPosition)


            binding.dateTv.text = item.arrivalDate
            binding.statusTv.text = item.status

            if (role=="user"){
                binding.doctorNameTv.text = item.doctor
                binding.specialityTv.text = item.speciality
            } else {
                binding.doctorNameTv.text = item.patient
                binding.specialityTv.text = item.aim
            }

            when (item.status) {
                "Upcoming" -> {
                    binding.statusTv.setTextColor(Color.parseColor("#FFC107"))
                    binding.statusView.setBackgroundColor(Color.parseColor("#FFC107"))
                }

                "Rejected" -> {
                    binding.statusTv.setTextColor(Color.parseColor("#F44336"))
                    binding.statusView.setBackgroundColor(Color.parseColor("#F44336"))
                }

                "Accepted" -> {
                    binding.statusTv.setTextColor(Color.parseColor("#4CAF50"))
                    binding.statusView.setBackgroundColor(Color.parseColor("#4CAF50"))
                }
            }


        }

        init {
            binding.root.setOnClickListener {
                onCLickListener!!.invoke(getItem(absoluteAdapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(ItemAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind()


    fun setOnCLickListener(block: ((Appointment) -> Unit)) {
        onCLickListener = block
    }


}
