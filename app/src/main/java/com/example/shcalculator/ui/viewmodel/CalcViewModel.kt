package com.example.shcalculator.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.shcalculator.data.EyeThrow
import com.example.shcalculator.data.StrongholdPrediction

class SearchViewModel : ViewModel() {

    var eyeThrows by mutableStateOf(emptyList<EyeThrow>())
        private set

    var prediction by mutableStateOf(
        StrongholdPrediction(Double.NaN, Double.NaN)
    )
        private set

    fun addEyeThrow(eyeThrow: EyeThrow) {
        eyeThrows = eyeThrows + eyeThrow
    }

    fun deleteEyeThrow(eyeThrow: EyeThrow) {
        eyeThrows = eyeThrows.filterNot { it == eyeThrow }
    }

    fun updatePrediction(newPrediction: StrongholdPrediction) {
        prediction = newPrediction
    }
}