package net.nyx.printerclient

object Result {
    fun msg(code: Int): String {
        return when (code) {
            SdkResult.SDK_SENT_ERR -> Utils.getApp().getString(R.string.result_sent_err)
            SdkResult.SDK_RECV_ERR -> Utils.getApp().getString(R.string.result_recv_err)
            SdkResult.SDK_TIMEOUT -> Utils.getApp().getString(R.string.result_timeout)
            SdkResult.SDK_PARAM_ERR -> Utils.getApp().getString(R.string.result_params_err)
            SdkResult.SDK_UNKNOWN_ERR -> Utils.getApp().getString(R.string.result_unknown_err)
            SdkResult.SDK_FEATURE_NOT_SUPPORT -> Utils.getApp().getString(R.string.result_feature_not_support)
            SdkResult.DEVICE_NOT_CONNECT -> Utils.getApp().getString(R.string.result_device_not_conn)
            SdkResult.DEVICE_DISCONNECT -> Utils.getApp().getString(R.string.result_device_disconnect)
            SdkResult.DEVICE_CONN_ERR -> Utils.getApp().getString(R.string.result_conn_err)
            SdkResult.DEVICE_CONNECTED -> Utils.getApp().getString(R.string.result_device_connected)
            SdkResult.DEVICE_NOT_SUPPORT -> Utils.getApp().getString(R.string.result_device_not_support)
            SdkResult.DEVICE_NOT_FOUND -> Utils.getApp().getString(R.string.result_device_not_found)
            SdkResult.DEVICE_OPEN_ERR -> Utils.getApp().getString(R.string.result_device_open_err)
            SdkResult.DEVICE_NO_PERMISSION -> Utils.getApp().getString(R.string.result_device_no_permission)
            SdkResult.BT_NOT_OPEN -> Utils.getApp().getString(R.string.result_bt_not_open)
            SdkResult.BT_NO_LOCATION -> Utils.getApp().getString(R.string.result_bt_no_location)
            SdkResult.BT_NO_BONDED_DEVICE -> Utils.getApp().getString(R.string.result_bt_no_bonded)
            SdkResult.BT_SCAN_TIMEOUT -> Utils.getApp().getString(R.string.result_bt_scan_timeout)
            SdkResult.BT_SCAN_ERR -> Utils.getApp().getString(R.string.result_bt_scan_err)
            SdkResult.BT_SCAN_STOP -> Utils.getApp().getString(R.string.result_bt_scan_stop)
            SdkResult.PRN_COVER_OPEN -> Utils.getApp().getString(R.string.result_prn_cover_open)
            SdkResult.PRN_PARAM_ERR -> Utils.getApp().getString(R.string.result_prn_params_err)
            SdkResult.PRN_NO_PAPER -> Utils.getApp().getString(R.string.result_prn_no_paper)
            SdkResult.PRN_OVERHEAT -> Utils.getApp().getString(R.string.result_prn_overheat)
            SdkResult.PRN_UNKNOWN_ERR -> Utils.getApp().getString(R.string.result_prn_unknown_err)
            SdkResult.PRN_PRINTING -> Utils.getApp().getString(R.string.result_prn_printing)
            SdkResult.PRN_NO_NFC -> Utils.getApp().getString(R.string.result_prn_no_nfc)
            SdkResult.PRN_NFC_NO_PAPER -> Utils.getApp().getString(R.string.result_nfc_no_paper)
            SdkResult.PRN_LOW_BATTERY -> Utils.getApp().getString(R.string.result_prn_low_battery)
            SdkResult.PRN_LBL_LOCATE_ERR -> Utils.getApp().getString(R.string.result_prn_locate_err)
            SdkResult.PRN_LBL_DETECT_ERR -> Utils.getApp().getString(R.string.result_prn_detect_err)
            SdkResult.PRN_LBL_NO_DETECT -> Utils.getApp().getString(R.string.result_prn_no_detect)
            SdkResult.PRN_UNKNOWN_CMD, SdkResult.SDK_UNKNOWN_CMD -> Utils.getApp().getString(R.string.result_unknown_cmd)
            else -> code.toString()
        }
    }
} 