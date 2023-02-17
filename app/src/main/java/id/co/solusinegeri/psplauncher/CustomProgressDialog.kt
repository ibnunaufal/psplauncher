package id.co.solusinegeri.psplauncher

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import kotlinx.android.synthetic.main.custom_preloaded.view.*


class CustomProgressDialog {
    lateinit var dialog: CustomDialog

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.custom_preloaded, null)
        if (title != null) {
            view.cp_title.text = title
        }

        // Card Color
        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000"))



        // Text Color
        view.cp_title.setTextColor(Color.WHITE)
        if (title != null) {
            view.cp_title.textSize = 22f
            view.cp_title.text = "Mengunduh"
        }

        dialog = CustomDialog(context)
        dialog.setContentView(view)
        dialog.show()
        return dialog
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.dialogBackground)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                    insets.consumeSystemWindowInsets()
                }
            }
        }
    }
}