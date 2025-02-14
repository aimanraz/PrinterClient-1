package net.nyx.printerclient

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.app.AlertDialog

abstract class BaseActivity : AppCompatActivity() {

    protected val handler = Handler(Looper.getMainLooper())
    private var mDialog: AlertDialog? = null

    fun showDialog() {
        if (isFinishing || isDestroyed) {
            return
        }

        handler.post {
            if (isFinishing || isDestroyed) {
                return@post
            }

            if (mDialog == null) {
                mDialog = MaterialAlertDialogBuilder(this)
                    .setMessage("loading...")
                    .setCancelable(false)
                    .create()
            }
            mDialog?.takeIf { !it.isShowing }?.show()
        }
    }

    fun hideDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        mDialog?.takeIf { it.isShowing }?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDialog?.takeIf { it.isShowing }?.let {
            hideDialog()
        }
        mDialog = null
    }
} 