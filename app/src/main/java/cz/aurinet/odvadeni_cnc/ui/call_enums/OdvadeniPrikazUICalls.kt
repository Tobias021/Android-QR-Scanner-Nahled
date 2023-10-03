package cz.aurinet.odvadeni_cnc.ui.call_enums

enum class OdvadeniPrikazUICalls {
    /*
    * Status enumy pro LiveData objekt ve třídě OdvadeniPrikazActivity
    */
    SHOW_SCANNER,
    SHOW_PROGRESSBAR,
    TO_DEFAULT,
    //Callback proběhl správně
    CALLBACK_OK,
    //Chybný kód - KÓD:99
    ERR_KOD,
    //neexistující příkaz
    ERR_PRIKAZ_NEEXISTUJE,
    //Příkaz již uzavřen
    ERR_PRIKAZ_UZAVREN,
    //Chyba komunikace se serverem
    ERR_SERVER,
    ERR_OBECNY
}