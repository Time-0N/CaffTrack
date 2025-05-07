package com.example.cafftrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafftrack.model.entity.CaffeineEntry
import com.example.cafftrack.repository.CaffeineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaffeineViewModel @Inject constructor(
    private val repository: CaffeineRepository
) : ViewModel() {

    val caffeineEntries: StateFlow<List<CaffeineEntry>> =
        repository.getAllEntries()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCaffeine: StateFlow<Int> =
        caffeineEntries
            .map { entries ->
                val now = System.currentTimeMillis()
                val startOfDay = now - (now % (24 * 60 * 60 * 1000))
                entries.filter { it.timestamp >= startOfDay }.sumOf { it.caffeineMg }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageCaffeineLast7Days: StateFlow<Int> =
        caffeineEntries
            .map { entries ->
                val now = System.currentTimeMillis()
                val sevenDaysAgo = now - 7 * 24 * 60 * 60 * 1000L

                val last7DaysEntries = entries.filter { it.timestamp >= sevenDaysAgo }

                if (last7DaysEntries.isNotEmpty()) {
                    last7DaysEntries.sumOf { it.caffeineMg } / 7
                } else {
                    0
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun insert(entry: CaffeineEntry) {
        viewModelScope.launch {
            repository.insert(entry)
        }
    }

    fun insertEntry(name: String, caffeineMg: Int) {
        insert(CaffeineEntry(name = name, caffeineMg = caffeineMg))
    }

    fun delete(entry: CaffeineEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}