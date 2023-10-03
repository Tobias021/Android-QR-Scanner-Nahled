package cz.aurinet.processmaterial_dim.ui.call_enums

enum class ProcessMaterialSwitchUICalls {
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