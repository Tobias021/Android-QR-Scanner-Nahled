package cz.aurinet.tisk_kodu_cnc.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.aurinet.tisk_kodu_cnc.repository.CodePrintRepository
import cz.aurinet.tisk_kodu_cnc.ui.call_enums.CodePrintUICalls
import kotlinx.coroutines.launch

class CodePrintViewModel: ViewModel()  {
    private val _state = MutableLiveData(CodePrintUICalls.TO_DEFAULT)
    private val repository = CodePrintRepository.getInstance()
    val state: LiveData<CodePrintUICalls> = _state

    fun callApi(){
        var callback = "100"
        val apiJob = viewModelScope.launch {
                callback = repository.getCallback()
        }
        viewModelScope.launch {
            apiJob.join()
            when (callback) {
                "100" -> _state.postValue(CodePrintUICalls.ERR_OBECNY)
                "99" -> _state.postValue(CodePrintUICalls.ERR_KOD)
                "98" -> _state.postValue(CodePrintUICalls.ERR_PRIKAZ_NEEXISTUJE)
                "97" -> _state.postValue(CodePrintUICalls.ERR_SERVER)
                "4" -> _state.postValue(CodePrintUICalls.ERR_PRIKAZ_UZAVREN)
                "1" -> _state.postValue(CodePrintUICalls.CALLBACK_OK)
            }
        }

    }

    fun getCode() = repository.code
    fun getProcedureName() = repository.procedureName

    }

