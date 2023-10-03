package cz.aurinet.tisk_kodu_cnc.ui.call_enums

enum class CodePrintUICalls {
    /*
    * Status enumy pro LiveData objekt ve třídě OdvadeniPrikazActivity
    */
    TO_DEFAULT,
    //Callback proběhl správně
    CALLBACK_OK,
    //Chybný kód - KÓD:99
    ERR_KOD,
    //neexistující příkaz - KÓD:98
    ERR_PRIKAZ_NEEXISTUJE,
    //Příkaz již uzavřen KÓD:4
    ERR_PRIKAZ_UZAVREN,
    //Chyba komunikace se serverem
    ERR_SERVER,
    ERR_OBECNY
}