package cz.aurinet.odvadeni_cnc.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.aurinet.processmaterial_dim.data.Code
import cz.aurinet.odvadeni_cnc.repository.OdvadeniPrikazRepository
import cz.aurinet.odvadeni_cnc.ui.call_enums.OdvadeniPrikazUICalls
import kotlinx.coroutines.launch

class OdvadeniPrikazViewModel: ViewModel()  {
    private val _state = MutableLiveData(OdvadeniPrikazUICalls.TO_DEFAULT)
    private val repository = OdvadeniPrikazRepository.getInstance()
    val state: LiveData<OdvadeniPrikazUICalls> = _state

    fun setIsScannerBlocked(blocked: Boolean){
        repository.isScannerBlocked = blocked
    }

    fun getIsScannerBlocked(): Boolean{
        return repository.isScannerBlocked
    }

    fun callApi(code: Code){
        _state.postValue(OdvadeniPrikazUICalls.SHOW_PROGRESSBAR)
        var callback = "100"
        val apiJob = viewModelScope.launch {
                callback = repository.getCallback(code)
        }
        viewModelScope.launch {
            apiJob.join()
            when (callback) {
                "100" -> _state.postValue(OdvadeniPrikazUICalls.ERR_OBECNY)
                "99" -> _state.postValue(OdvadeniPrikazUICalls.ERR_KOD)
                "98" -> _state.postValue(OdvadeniPrikazUICalls.ERR_PRIKAZ_NEEXISTUJE)
                "97" -> _state.postValue(OdvadeniPrikazUICalls.ERR_SERVER)
                "4" -> _state.postValue(OdvadeniPrikazUICalls.ERR_PRIKAZ_UZAVREN)
                "1" -> _state.postValue(OdvadeniPrikazUICalls.CALLBACK_OK)
            }
        }

    }

    fun getCode() = repository.code
    fun getProcedureName() = repository.procedureName

    }

