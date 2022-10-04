package com.sadataljony.app.android.inappupdates.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import com.sadataljony.app.android.inappupdates.R

class ViewExt {
}

fun Context.showSuccessDialog(
    title: String,
    message: String?,
    buttonTitle: String? = "Ok",
    callback: ((DialogInterface) -> Unit?)? = null
) {
    val alertDialog: AlertDialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setIcon(R.drawable.ic_icon_info)
        .setMessage(message ?: "Unknown")
        .setPositiveButton(
            buttonTitle
        ) { dialog: DialogInterface, _: Int ->
            callback?.let {
                callback.invoke(dialog)
            }
            dialog.dismiss()
        }
        .create()
    alertDialog.setOnShowListener {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(Color.parseColor("#15ac42"))
    }
    alertDialog.setCancelable(false)
    alertDialog.show()
}






