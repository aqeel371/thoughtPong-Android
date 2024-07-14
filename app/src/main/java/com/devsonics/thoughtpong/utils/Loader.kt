package com.devsonics.thoughtpong.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.devsonics.thoughtpong.dialog.MessageDialogFragment

class Loader(var context: Context) {
    private var progressDialog: ProgressDialog? = null
    var dialogFragment: MessageDialogFragment = MessageDialogFragment()


    private fun initProgressDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Loading...")
    }

    fun showProgress() {
        if (progressDialog == null) initProgressDialog()
        if (!progressDialog!!.isShowing) progressDialog!!.show()
    }

    fun hideProgress() {
        if (progressDialog == null) initProgressDialog()
        if (progressDialog!!.isShowing) progressDialog!!.hide()
    }

    fun showDialogMessage(message: String, manager: FragmentManager) {
        dialogFragment.setMessage(message)
        dialogFragment.show(manager, MessageDialogFragment.DIALOG_TAG)
    }

    fun hideDialogMessage() {
        if (dialogFragment.dialog != null) {
            if (dialogFragment.dialog!!.isShowing()) {
                dialogFragment.dismiss()
            }
        } else {
            dialogFragment.dismiss()
        }
    }
}