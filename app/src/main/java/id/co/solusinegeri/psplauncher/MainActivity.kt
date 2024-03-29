package id.co.solusinegeri.psplauncher

import android.app.AlertDialog
import android.app.UiModeManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import id.co.solusinegeri.psplauncher.dialog.DateTimeDialogFragment
import id.co.solusinegeri.psplauncher.networks.APIService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Retrofit
import java.util.*


class MainActivity : AppCompatActivity() {

    private var isStartOpenDefaultApp: Boolean = false
    private val appName: String = "id.co.solusinegeri.katalisinfostb"
    private var apps: ArrayList<Item>? = null
//    private var list: ListView? = null


    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }
    lateinit var downloadController: DownloadControllerPlaystore

    private var list: ArrayList<Menus> = arrayListOf()

    var handler = Handler()
    var apkUrl = "https://github.com/ibnunaufal/stb-launcher/raw/master/psp-launcher.apk"
    var inputtedApkUrl = ""
    var wifiJob: Job? = null

//    lateinit var receiver : UninstalledReceiver

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showAll()

        txt_version.text = "versi ${BuildConfig.VERSION_NAME}"
//        receiver = UninstalledReceiver()
//        receiver.setActivityHandler(this)
//        receiver.mainActivity = this
//        IntentFilter(Intent.ACTION_PACKAGE_REMOVED).also {
//            registerReceiver(receiver, it)
//        }
        Log.d("lifecycle", "oncreate")
        // This apk is taking pagination sample app



        btn_test.setOnClickListener {
//            onAlertDialog(mainLayout)
            inputtedApkUrl = apkUrl
//            requestStoragePermission()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }

        btn_test.setOnFocusChangeListener { _, b ->
            if (b){
                btn_test.background = resources.getDrawable(R.drawable.btn_active)
//                btn_test.setTextColor(resources.getColor(R.color.white))
            } else {
                btn_test.background = resources.getDrawable(R.drawable.btn_outline)
//                btn_test.setTextColor(resources.getColor(R.color.black))
            }
        }

        getScreenSize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            showLauncherSelection()
        }
        if(savedInstanceState == null){
            val temp = getPref()
            Log.d("start",temp.toString())
            if(getPref() == "" || getPref() == "false"){
                startDefaultApp()
            }
        }
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
        print("longPress $keyCode");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            println("Back button long pressed")
            onLongBackPressed()
//            var ada = false
//            list.forEach {
//                if (it.label.contains("katalisinfostb")){
//                    ada = true
//                }
//            }
//            if (!ada) {
//                onLongBackPressed()
//            } else {
//                startActivityForResult(Intent(android.provider.Settings.ACTION_SETTINGS), 0);
//            }
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }


    fun onLongBackPressed(){
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        builder.setTitle("Aksi")

        builder.setNegativeButton("Batal"){
                _, _ ->
        }

        val temp: MutableList<String> = mutableListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val roleManager = this.getSystemService(Context.ROLE_SERVICE)
//                    as RoleManager
//            if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
//                !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
//            ){
            val packageManager = this.packageManager
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }

            val resolveInfo = packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val defaultLauncherPackageName = resolveInfo?.activityInfo?.packageName

            if (defaultLauncherPackageName != null && defaultLauncherPackageName != packageName) {
                temp.add("Atur PSP Launcher sebagai default")
            }
        }

        var ada = false
        list.forEach {
            if (it.label.contains("katalisinfostb")){
                ada = true
            }
        }
        if (!ada) {
            if (checkIsTelevision()){
                temp.add("Install Absensi")
            }
        }
        temp.add("Atur Wifi")
        temp.add("Atur Waktu dan Tanggal")
        temp.add("Atur Tampilan (Zoom)")
        temp.add("Buka Pengaturan Lainnya")

        val devices = temp.toTypedArray()
        builder.setItems(
            devices
        ) { _, which ->
            if (temp[which].contains("Install Absensi")) {
                confirmDownload()
            } else if(temp[which].contains("Wifi")) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } else if(temp[which].contains("Waktu")) {
                startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
            } else if(temp[which].contains("Tampilan")) {
                startActivityForResult(Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS), 0);
//                startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
            } else if(temp[which].contains("Launcher")) {
                showLauncherSelection()
            } else {
                startActivity(Intent(Settings.ACTION_SETTINGS));
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun confirmDownload(){
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        builder.setTitle("Konfirmasi")
        builder.setMessage("Anda yakin akan mengunduh Absensi")

        builder.setNegativeButton("Batal"){
                _, _ -> onLongBackPressed()
        }

        builder.setPositiveButton("Unduh"){
                _, _ ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("http://play.google.com/store/apps/details?id=id.co.solusinegeri.katalisinfostb")
                setPackage("com.android.vending")
            }
            startActivity(intent)
//            downloadController = DownloadControllerPlaystore(this@MainActivity)
//            inputtedApkUrl = "https://raw.githubusercontent.com/ibnunaufal/stb-launcher/master/Absensi/Latest/app-debug.apk"
//            checkStoragePermission()

        }


        val dialog = builder.create()
        dialog.show()
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
            rvMenus.findViewHolderForAdapterPosition(0)?.itemView?.requestFocus()
//            rvMenus.viewTreeObserver.addOnGlobalLayoutListener {
//                rvMenus.layoutManager?.scrollToPosition(0)
//
//            }
            showRecycler()
//            startDefaultApp()

        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        showAll()
//        startTimer.run()
        startWifiCoroutine()
        if(isNetworkAvailable(this)){
            checkLatestVersion()
        }
//        receiver = UninstalledReceiver()
//        receiver.setActivityHandler(this)
//        receiver.mainActivity = this
//        IntentFilter(Intent.ACTION_PACKAGE_FULLY_REMOVED).also {
//            registerReceiver(receiver, it)
//        }
        Log.d("lifecycle", "onresume")
//        isStartOpenDefaultApp = false
    }
//    var startTimer: Runnable = object : Runnable {
//        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//        override fun run() {
////            showAll()
//
//            if (isNetworkAvailable(this@MainActivity)){
//                wifi.text = "Terhubung"
//                wifi.setTextColor(Color.parseColor("#00ff00"))
//            }else{
//                wifi.text = "Tidak Ada Jaringan"
//                wifi.setTextColor(Color.parseColor("#ff0000"))
//            }
//            handler.postDelayed(this, 1000)//1 sec delay
//        }
//    }
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun startWifiCoroutine() {
        // Ensure that there's no existing coroutine running
        stopWifiCoroutine()
        // Start the new coroutine
        wifiJob = coroutineScope.launch {
            updateWifiText()
        }
    }

    fun stopWifiCoroutine() {
        // Cancel the job if it's not null
        wifiJob?.cancel()
        wifiJob = null
    }

    suspend fun updateWifiText() {
        while (true) {
            // Update the wifi.text value
            if (isNetworkAvailable(this@MainActivity)){
                wifi.text = "Terhubung"
                wifi.setTextColor(Color.parseColor("#00ff00"))
            }else{
                wifi.text = "Tidak Ada Jaringan"
                wifi.setTextColor(Color.parseColor("#ff0000"))
            }
            // Delay for 5 seconds
            delay(3000)
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
                downloadController.enqueueDownload(inputtedApkUrl)
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
            downloadController.enqueueDownload(inputtedApkUrl)
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }

    fun brCallback(intent: Intent) {
//        Log.d("BroadcastReceiver", intent.toString())
//        Log.d("BroadcastReceiver", intent.data.toString())
//        val url = "https://raw.githubusercontent.com/ibnunaufal/stb-launcher/master/Absensi/Latest/app-debug.apk"
//        inputtedApkUrl = url
//        requestStoragePermission()
    }

    fun confirmDelete(packageName: String){
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        var name = ""
        list.forEach {
            if (packageName == it.label){
                name = it.name
            }
        }
        builder.setTitle("Anda yakin akan melakukan menghapus $name?")
        builder.setPositiveButton("Ya"){
                _,_ -> doRemove(packageName)
        }
        builder.setNegativeButton("Batal"){
                _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun doRemove(packageName: String){
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
        showAll()
    }

    fun openAppDetail(app: String){
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        builder.setTitle("Opsi")

        builder.setNegativeButton("Batal"){
                _, _ ->
        }

        val temp: MutableList<String> = mutableListOf()

        if (app.contains("solusinegeri")){
            temp.add("Hapus Aplikasi")
        }
        temp.add("Buka Detail Aplikasi")

        val devices = temp.toTypedArray()
        builder.setItems(
            devices
        ) { _, which ->
            if (temp[which] == "Hapus Aplikasi") {
                confirmDelete(app)
            } else {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$app")))
            }
        }

        val dialog = builder.create()
        dialog.show()
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

    fun addOtherApk(){
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
        builder.setTitle("Tambahkan Aplikasi Lain")
        builder.setMessage(
            Html.fromHtml("" +
                "Mohon masukkan url download file:<br>"
        ))

        val inputEditTextField = EditText(this)
        inputEditTextField.setTextColor(resources.getColor(R.color.white))
        builder.setView(inputEditTextField)

        builder.setPositiveButton("Download") { dialog, which ->
            var url = inputEditTextField.text.toString()
            Log.d("alerturl", url.toString())
            if (!url.contains("http") || !url.contains("https")){
                url = "https://$url"
            }
            inputtedApkUrl = url
//            requestStoragePermission()
            checkStoragePermission()
        }

        builder.setNegativeButton("Batal") { dialog, which ->

        }

        builder.show()
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
                    Log.d("Pretty Printed JSON :", json.getString("version").toString())

                    if(BuildConfig.VERSION_NAME != json.getString("version")){
                        btn_test.visibility = View.VISIBLE
                        Log.d("apk url", apkUrl)
//                        apkUrl = json.getString("msg").toString()
//                        Log.d("apk url", apkUrl)
//                        apkUrl = apkUrl.replace('"','[').replace("[","").replace("]","")
//                            .replace("\\","")
//                        Log.d("apk url", apkUrl)
//                        downloadController = DownloadControllerPlaystore(this@MainActivity)
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
        stopWifiCoroutine()
//        handler.removeCallbacks(startTimer)
//        unregisterReceiver(receiver)
    }

    override fun onPause() {
        super.onPause()
        Log.d("lifecycle", "onpause")
        isStartOpenDefaultApp = false
        stopWifiCoroutine()
//        handler.removeCallbacks(startTimer)
    }

    override fun onStop() {
        super.onStop()
        Log.d("lifecycle", "onStop")
        isStartOpenDefaultApp = false
//        handler.removeCallbacks(startTimer)
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

    private fun checkIsTelevision(): Boolean {
        val uiMode: Int = resources.configuration.uiMode
        return uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
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
        val june12 = Calendar.getInstance()
        june12.set(2023, Calendar.JUNE, 12, 0, 0, 0)
        val currentDate = Calendar.getInstance()

        if (currentDate.before(june12)){
            super.onBackPressed()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val packageManager = this.packageManager
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }

            val resolveInfo = packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
            val defaultLauncherPackageName = resolveInfo?.activityInfo?.packageName

            if (defaultLauncherPackageName != null && defaultLauncherPackageName != packageName) {
                // PSP Launcher is not set as the default launcher
                val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                builder.setTitle("Peringatan")
                builder.setMessage("PSP Launcher belum diatur menjadi Launcher default")
                builder.setPositiveButton("Atur Default") { _, _ ->
                    showLauncherSelection()
                }
                builder.setNegativeButton("Nanti") { _, _ ->
//                    super.onBackPressed()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
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

    private fun isAndroidTV(): Boolean {
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }
    // dialog set default launcher app
//    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showLauncherSelection() {
        Log.d("showLauncherSelection", "called")
        val settingsIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (settingsIntent.resolveActivity(packageManager) != null) {
                Log.d("showLauncherSelection", "${isAndroidTV()}")
                if (isAndroidTV()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val roleManager = this.getSystemService(Context.ROLE_SERVICE)
                                as RoleManager
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                        startActivityForResult(intent,0)
                        return
                    }
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    Log.d("intent", settingsIntent.toString())
                    startActivity(settingsIntent)
                } catch (e: SendIntentException){
                    Log.e("error", e.toString())
                }
            } else {
                // Handle the case when the settings activity is not available
                // or the device does not support managing default apps
            }
        } else {
            // Handle the case when the device's Android version is below O
        }

//        val settingsIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
//        if (settingsIntent.resolveActivity(packageManager) != null) {
//            Log.d("showLauncherSelection", "null")
//            startActivity(settingsIntent)
//        } else {
//            Log.d("showLauncherSelection", "null")
//            // Handle the case when the settings activity is not available
//            // or the device does not support managing default apps
//        }
//        startActivityForResult(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS), 0);
//        this.startactivity
//        if (requestCode !== -1) act.startActivityForResult(
//            intent,
//            requestCode
//        ) else act.startActivity(intent)
//        val roleManager = this.getSystemService(Context.ROLE_SERVICE)
//                as RoleManager
//        if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
//            !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
//        ) {
//            Log.d("showLauncherSelection", "not set as default")
////            SetDefaultLauncher(this).launchHomeOrClearDefaultsDialog()
//            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
//            startActivityForResult(intent,0)
//        }
    }
}