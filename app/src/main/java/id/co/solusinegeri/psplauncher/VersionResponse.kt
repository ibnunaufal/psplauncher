package id.co.solusinegeri.psplauncher


import androidx.annotation.Keep

@Keep
data class VersionResponse(
    val _id: String,
    val date: String,
    val id: String,
    val isUrgent: Boolean,
    val msg: List<Any>,
    val statusMigrasi: Boolean,
    val versionCode: String
)