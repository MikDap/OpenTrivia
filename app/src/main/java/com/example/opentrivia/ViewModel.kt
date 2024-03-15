package com.example.opentrivia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ViewModel: ViewModel() {

    private val _isReady = MutableStateFlow(false)

    val isReady = _isReady.asStateFlow()

    init {
         viewModelScope.launch {
             delay(2000L)
             _isReady.value = true
          }
         }
    }