package cz.aurinet.tisk_kodu_cnc.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.aurinet.processmaterial_dim.data.Code
import cz.aurinet.tisk_kodu_cnc.ui.call_enums.TiskPrikazUICalls
import kotlinx.coroutines.launch

class TiskPrikazViewModel: ViewModel()  {
    private val _state = MutableLiveData(TiskPrikazUICalls.TO_DEFAULT)
    private val repository = cz.aurinet.tisk_kodu_cnc.repository.TiskPrikazRepository.getInstance()
    val state: LiveData<TiskPrikazUICalls> = _state

    fun setIsScannerBlocked(blocked: Boolean){
        repository.isScannerBlocked = blocked
    }

    fun getIsScannerBlocked(): Boolean{
        return repository.isScannerBlocked
    }

    fun callApi(code: Code){
        _state.postValue(TiskPrikazUICalls.SHOW_PROGRESSBAR)
        var callback = "100"
        val apiJob = viewModelScope.launch {
                callback = repository.getCallback(code)
        }
        viewModelScope.launch {
            apiJob.join()
            when (callback) {
                "100" -> _state.postValue(TiskPrikazUICalls.ERR_OBECNY)
                "99" -> _state.postValue(TiskPrikazUICalls.ERR_KOD)
                "98" -> _state.postValue(TiskPrikazUICalls.ERR_PRIKAZ_NEEXISTUJE)
                "97" -> _state.postValue(TiskPrikazUICalls.ERR_SERVER)
                "4" -> _state.postValue(TiskPrikazUICalls.ERR_PRIKAZ_UZAVREN)
                "1" -> _state.postValue(TiskPrikazUICalls.CALLBACK_OK)
            }
        }

    }

    fun getCode() = repository.code
    fun getProcedureName() = repository.procedureName

    }

