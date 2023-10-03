package cz.aurinet.api

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException


data class BridgeCallback(val url: String, val args: Map<String, String?>, val retrieveArgs: Array<String>){
    private lateinit var finalURL: String
    private lateinit var callbackResponse: List<String>
    private var status = StatusCode.PROCESSING

    enum class StatusCode() {
        OK,
        ERROR,
        ERROR_SERVER,
        PROCESSING
    }

    //sestav listener pro Networking request builder
    private var jsonArrayRequestListener: JSONArrayRequestListener = object: JSONArrayRequestListener{
        var tempCallbackResponse = mutableListOf<String>()
        override fun onResponse(response: JSONArray?) {
            status = if (response!!.length()> 0) {
                try {
                    for(arg in retrieveArgs){
                        val callback = response.getJSONObject(0).get(arg).toString()
                        tempCallbackResponse.add(callback)
                    }
                    StatusCode.OK
                } catch (e: JSONException) {
                    e.printStackTrace()
                    StatusCode.ERROR
                }
            }else{
                StatusCode.ERROR
            }
            callbackResponse = tempCallbackResponse
        }

        override fun onError(anError: ANError?) {
            status = StatusCode.ERROR_SERVER
        }

    }

    suspend fun getResponse(): List<String>{
        /*
         *Sestav celou URL adresu pro AndroidNetworking
         */
        val tmpURL = StringBuilder()
        tmpURL.append(url)
        //Pro každé d v args: Map na konec základní adresy přidej "/{d}"
        for(d in args){
            tmpURL.append("/{${d.key}}")
        }
        finalURL = tmpURL.toString()

        //sestav AndroidNetworking
        val request = AndroidNetworking.get(finalURL)
            .addHeaders("token", "1234")
            .setPriority(Priority.HIGH)
        //prolistuj map args a přidej parametry
        for(d in args){
            request.addPathParameter(d.key,d.value)
        }
        //sestav a odešli request pomocí listeneru
        val requestJob = CoroutineScope(Dispatchers.IO).launch {
                request.build().getAsJSONArray(jsonArrayRequestListener)
            while(status == StatusCode.PROCESSING){
                delay(80)
            }
        }

        //počkej na requestJob a vrať odpověď api nebo chybový kód
        requestJob.join()
        return when(status){
            StatusCode.OK -> callbackResponse
            StatusCode.ERROR -> listOf("")
            StatusCode.ERROR_SERVER -> listOf("97")
            else -> {
                listOf("100")}
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BridgeCallback

        if (!retrieveArgs.contentEquals(other.retrieveArgs)) return false

        return true
    }

    override fun hashCode(): Int {
        return retrieveArgs.contentHashCode()
    }

}
