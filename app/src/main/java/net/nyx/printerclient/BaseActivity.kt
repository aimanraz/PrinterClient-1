package net.nyx.printerclient

import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    protected val handler = Handler(Looper.getMainLooper())
    private var mDialog: ProgressDialog? = null

    fun showDialog() {
        if (isFinishing || isDestroyed) {
            return
        }

        handler.post {
            if (isFinishing || isDestroyed) {
                return@post
            }

            if (mDialog == null) {
                mDialog = ProgressDialog(this).apply {
                    setMessage("loading...")
                    isIndeterminate = false
                    setCancelable(false)
                }
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