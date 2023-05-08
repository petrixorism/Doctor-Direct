package uz.ravshanbaxranov.doctordirect.other

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.vmadalin.easypermissions.EasyPermissions
import uz.ravshanbaxranov.doctordirect.other.Constants.PERMISSION_STORAGE_REQUEST_CODE

object Permission {


    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )


    fun requestPermission(fragment: Fragment) {
        EasyPermissions.requestPermissions(
            fragment,
            "This application cannot work without permission",
            PERMISSION_STORAGE_REQUEST_CODE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


}