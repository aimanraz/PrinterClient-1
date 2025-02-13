package net.nyx.printerclient

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*
import kotlin.IllegalArgumentException

object Utils {

    private const val TAG = "Utils"

    private var sApplication: Application? = null

    fun init(app: Application?): Utils {
        if (sApplication == null) {
            sApplication = app ?: getApplicationByReflect()
        } else {
            app?.let { newApp ->
                if (newApp.javaClass != sApplication?.javaClass) {
                    sApplication = newApp
                }
            }
        }
        return this
    }

    fun getApp(): Application {
        return sApplication ?: getApplicationByReflect().also { init(it) }
    }

    @SuppressLint("PrivateApi")
    private fun getApplicationByReflect(): Application {
        try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
            return (app as? Application) ?: throw NullPointerException("u should init first")
        } catch (e: Exception) {
            e.printStackTrace()
            throw NullPointerException("u should init first")
        }
    }

    fun getRandomStr(len: Int): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        return buildString {
            repeat(len) {
                append(str[random.nextInt(62)])
            }
        }
    }

    fun createQRCode(content: String?, width: Int, height: Int): Bitmap? {
        if (content.isNullOrEmpty()) {
            return null
        }

        // Configure parameters
        val hints = mapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "utf-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 0 // default is 4
        )

        return try {
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val finalWidth = width.coerceAtLeast(bitMatrix.width)
            val finalHeight = height.coerceAtLeast(bitMatrix.height)
            val pixels = IntArray(finalWidth * finalHeight) { i ->
                val x = i % finalWidth
                val y = i / finalWidth
                if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
            }
            Bitmap.createBitmap(finalWidth, finalHeight, Config.ARGB_8888).apply {
                setPixels(pixels, 0, finalWidth, 0, 0, finalWidth, finalHeight)
            }
        } catch (e: WriterException) {
            throw IllegalArgumentException("CreateQRCode failed", e)
        }
    }

    fun printTwoColumn(title: String, content: String): ByteArray {
        var iNum = 0
        val buf = ByteArray(100)
        var tmp = title.toByteArray()

        System.arraycopy(tmp, 0, buf, iNum, tmp.size)
        iNum += tmp.size

        tmp = setPosition(384 - content.length * 12)
        System.arraycopy(tmp, 0, buf, iNum, tmp.size)
        iNum += tmp.size

        tmp = content.toByteArray()
        System.arraycopy(tmp, 0, buf, iNum, tmp.size)

        iNum += tmp.size
        buf[iNum] = '\n'.toByte()
        return buf
    }

    private fun setPosition(position: Int): ByteArray {
        return byteArrayOf(0x1B, 0x24, (position % 256).toByte(), (position / 256).toByte())
    }
} 