package cz.aurinet.odvadeni_cnc

/*
 * Singleton s globálními proměnnými.
 */
object ProcessMaterialPropertySingleton {



    /*
     * Konstanta názvu Zebra zařízení.
     */
    private const val ZEBRA_DEVICE = "Zebra Technologies"

    /*
     * Konstanta názvu Zebra zařízení.
     */
    const val PRODUCTION_ENPOINTS_URL = "http://192.168.40.108:8080/api/v1/productions/"

    const val DELAY_MS = 5000

    /*
     * Název procedury.
     */
    var procedureName: String? = null

    /*
     * Čárový kód procedury.
     */
    var procedureBarcode: String? = null

    /*
     * Čárový kód materiálového bloku.
     */
    var procedureMaterialBlock: String? = null

    /*
     * Šarže materiálového bloku.
     */
    var materialBatch: String? = null

    /*
     * Název materiálového bloku.
     */
    var materialName: String? = null

    /*
     * Název zařízení.
     */
    var deviceName: String? = null

    /*
     * Jedná se o Zebra zařízení.
     */
    val isZebraDevice: Boolean
        get() = deviceName!!.contains(ZEBRA_DEVICE)
}