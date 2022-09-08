package id.co.solusinegeri.psplauncher.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import id.co.solusinegeri.psplauncher.R
import kotlinx.android.synthetic.main.dialog_date_time.*

class DateTimeDialogFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);
        return inflater.inflate(R.layout.dialog_date_time, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        btn_wifi.setOnClickListener {
            val i: Intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
            startActivity(i)
        }
        btn_date_time.setOnClickListener {
            val i: Intent = Intent(android.provider.Settings.ACTION_DATE_SETTINGS)
            startActivity(i)
        }
    }
}