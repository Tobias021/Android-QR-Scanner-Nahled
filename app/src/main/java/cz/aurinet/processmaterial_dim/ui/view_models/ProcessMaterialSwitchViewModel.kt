package cz.aurinet.processmaterial_dim.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.aurinet.processmaterial_dim.repository.ProcessMaterialSwitchRepository
import cz.aurinet.processmaterial_dim.ui.call_enums.ProcessMaterialSwitchUICalls
import kotlinx.coroutines.launch

class ProcessMaterialSwitchViewModel: ViewModel()  {
    private val _state = MutableLiveData(ProcessMaterialSwitchUICalls.TO_DEFAULT)
    private val repository = ProcessMaterialSwitchRepository.getInstance()
    val state: LiveData<ProcessMaterialSwitchUICalls> = _state

    fun callApi(){
        var callback = "100"
        val apiJob = viewModelScope.launch {
                callback = repository.getCallback()
        }
        viewModelScope.launch {
            apiJob.join()
            when (callback) {
                "100" -> _state.postValue(ProcessMaterialSwitchUICalls.ERR_OBECNY)
                "99" -> _state.postValue(ProcessMaterialSwitchUICalls.ERR_KOD)
                "98" -> _state.postValue(ProcessMaterialSwitchUICalls.ERR_PRIKAZ_NEEXISTUJE)
                "97" -> _state.postValue(ProcessMaterialSwitchUICalls.ERR_SERVER)
                "4" -> _state.postValue(ProcessMaterialSwitchUICalls.ERR_PRIKAZ_UZAVREN)
                "1" -> _state.postValue(ProcessMaterialSwitchUICalls.CALLBACK_OK)
            }
        }

    }

    fun getCode() = repository.code
    fun getProcedureName() = repository.procedureName

    }

