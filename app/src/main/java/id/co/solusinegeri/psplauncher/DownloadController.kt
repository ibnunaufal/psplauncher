package id.co.solusinegeri.psplauncher

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File


class DownloadController(private val context: Context) {
    companion object {
        lateinit var m_progress: ProgressDialog
        val progressDialog = CustomProgressDialog()
//        private const val FILE_NAME = "File.apk"
        private const val FILE_NAME = "SampleDownloadApp.apk"
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }
    fun enqueueDownload(url: String) {
//===== Ini buat yang di playstore =================================================================
//        val file = File(Environment.getExternalStorageDirectory(), "Download")
//        var destination = file.absolutePath
//            destination += FILE_NAME
//        val uri = Uri.fromFile(file)
//        val tempDest = Environment.DIRECTORY_DOWNLOADS + "/" + FILE_NAME
//        val file2 = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//            FILE_NAME
//        ) // Set Your File Name
//        Log.i("file2", file2.absolutePath)
//        if (file2.exists()) {
//            file2.delete()
//            Log.i("file2", "deleting ${file2.absolutePath}")
//        }
//        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val downloadUri = Uri.parse(url)
//        val request = DownloadManager.Request(downloadUri)
//        request.setMimeType(MIME_TYPE)
//        request.setDescription(context.getString(R.string.downloading))
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FILE_NAME);
//        showInstallOption(destination, uri)
//        // Enqueue a new download and same the referenceId
//        downloadManager.enqueue(request)
//        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG)
//            .show()
//====== Ini buat yang di non play store ===========================================================
        var destination =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        destination += FILE_NAME
        val uri = Uri.parse("$FILE_BASE_PATH$destination")
        val file = File(destination)
        if (file.exists()) file.delete()
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
//        request.setTitle(context.getString(R.string.title_file_download))
        request.setDescription(context.getString(R.string.downloading))
        // set destination
        request.setDestinationUri(uri)
        showInstallOption(destination, uri)
        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG)
            .show()
//==================================================================================================
        progressDialog.show(context,"Mengunduh")
    }
    private fun showInstallOption(
        destination: String,
        uri: Uri
    ) {
        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                progressDialog.dialog.dismiss()
                Log.d("showInstallOption", "dismiss")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.d("showInstallOption", ">= N")
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                        File(destination)
                    )
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    Log.d("showInstallOption", install.toString())
                    Log.d("showInstallOptionExtr", install.extras.toString())
                    Log.d("showInstallOptionData", install.data.toString())
//                    try {
//                        Log.d("showInstallOption", "try")
//
//                    } catch (e: ActivityNotFoundException) {
//                        e.printStackTrace()
//                        Log.e("TAG", "Error in opening the file!")
//                    }

//===== Ini buat yang di playstore =================================================================
//                    confirmOpenFile()
//====== Ini buat yang di non play store ===========================================================

//==================================================================================================
                    context.unregisterReceiver(this)
                    // finish()
                } else {
                    Log.d("showInstallOption", "< N")
                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(
                        uri,
                        APP_INSTALL_PATH
                    )
                    Log.d("showInstallOption", "install")
//===== Ini buat yang di playstore =================================================================
//                    confirmOpenFile()
//====== Ini buat yang di non play store ===========================================================
                    context.startActivity(install)
//==================================================================================================
                    context.unregisterReceiver(this)
                    // finish()
                }
            }
        }
        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun uriFromFile(context: Context, file: File): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun confirmOpenFile(){
        val builder = AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert)

        builder.setTitle("Berhasil Mengunduh")
        builder.setMessage(Html.fromHtml("<h3>Anda akan diarahkan ke file yang terunduh, Klik 'File.apk' terbaru untuk menginstall</h3>"))
        builder.setPositiveButton("Ya"){
                _,_ ->
            context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        }
        builder.setNegativeButton("Batal"){
                _, _ ->
        }

        val dialog = builder.create()
        dialog.show()
    }
}