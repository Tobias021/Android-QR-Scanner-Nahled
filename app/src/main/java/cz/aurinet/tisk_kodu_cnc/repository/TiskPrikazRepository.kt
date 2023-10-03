package cz.aurinet.tisk_kodu_cnc.repository

import cz.aurinet.odvadeni_cnc.ProcessMaterialPropertySingleton
import cz.aurinet.api.BridgeCallback
import cz.aurinet.processmaterial_dim.data.Code

class TiskPrikazRepository private constructor(){
    lateinit var code: String
    lateinit var procedureName: String
    var isScannerBlocked: Boolean = false
    private val singleton = ProcessMaterialPropertySingleton
    val isZebra : Boolean = singleton.isZebraDevice

    //Provolá api na dané URL, vloží argumenty v Mapu a získá argumenty v Array
    suspend fun getCallback(code: Code): String {
        clearSingleton()
        singleton.procedureBarcode = code.code
        singleton.procedureName = code.getProcedureName()
        this.code = code.code
        procedureName = code.getProcedureName()
        return BridgeCallback(
            ProcessMaterialPropertySingleton.PRODUCTION_ENPOINTS_URL + "search-production-order",
            mapOf(Pair("qrcode", code.code)),
            arrayOf("status")
        )
            .getResponse()[0]
    }

    private fun clearSingleton(){
        singleton.procedureBarcode = null
        singleton.procedureName = null
        singleton.procedureMaterialBlock = "HAODPR|PRINTCODE"
    }

    //statický objekt, vrací instanci
    companion object{
        //Volatile - všechna vlákna vidí změnu
        @Volatile private var instance: TiskPrikazRepository? = null
        fun getInstance(): TiskPrikazRepository{
            instance ?: synchronized(this){
                instance ?: TiskPrikazRepository().also { instance = it }
            }
            return instance!!
        }
    }

}

