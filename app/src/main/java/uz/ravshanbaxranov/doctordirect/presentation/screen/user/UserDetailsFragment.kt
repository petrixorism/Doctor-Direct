package uz.ravshanbaxranov.doctordirect.presentation.screen.user

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import uz.ravshanbaxranov.doctordirect.R
import uz.ravshanbaxranov.doctordirect.databinding.FragmentUserDetailsBinding

class UserDetailsFragment : Fragment(R.layout.fragment_user_details) {

    private val binding by viewBinding(FragmentUserDetailsBinding::bind)
    private val args by navArgs<UserDetailsFragmentArgs>(
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val user = args.user
        var gender = if (user.male) {
            getString(R.string.male)
        } else {
            getString(R.string.female)
        }


        binding.patientNameTv.text = user.getFullName()
        binding.birthTv.text = user.birthDate
        binding.emailTv.text = user.email


        binding.genderTv.text = gender

        Glide.with(binding.avatarIv)
            .load(user.avatarUrl)
            .placeholder(R.drawable.img_1)
            .into(binding.avatarIv)


        val textForQR = "uid : ${user.username}\n" +
                "${getString(R.string.full_name) + ": " + user.getFullName()}\n" +
                "${getString(R.string.gender) + ": " + gender}\n" +
                "${getString(R.string.birth_date) + ": " + user.birthDate}\n" +
                (getString(R.string.email) + ": " + user.email)
        try {
            val bitmap = encodeAsBitmap(textForQR)
            binding.qrCodeIv.setImageBitmap(bitmap)
        } catch (ex: WriterException) {
            ex.printStackTrace()
        }


    }

    @Throws(WriterException::class)
    fun encodeAsBitmap(str: String?): Bitmap? {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 1024, 1024)
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