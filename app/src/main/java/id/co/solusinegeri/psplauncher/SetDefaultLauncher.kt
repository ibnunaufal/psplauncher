package id.co.solusinegeri.psplauncher

import id.co.solusinegeri.psplauncher.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build.VERSION
import android.text.SpannableString
import android.text.style.TtsSpan.TextBuilder


class SetDefaultLauncher(act: Activity?) {
    val LAUNCHER_CLASS = "id.co.solusinegeri.psplauncher.MainActivity"
    val LAUNCHER_PACKAGE = "id.co.solusinegeri.psplauncher"

    var activity: Activity? = act
//    fun SetDefaultLauncher(activity: Activity?) {
//        this.activity = activity
//    }

    internal enum class HomeState {
        GEL_IS_DEFAULT, OTHER_LAUNCHER_IS_DEFAULT, NO_DEFAULT
    }

    fun launchHomeOrClearDefaultsDialog(): Boolean {
        var intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.HOME")
        val resolveActivity = activity!!.packageManager.resolveActivity(
            intent, 0
        )
        val homeState = if ((LAUNCHER_PACKAGE
                    == resolveActivity!!.activityInfo.applicationInfo.packageName) && (LAUNCHER_CLASS
                    == resolveActivity.activityInfo.name)
        ) HomeState.GEL_IS_DEFAULT else if (resolveActivity == null || resolveActivity.activityInfo == null || !inResolveInfoList(
                resolveActivity, activity!!.packageManager
                    .queryIntentActivities(intent, 0)
            )
        ) HomeState.NO_DEFAULT else HomeState.OTHER_LAUNCHER_IS_DEFAULT
        return when (homeState) {
            HomeState.GEL_IS_DEFAULT, HomeState.NO_DEFAULT -> {
                intent = Intent("android.intent.action.MAIN")
                intent.addCategory("android.intent.category.HOME")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity!!.startActivity(intent)
                true
            }
            else -> {
                showClearDefaultsDialog(resolveActivity)
                false
            }
        }
    }

    @SuppressLint("NewApi", "WrongConstant")
    private fun showClearDefaultsDialog(resolveInfo: ResolveInfo?) {
        val string: CharSequence
        val intent: Intent
        val loadLabel = resolveInfo!!.loadLabel(activity!!.packageManager)
        if (VERSION.SDK_INT < 21
            || activity!!.packageManager.resolveActivity(
                Intent("android.settings.HOME_SETTINGS"), 0
            ) == null
        ) {
            string = "Atur default launcher"
            intent = Intent(
                "android.settings.APPLICATION_DETAILS_SETTINGS",
                Uri.fromParts(
                    "package",
                    resolveInfo.activityInfo.packageName, null
                )
            )
        } else {
            intent = Intent("android.settings.HOME_SETTINGS")
            string = SpannableString(
                "Atur default launcher"
//                activity!!.getString(
//                    R.string.change_default_home_dialog_body_settings, arrayOf<Any>(loadLabel)
//                )
            )
            string
                .setSpan(
                    TextBuilder(
                        "Atur default launcher"
//                        activity!!.getString(
//                            R.string.change_default_home_dialog_body_settings_tts,
//                            loadLabel
//                        )
                    ).build(), 0, string
                        .length, 18
                )
        }
        AlertDialog.Builder(activity)
            .setMessage(string)
            .setNegativeButton(
                "Batal",
                DialogInterface.OnClickListener { dialog, which -> //
                    activity!!.finish()
                })
            .setOnCancelListener(DialogInterface.OnCancelListener { //
                activity!!.finish()
            })
            .setPositiveButton(
                "Atur",
                DialogInterface.OnClickListener { dialog, which -> // TODO Auto-generated method stub
                    try {
                        intent.flags = 276856832
                        activity!!.startActivity(intent)
                    } catch (e: Exception) {
                        setDefLauncher(activity!!)
                    }
                }).create().show()
    }

    private fun inResolveInfoList(
        resolveInfo: ResolveInfo,
        list: List<ResolveInfo>
    ): Boolean {
        for (resolveInfo2 in list) {
            if ((resolveInfo2.activityInfo.name
                        == resolveInfo.activityInfo.name) && (resolveInfo2.activityInfo.packageName
                        == resolveInfo.activityInfo.packageName)
            ) {
                return true
            }
        }
        return false
    }

    private fun setDefLauncher(c: Context) {
        val p: PackageManager = c.packageManager
        val cN = ComponentName(c, MainActivity::class.java)
        p.setComponentEnabledSetting(
            cN,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
        c.startActivity(selector)
        p.setComponentEnabledSetting(
            cN,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}