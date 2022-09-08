package id.co.solusinegeri.psplauncher

import android.R.attr
import android.app.Activity
import android.app.AlertDialog
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import id.co.solusinegeri.psplauncher.networks.APIService
import org.json.JSONObject
import android.R.attr.x
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import id.co.solusinegeri.psplauncher.dialog.DateTimeDialogFragment


class MainActivity : AppCompatActivity() {

    private var isStartOpenDefaultApp: Boolean = false
    private val appName: String = "id.co.solusinegeri.katalisinfostb"
    private var apps: ArrayList<Item>? = null
//    private var list: ListView? = null


    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }
    lateinit var downloadController: DownloadController

    private var list: ArrayList<Menus> = arrayListOf()

    var handler = Handler()
    var apkUrl = "https://github.com/ibnunaufal/stb-launcher/raw/master/psp-launcher.apk"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showAll()
        Log.d("lifecycle", "oncreate")
        // This apk is taking pagination sample app



        btn_test.setOnClickListener {
//            onAlertDialog(mainLayout)
            requestStoragePermission()
        }
        if(isNetworkAvailable(this)){
            checkLatestVersion()
        }

        getScreenSize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            showLauncherSelection()
        }
        startTimer.run()
        if(savedInstanceState == null){
            val temp = getPref()
            Log.d("start",temp.toString())
            if(getPref() == "" || getPref() == "false"){
                startDefaultApp()
            }
        }
//        checkDateTime()

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

    fun setPref(bool: String){
        getPreferences(MODE_PRIVATE).edit().putString("isStartOpenDefaultApp",bool).commit();
    }
    fun getPref():String?{
        val bool: String? = getPreferences(MODE_PRIVATE).getString("isStartOpenDefaultApp","");
        return bool
    }

    fun checkDateTime(){
        if(isNetworkAvailable(this)){
            DateTimeDialogFragment().show(supportFragmentManager, "DateTimeDialogFragment")
        }
    }

    private fun startDefaultApp(){
        val launchIntent = packageManager.getLaunchIntentForPackage("id.co.solusinegeri.katalisinfostb")

//        val intent = Intent(this, packageManager.getLaunchIntentForPackage("id.co.solusinegeri.katalisinfostb"));
//        startActivity(intent)

        if (launchIntent != null) {
            Handler().postDelayed({
//                if(isStartOpenDefaultApp){
                launchIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
//                    isStartOpenDefaultApp = false
//                }
            }, 2000)
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
        Log.d("all", availableActivities.toString())

        if (availableActivities != null) {
            for (x in availableActivities){
                val app = Item()
//                if(x.activityInfo.packageName.toString().substring(0,18) == "id.co.solusinegeri"){
                if(x.activityInfo.packageName.contains("solusinegeri")){
                // add app from TKI only
                app.label = x.activityInfo.packageName
                app.name = x.loadLabel(manager)
                app.icon = x.loadIcon(manager)
                apps!!.add(app)
                }
            }
            rvMenus.layoutManager = LinearLayoutManager(this)
            val menuAdapter = MenuAdapter(list)
            rvMenus.adapter = menuAdapter

            Log.d("qweqwe", apps.toString())
            rvMenus.setHasFixedSize(true)

            list.clear()
            for (x in availableActivities){
                if(x.activityInfo.packageName.contains("solusinegeri") &&
                       ! x.activityInfo.packageName.contains("psplauncher")){
                    Log.d("zxc", x.activityInfo.packageName)
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
        Log.d("lifecycle", "onresume")
//        isStartOpenDefaultApp = false
    }
    var startTimer: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun run() {
//            showAll()

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            } else {
                // Permission request was denied.
                mainLayout.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mainLayout.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            requestPermissionsCompat(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

    fun checkLatestVersion(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.katalis.info/")
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            /*
             * For @Query: You need to replace the following line with val response = service.getEmployees(2)
             * For @Path: You need to replace the following line with val response = service.getEmployee(53)
             */

            // Do the GET request and get response
            val response = service.getVersi()

            withContext(Dispatchers.Main) {

                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val json = JSONObject(response.body()!!.string())

                    Log.d("Pretty Printed JSON :", json.toString())
                    Log.d("Pretty Printed JSON :", json.getString("versionCode").toString())

                    if(BuildConfig.VERSION_NAME != json.getString("versionCode")){
                        btn_test.visibility = View.VISIBLE
                        Log.d("apk url", apkUrl)
                        apkUrl = json.getString("msg").toString()
                        Log.d("apk url", apkUrl)
                        apkUrl = apkUrl.replace('"','[').replace("[","").replace("]","")
                            .replace("\\","")
                        Log.d("apk url", apkUrl)
                        downloadController = DownloadController(this@MainActivity, apkUrl)
//                        checkStoragePermission()
                    }
                } else {
                    Log.e("RETROFIT_ERROR", response.raw().request.url.toString())
                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    // When User cilcks on dialog button, call this method
    fun onAlertDialog(view: View) {
        //Instantiate builder variable
        val builder = AlertDialog.Builder(view.context)

        // set title
        builder.setTitle("Versi baru tersedia")

        //set content area
        builder.setMessage("Unduh versi terbaru untuk performa yang lebih baik")

        //set negative button
        builder.setPositiveButton(
            "Unduh") { dialog, id ->
            // User clicked Update Now button
//            checkLatestVersion()
            checkStoragePermission()
        }

        //set positive button
        builder.setNegativeButton(
            "Nanti") { dialog, id ->
            // User cancelled the dialog
        }

        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("lifecycle", "ondestroy")
        isStartOpenDefaultApp = false
        setPref("false")
        handler.removeCallbacks(startTimer)
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifecycle", "onpause")
        isStartOpenDefaultApp = false
        handler.removeCallbacks(startTimer)
    }

    override fun onStop() {
        super.onStop()
        Log.d("lifecycle", "onpause")
        isStartOpenDefaultApp = false
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


    fun getScreenSize(){
        list = list
        val height: Int = this.resources.displayMetrics.heightPixels
        val width: Int = this.resources.displayMetrics.widthPixels
        Log.d("size","height: $height, widht: $width")
        val bigSize = 30
        val smallSize = 16

        if(width > 1000){
            wifi.textSize = bigSize.toFloat()
            txtclock.textSize = bigSize.toFloat()
        }else{
            wifi.textSize = smallSize.toFloat()
            txtclock.textSize = smallSize.toFloat()
        }
//        var adapter = ArrayAdapter<Item>


    }

    // dialog set default launcher app
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showLauncherSelection() {
        val roleManager = this.getSystemService(Context.ROLE_SERVICE)
                as RoleManager
        if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        ) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
            startActivityForResult(intent,0)
        }
    }
}