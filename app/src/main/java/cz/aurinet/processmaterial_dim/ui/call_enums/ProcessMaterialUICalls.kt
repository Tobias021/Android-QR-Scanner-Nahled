package cz.aurinet.processmaterial_dim.ui.call_enums

enum class ProcessMaterialUICalls {
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
    //neexistující blok materiálu - KÓD:98
    ERR_MATERIAL_NEEXISTUJE,
    //Chyba komunikace se serverem
    ERR_SERVER,
    ERR_OBECNY
}