package id.co.solusinegeri.psplauncher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val appName: String = "id.co.solusinegeri.katalisinfostb"
    private var apps: ArrayList<Item>? = null
//    private var list: ListView? = null


    private var list: ArrayList<Menus> = arrayListOf()

    var handler = Handler()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showAll()

//        list_info.setOnClickListener {
////            val i: Intent? = packageManager.getLaunchIntentForPackage("id.co.solusinegeri.katalisinfostb")
////            startActivity(i)
//            val launchIntent =
//                packageManager.getLaunchIntentForPackage(appName)
//            if (launchIntent != null) {
//                startActivity(launchIntent)
//            } else {
//                Toast.makeText(
//                    this@MainActivity,
//                    "There is no package available in android",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//        list_info.setOnFocusChangeListener { v, hasFocus ->
//            if(hasFocus){
//                Log.d("hello","info active")
//                list_info.background = this.getDrawable(R.drawable.active)
//                list_info.setBackgroundColor(Color.parseColor("#ffffff"));
//            }else{
//                Log.d("hello","info inactive")
//                list_info.background = this.getDrawable(R.drawable.inactivea)
//            }
//        }
//        list_setting.setOnFocusChangeListener { v, hasFocus ->
//            if(hasFocus){
//                Log.d("hello","setting active")
//                list_setting.background = this.getDrawable(R.drawable.active)
//            }else{
//                Log.d("hello","setting inactive")
//                list_setting.background = this.getDrawable(R.drawable.inactivea)
//            }
//        }
//        list_setting.setOnClickListener {
//            val intent = Intent(Settings.ACTION_SETTINGS)
//            startActivity(intent)
//        }


//
//
//        list_view.onItemClickListener = object : AdapterView.OnItemClickListener {
//            override fun onItemClick(parent: AdapterView<*>, view: View,
//                                     position: Int, id: Long) {
//                // value of item that is clicked
//                val itemValue = list_view.getItemAtPosition(position) as String
//                // Toast the values
//                Log.d("click", apps!![0].label.toString())
//                Toast.makeText(applicationContext,
//                    "Position :$position\nItem Value : $itemValue", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }

//        if(isAppInstalled(appName, this)){
//            Log.d("available", "true")
//            list_info.visibility = View.VISIBLE
//            val icon: Drawable = this.packageManager.getApplicationIcon(appName)
//            val name: String = this.packageManager.getApplicationInfo(appName,0).loadLabel(packageManager).toString()
//
//            ic_info.setImageDrawable(icon)
//
//            tv_info.text = name
//
//        }
//        else{
//            Log.d("available", "false")
//
//            ic_info.background = this.getDrawable(R.drawable.ic_launcher_foreground)
//
//            tv_info.text = "Dummy"
//        }
    }

    private fun startDefaultApp(){
        val launchIntent = packageManager.getLaunchIntentForPackage("id.co.solusinegeri.katalisinfostb")
        if (launchIntent != null) {
//            coba.text = "Ada, Mulai buka"
            Handler().postDelayed({
                //doSomethingHere()
                startActivity(launchIntent)
            }, 2000)

        }else{
//            coba.text = "Ga Ada"
        }
    }
    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            println("Back button long pressed")
            startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showAll(){
        apps = ArrayList()

        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)

        val manager = packageManager
        val availableActivities = manager?.queryIntentActivities(i, 0)

        if (availableActivities != null) {
            for (x in availableActivities){
                val app = Item()
//                if(x.activityInfo.packageName.toString().substring(0,18) == "id.co.solusinegeri"){
                // add app from TKI only
                app.label = x.activityInfo.packageName
                app.name = x.loadLabel(manager)
                app.icon = x.loadIcon(manager)
                apps!!.add(app)
//                }
            }
            rvMenus.layoutManager = LinearLayoutManager(this)
            val menuAdapter = MenuAdapter(list)
            rvMenus.adapter = menuAdapter

            Log.d("qweqwe", apps.toString())
            rvMenus.setHasFixedSize(true)

            list.clear()
            for (x in availableActivities){
                Log.d("zxc", x.activityInfo.packageName)
                if(x.activityInfo.packageName.contains("solusinegeri") &&
                        x.activityInfo.packageName.contains("katalisinfostb")){
                    list.add(Menus(x.activityInfo.packageName, x.loadLabel(manager).toString(), x.loadIcon(manager)))
                }
                if(x.activityInfo.packageName.contains("vending")){
                    list.add(Menus(x.activityInfo.packageName, x.loadLabel(manager).toString(), x.loadIcon(manager)))
                }
//                else{
//                    if(x.activityInfo.packageName.contains("android.setting") ||
//                        x.activityInfo.packageName.contains("tv.settings")){
//                        list.add(Menus(x.activityInfo.packageName, x.loadLabel(manager).toString(), x.loadIcon(manager)))
//                    }
//                }
                menuAdapter.notifyDataSetChanged()
            }

            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // In landscape
//                rvMenus.layoutManager = GridLayoutManager(this, 5)
                if (list.size < 5){
                    rvMenus.layoutManager = GridLayoutManager(this, list.size)
                }else{
                    rvMenus.layoutManager = GridLayoutManager(this, 5)
                }
                main_layout.background = this.getDrawable(R.drawable.main_bg_landscape)
                second_layout.background = this.getDrawable(R.drawable.menu_bg_landscape)
            } else {
                // In portrait
                rvMenus.layoutManager = GridLayoutManager(this, 2)
                main_layout.background = this.getDrawable(R.drawable.main_bg_potrait)
                second_layout.background = this.getDrawable(R.drawable.menu_bg_potrait)
            }
            Log.d("asdasd", list.toString())
            showRecycler()
//            startDefaultApp()

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        showAll()
    }
    var startTimer: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun run() {
            showAll()

            if (isNetworkAvailable(this@MainActivity)){
                wifi.text = "Terhubung"
                wifi.setTextColor(Color.parseColor("#00ff00"))
            }else{
                wifi.text = "Tidak Ada Jaringan"
                wifi.setTextColor(Color.parseColor("#ff0000"))
            }
            handler.postDelayed(this, 1000)//1 sec delay
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(startTimer)
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun isAppInstalled(appName: String, context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo(appName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onBackPressed() {
        // do nothing
    }

    fun loadApps(){

    }
    private fun showRecycler() {
    }


    fun loadList(){
        list = list

//        var adapter = ArrayAdapter<Item>


    }
}