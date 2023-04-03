package id.co.solusinegeri.psplauncher.networks

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface APIService {

    @GET("main_a/info/google-play/id.co.solusinegeri.psplauncher")
    suspend fun getVersi(): Response<ResponseBody>
}