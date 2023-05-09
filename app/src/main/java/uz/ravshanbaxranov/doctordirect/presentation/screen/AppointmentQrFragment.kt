package uz.ravshanbaxranov.doctordirect.presentation.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentAppointmentQrBinding
import java.text.SimpleDateFormat
import java.util.Date


class AppointmentQrFragment : Fragment(R.layout.fragment_appointment_qr) {

    private val binding by viewBinding(FragmentAppointmentQrBinding::bind)
    private val args by navArgs<AppointmentQrFragmentArgs>()

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val appointment = args.appointment

        try {
            if (appointment.status == 1) {
                binding.qrCodeIv.isVisible = true
                val bitmap = encodeAsBitmap(args.appointment.getTextDataForQR())
                binding.qrCodeIv.setImageBitmap(bitmap)
            } else {
                binding.diagnosisTv.isVisible = false
                binding.recipeTv.isVisible = false
                binding.conclusionTv.isVisible = false
                binding.textDiagnosis.isVisible = false
                binding.textRecipe.isVisible = false
                binding.textConclusion.isVisible = false
            }

        } catch (ex: WriterException) {
            ex.printStackTrace()
        }


        val time = SimpleDateFormat("HH:mm dd MMMM").format(Date(appointment.date))

        binding.patientNameTv.text = appointment.patient
        binding.aimTv.text = appointment.aim
        binding.dateTv.text = "Sent in $time"
        binding.arrivalTv.text = appointment.arrivalDate
        binding.diagnosisTv.text = appointment.diagnosis
        binding.recipeTv.text = appointment.recipe
        binding.conclusionTv.text = appointment.conclusion

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(str: String?): Bitmap? {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 512, 512)
        val w = bitMatrix.width
        val h = bitMatrix.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    }

}