package org.jaaksi.coicache.demo.util

import android.widget.Toast
import androidx.annotation.StringRes
import org.jaaksi.coicache.demo.TheApplication

object ToastUtil {
    fun toast(toast: String?) {
        if (!toast.isNullOrEmpty()) {
            Toast.makeText(
                TheApplication.instance,
                toast,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun toast(@StringRes id: Int) {
        toast(TheApplication.instance.getString(id))
    }
}