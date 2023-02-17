package id.co.solusinegeri.psplauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class UninstalledReceiver: BroadcastReceiver(){
    var mainActivity: MainActivity? = null

    fun setActivityHandler(main: MainActivity?) {
        Log.d("setActivityHandler", "called")
        mainActivity = main
        Log.d("mainActivity", "called $mainActivity")
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("onReceiveMainActivity", "called $mainActivity")
        Log.d("onReceive", context.toString())
        Log.d("ACTION_PACKAGE_FULLY", intent.toString())
        if (Intent.ACTION_PACKAGE_FULLY_REMOVED == intent.action) {
            Log.d("onReceive", context.toString())
            Log.d("ACTION_PACKAGE_FULLY_", intent.toString())
            Log.d("ACTION_PACKAGE_FULL", intent.data.toString())
            Log.d("ACTION_", intent.extras.toString())
            Log.d("onReceiveMainActivity", "called $mainActivity")
            mainActivity?.brCallback(intent)
        }
    }
}
