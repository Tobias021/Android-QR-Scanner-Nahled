package cz.aurinet.odvadeni_cnc.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.aurinet.odvadeni_cnc.repository.StockMaterialRepository
import cz.aurinet.processmaterial_dim.data.Code
import cz.aurinet.processmaterial_dim.ui.call_enums.ProcessMaterialUICalls
import kotlinx.coroutines.launch

class StockMaterialViewModel: ViewModel()  {
    private val _state = MutableLiveData(ProcessMaterialUICalls.TO_DEFAULT)
    private val repository = StockMaterialRepository.getInstance()
    val state: LiveData<ProcessMaterialUICalls> = _state

    fun setIsScannerBlocked(blocked: Boolean){
        repository.isScannerBlocked = blocked
    }

    fun getIsScannerBlocked(): Boolean{
        return repository.isScannerBlocked
    }

    fun callApi(code: Code){
        _state.postValue(ProcessMaterialUICalls.SHOW_PROGRESSBAR)
        var callback = "100"
        val apiJob = viewModelScope.launch {
                callback = repository.getCallback(code)
        }
        viewModelScope.launch {
            apiJob.join()
            when (callback) {
                "100" -> _state.postValue(ProcessMaterialUICalls.ERR_OBECNY)
                "99" -> _state.postValue(ProcessMaterialUICalls.ERR_KOD)
                "98" -> _state.postValue(ProcessMaterialUICalls.ERR_MATERIAL_NEEXISTUJE)
                "97" -> _state.postValue(ProcessMaterialUICalls.ERR_SERVER)
                "1" -> _state.postValue(ProcessMaterialUICalls.CALLBACK_OK)
            }
        }

    }

    fun getCode() = repository.code
    fun getProcedureName() = repository.procedureName

    }

