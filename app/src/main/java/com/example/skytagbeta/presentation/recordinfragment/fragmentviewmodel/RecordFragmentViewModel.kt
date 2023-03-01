package com.example.skytagbeta.presentation.recordinfragment.fragmentviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordFragmentViewModel: ViewModel() {

     val showButtons = MutableLiveData<Boolean>()

    fun showButtons(isVisible: Boolean){
        showButtons.postValue(isVisible)

    }
}