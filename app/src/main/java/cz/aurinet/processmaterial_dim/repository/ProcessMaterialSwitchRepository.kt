package cz.aurinet.processmaterial_dim.repository

import cz.aurinet.odvadeni_cnc.ProcessMaterialPropertySingleton
import cz.aurinet.api.BridgeCallback

class ProcessMaterialSwitchRepository{
    lateinit var code: String
    lateinit var procedureName: String
    var isScannerBlocked: Boolean = false

    private val singleton = ProcessMaterialPropertySingleton

    //Provolá api na dané URL, vloží argumenty v Mapu a získá argumenty v Array
    suspend fun getCallback(): String {
        return BridgeCallback(
            ProcessMaterialPropertySingleton.PRODUCTION_ENPOINTS_URL + "production-order-close",
            mapOf(
                Pair("qrcode", singleton.procedureBarcode),
                Pair("qrbatch", singleton.procedureMaterialBlock)
            ),
            arrayOf("status")
        )
            .getResponse()[0]
    }

    //statický objekt, vrací instanci
    companion object{
        //Volatile - všechna vlákna vidí změnu
        @Volatile private var instance: ProcessMaterialSwitchRepository? = null
        fun getInstance(): ProcessMaterialSwitchRepository {
            instance ?: synchronized(this){
                instance ?: ProcessMaterialSwitchRepository().also { instance = it }
            }
            return instance!!
        }
    }

}

