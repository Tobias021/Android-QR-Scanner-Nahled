package cz.aurinet.data

/*
 * Singleton s globálními proměnnými.
 */
class GlobalPropertySingleton {

    /*
     * Konstanta názvu Zebra zařízení.
     */
    private val ZEBRA_DEVICE = "Zebra"

    /*
     * Konstanta názvu Zebra zařízení.
     */
    val PRODUCTION_ENPOINTS_URL = "http://192.168.40.108:8080/api/v1/productions/"

    val DELAY_MS = 5000

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