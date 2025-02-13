package net.nyx.printerclient

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import net.nyx.printerservice.print.IPrinterService
import net.nyx.printerservice.print.PrintTextFormat
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SCAN = 0x99
        var PRN_TEXT: String = ""
    }

    private lateinit var btnVer: Button
    private lateinit var btnPaper: Button
    private lateinit var btnPrint: Button
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btnLbl: Button
    private lateinit var btnLblLearning: Button
    private lateinit var btnLcdBmp: Button
    private lateinit var btnLcdReset: Button
    private lateinit var btnLcdWakeup: Button
    private lateinit var btnLcdSleep: Button
    private lateinit var btnCashBox: Button
    private lateinit var btnInfraredScan: Button
    private lateinit var btnCameraScan: Button
    private lateinit var tvLog: TextView

    private val singleThreadExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val version = arrayOf<String?>(null)

    private var printerService: IPrinterService? = null

    private val connService = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            showLog("printer service disconnected, try reconnect")
            printerService = null
            // Try to rebind
            handler.postDelayed({ bindService() }, 5000)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("onServiceConnected: %s", name)
            printerService = IPrinterService.Stub.asInterface(service)
            getVersion()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        bindService()
        registerQscScanReceiver()
        Timber.plant(Timber.DebugTree())
        PRN_TEXT = getString(R.string.print_text)
    }

    private fun bindService() {
        val intent = Intent().apply {
            setPackage("net.nyx.printerservice")
            action = "net.nyx.printerservice.IPrinterService"
        }
        bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        unbindService(connService)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_ver -> getVersion()
            R.id.btn_paper -> paperOut()
            R.id.btn_print -> printTest()
            R.id.btn1 -> printText()
            R.id.btn2 -> printBarcode()
            R.id.btn3 -> printQrCode()
            R.id.btn4 -> printBitmap()
            R.id.btn5 -> printTable()
            R.id.btn6 -> printEscpos()
            R.id.btn_infrared_scan -> infraredScan()
            R.id.btn_camera_scan -> cameraScan()
            R.id.btn_lbl -> printLabel()
            R.id.btn_lbl_learning -> printLabelLearning()
            R.id.btn_lcd_bmp -> showLcdBitmap()
            R.id.btn_lcd_reset -> configLcd(4)
            R.id.btn_lcd_wakeup -> configLcd(1)
            R.id.btn_lcd_sleep -> configLcd(2)
            R.id.btn_cash_box -> openCashBox()
        }
    }

    private fun getVersion() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.getPrinterVersion(version) ?: return@submit
                showLog("Version: ${Result.msg(ret)} ${version[0]}")
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun paperOut() {
        singleThreadExecutor.submit {
            try {
                printerService?.paperOut(80)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun printTest() {
        singleThreadExecutor.submit {
            try {
                val textFormat = PrintTextFormat()
                var ret = printerService?.printText(PRN_TEXT, textFormat) ?: return@submit
                ret = printerService?.printBarcode("123456789", 300, 160, 1, 1) ?: return@submit
                ret = printerService?.printQrCode("123456789", 300, 300, 1) ?: return@submit
                ret = printerService?.printBitmap(BitmapFactory.decodeStream(assets.open("bmp.png")), 1, 1) ?: return@submit
                showLog("Print test: ${Result.msg(ret)}")
                if (ret == 0) {
                    printerService?.printEndAutoOut()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printText() {
        printText(PRN_TEXT)
    }

    private fun printText(text: String) {
        singleThreadExecutor.submit {
            try {
                val textFormat = PrintTextFormat()
                // textFormat.textSize = 32
                // textFormat.isUnderline = true
                val ret = printerService?.printText(text, textFormat) ?: return@submit
                showLog("Print text: ${Result.msg(ret)}")
                if (ret == 0) {
                    printerService?.printEndAutoOut()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun printBarcode() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.printBarcode("123456789", 300, 160, 1, 1) ?: return@submit
                showLog("Print text: ${Result.msg(ret)}")
                if (ret == 0) {
                    printerService?.printEndAutoOut()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun printQrCode() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.printQrCode("123456789", 300, 300, 1) ?: return@submit
                showLog("Print barcode: ${Result.msg(ret)}")
                if (ret == 0) {
                    printerService?.printEndAutoOut()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun printBitmap() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.printBitmap(BitmapFactory.decodeStream(assets.open("bmp.png")), 1, 1) ?: return@submit
                showLog("Print bitmap: ${Result.msg(ret)}")
                if (ret == 0) {
                    printerService?.printEndAutoOut()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printTable() {
        singleThreadExecutor.submit {
            try {
                var ret: Int
                val formatCenter = PrintTextFormat().apply { ali = 1 }
                val formatLeft = PrintTextFormat().apply { ali = 0 }
                val formats = arrayOf(formatCenter, formatCenter, formatCenter, formatCenter)
                val formats2 = arrayOf(formatLeft, formatCenter, formatCenter, formatCenter)
                val weights = intArrayOf(2, 1, 1, 1)
                val row1 = arrayOf("ITEM", "QTY", "PRICE", "TOTAL")
                val row2 = arrayOf("Apple", "1", "2.00", "2.00")
                val row3 = arrayOf("Strawberry", "1", "2.00", "2.00")
                val row4 = arrayOf("Watermelon", "1", "2.00", "2.00")
                val row5 = arrayOf("Orange", "1", "2.00", "2.00")
                ret = printerService?.printTableText(row1, weights, formats) ?: return@submit
                ret = printerService?.printTableText(row2, weights, formats2) ?: return@submit
                ret = printerService?.printTableText(row3, weights, formats2) ?: return@submit
                ret = printerService?.printTableText(row4, weights, formats2) ?: return@submit
                ret = printerService?.printTableText(row5, weights, formats2) ?: return@submit
                showLog("Print table: ${Result.msg(ret)}")
                if (ret == 0) {
                    printerService?.printEndAutoOut()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printEscpos() {
        singleThreadExecutor.submit {
            try {
                printerService?.apply {
                    printEscposData(byteArrayOf(0x1b, 0x40))
                    printEscposData(byteArrayOf(0x1b, 0x61, 0x01, 0x1b, 0x21, 48))
                    printEscposData("Receipt\n".toByteArray())
                    printEscposData(byteArrayOf(0x1b, 0x61, 0x00, 0x1b, 0x21, 0x00))
                    printEscposData("\n".toByteArray())
                    printEscposData(Utils.printTwoColumn("Order:", System.currentTimeMillis().toString()))
                    printEscposData(Utils.printTwoColumn("Time:", "2024-12-12 12:12:12"))
                    printEscposData("--------------------------------\n".toByteArray())
                    printEscposData(Utils.printTwoColumn("phone", "4999.00"))
                    printEscposData(Utils.printTwoColumn("laptop", "4999.00"))
                    printEscposData("--------------------------------\n".toByteArray())
                    printEscposData(Utils.printTwoColumn("Total:", "9998.00"))
                    printEscposData(Utils.printTwoColumn("Cash:", "10000.00"))
                    printEscposData(Utils.printTwoColumn("Change:", "22.00"))
                    printEscposData(byteArrayOf(0x1d, 0x56, 0x42, 0x00))
                }
                showLog("Print ESC/POS cmd: ${Result.msg(0)}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printLabel() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.labelLocate(240, 16) ?: return@submit
                if (ret == 0) {
                    val format = PrintTextFormat()
                    printerService?.apply {
                        printText("\nModel:\t\tNB55", format)
                        printBarcode("1234567890987654321", 320, 90, 2, 0)
                        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
                        printText("Time:\t\t$date", format)
                        labelPrintEnd()
                    }
                }
                showLog("Print label: ${Result.msg(ret)}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printLabelLearning() {
        if (version[0]?.let { it.toFloat() < 1.10 } == true) {
            showLog(getString(R.string.res_not_support))
            return
        }
        singleThreadExecutor.submit {
            var ret = 0
            try {
                printerService?.let { service ->
                    if (!service.hasLabelLearning()) {
                        // label learning
                        ret = service.labelDetectAuto()
                    }
                    if (ret == 0) {
                        ret = service.labelLocateAuto()
                        if (ret == 0) {
                            val format = PrintTextFormat()
                            service.printText("\nModel:\t\tNB55", format)
                            service.printBarcode("1234567890987654321", 320, 90, 2, 0)
                            val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
                            service.printText("Time:\t\t$date", format)
                            service.labelPrintEnd()
                        }
                    }
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            showLog("Label learning print: ${Result.msg(ret)}")
        }
    }

    private fun setLcdLogo() {
        singleThreadExecutor.submit {
            val content = Utils.getRandomStr(100)
            val bitmap = Utils.createQRCode(content, 220, 220)
            try {
                // init
                printerService?.let { service ->
                    var ret = service.configLcd(0)
                    if (ret == 0) {
                        ret = service.setLcdLogo(bitmap)
                    }
                    showLog("Show LCD default: ${Result.msg(ret)}")
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun showLcdBitmap() {
        showDialog()
        singleThreadExecutor.submit {
            try {
                // init
                printerService?.let { service ->
                    var ret = service.configLcd(0)
                    if (ret == 0) {
                        val bitmap = BitmapFactory.decodeStream(assets.open("bmp.png"))
                        ret = service.showLcdBitmap(bitmap)
                    }
                    showLog("Show LCD bitmap: ${Result.msg(ret)}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            hideDialog()
        }
    }

    private fun configLcd(opt: Int) {
        singleThreadExecutor.submit {
            try {
                // init
                printerService?.let { service ->
                    var ret = service.configLcd(0)
                    if (ret == 0) {
                        ret = service.configLcd(opt)
                    }
                    showLog("LCD config: ${Result.msg(ret)}")
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun openCashBox() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.openCashBox() ?: return@submit
                showLog("Open cash box: ${Result.msg(ret)}")
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private val qscReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ("com.android.NYX_QSC_DATA" == intent.action) {
                val qsc = intent.getStringExtra("qsc")
                showLog("qsc scan result: %s", qsc)
                printText("qsc-quick-scan-code\n$qsc")
            }
        }
    }

    private fun registerQscScanReceiver() {
        val filter = IntentFilter().apply {
            addAction("com.android.NYX_QSC_DATA")
        }
        registerReceiver(qscReceiver, filter)
    }

    private fun unregisterQscReceiver() {
        unregisterReceiver(qscReceiver)
    }

    private fun infraredScan() {
        singleThreadExecutor.submit {
            try {
                val ret = printerService?.triggerQscScan() ?: return@submit
                showLog("Infrared scan: ${Result.msg(ret)}")
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun cameraScan() {
        // need permission "android.permission.QUERY_ALL_PACKAGES" for android 11 package visible
        if (!existApp("net.nyx.scanner")) {
            showLog("Scanner app is not installed")
            return
        }
        val intent = Intent().apply {
            component = ComponentName("net.nyx.scanner", "net.nyx.scanner.ScannerActivity")
            // set the capture activity actionbar title
            putExtra("TITLE", "Scan")
            // show album icon, default true
            // putExtra("SHOW_ALBUM", true)
            // play beep sound when get the scan result, default true
            // putExtra("PLAY_SOUND", true)
            // play vibrate when get the scan result, default true
            // putExtra("PLAY_VIBRATE", true)
        }
        startActivityForResult(intent, RC_SCAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SCAN && resultCode == RESULT_OK && data != null) {
            val result = data.getStringExtra("SCAN_RESULT")
            showLog("Scanner result: $result")
        }
    }

    private fun existApp(pkg: String): Boolean {
        return try {
            packageManager.getPackageInfo(pkg, 0) != null
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService()
        unregisterQscReceiver()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_clear) {
            clearLog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        btnVer = findViewById<Button>(R.id.btn_ver).apply {
            setOnClickListener(this@MainActivity)
        }
        btnPaper = findViewById<Button>(R.id.btn_paper).apply {
            setOnClickListener(this@MainActivity)
        }
        btnPrint = findViewById<Button>(R.id.btn_print).apply {
            setOnClickListener(this@MainActivity)
        }
        btn1 = findViewById<Button>(R.id.btn1).apply {
            setOnClickListener(this@MainActivity)
        }
        btn2 = findViewById<Button>(R.id.btn2).apply {
            setOnClickListener(this@MainActivity)
        }
        btn3 = findViewById<Button>(R.id.btn3).apply {
            setOnClickListener(this@MainActivity)
        }
        btn4 = findViewById<Button>(R.id.btn4).apply {
            setOnClickListener(this@MainActivity)
        }
        btn5 = findViewById<Button>(R.id.btn5).apply {
            setOnClickListener(this@MainActivity)
        }
        btn6 = findViewById<Button>(R.id.btn6).apply {
            setOnClickListener(this@MainActivity)
        }
        btnInfraredScan = findViewById<Button>(R.id.btn_infrared_scan).apply {
            setOnClickListener(this@MainActivity)
        }
        btnCameraScan = findViewById<Button>(R.id.btn_camera_scan).apply {
            setOnClickListener(this@MainActivity)
        }
        tvLog = findViewById(R.id.tv_log)
        btnLbl = findViewById<Button>(R.id.btn_lbl).apply {
            setOnClickListener(this@MainActivity)
        }
        btnLblLearning = findViewById<Button>(R.id.btn_lbl_learning).apply {
            setOnClickListener(this@MainActivity)
        }
        btnLcdBmp = findViewById<Button>(R.id.btn_lcd_bmp).apply {
            setOnClickListener(this@MainActivity)
        }
        btnLcdReset = findViewById<Button>(R.id.btn_lcd_reset).apply {
            setOnClickListener(this@MainActivity)
        }
        btnLcdWakeup = findViewById<Button>(R.id.btn_lcd_wakeup).apply {
            setOnClickListener(this@MainActivity)
        }
        btnLcdSleep = findViewById<Button>(R.id.btn_lcd_sleep).apply {
            setOnClickListener(this@MainActivity)
        }
        btnCashBox = findViewById<Button>(R.id.btn_cash_box).apply {
            setOnClickListener(this@MainActivity)
        }
    }

    private fun showLog(log: String, vararg args: Any?) {
        val finalLog = if (args.isNotEmpty()) String.format(log, *args) else log
        Log.e(TAG, finalLog)
        val dateFormat = SimpleDateFormat("MM-dd HH:mm:ss")
        runOnUiThread {
            if (tvLog.lineCount > 100) {
                tvLog.text = ""
            }
            tvLog.append("${dateFormat.format(Date())}:$finalLog\n")
            tvLog.post {
                (tvLog.parent as ScrollView).fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    private fun clearLog() {
        tvLog.text = ""
    }
} 