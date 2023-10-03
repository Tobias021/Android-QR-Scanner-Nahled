package cz.aurinet.processmaterial_dim.repository

import cz.aurinet.odvadeni_cnc.ProcessMaterialPropertySingleton
import cz.aurinet.api.BridgeCallback
import cz.aurinet.processmaterial_dim.data.Code

class ProcessMaterialRepository{
    lateinit var code: String
    lateinit var procedureName: String
    var isScannerBlocked: Boolean = false

    private val singleton = ProcessMaterialPropertySingleton

    suspend fun getCallback(code: Code): String {
        singleton.procedureMaterialBlock = code.code
        this.code = code.code
        procedureName = code.getProcedureName()
        val callback = BridgeCallback(
            ProcessMaterialPropertySingleton.PRODUCTION_ENPOINTS_URL + "search-material-stock",
            mapOf(Pair("qrcode", code.code)),
            arrayOf("status", "sarze", "nazev")
        )
            .getResponse()
        singleton.materialBatch = callback[1]
        singleton.materialName = callback[2]
        return callback[0]
    }


    companion object{
        @Volatile private var instance: ProcessMaterialRepository? = null
        fun getInstance(): ProcessMaterialRepository {
            instance ?: synchronized(this){
                instance ?: ProcessMaterialRepository().also { instance = it }
            }
            return instance!!
        }
    }

}

