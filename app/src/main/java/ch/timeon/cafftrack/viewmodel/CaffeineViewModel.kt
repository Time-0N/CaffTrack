package ch.timeon.cafftrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.timeon.cafftrack.model.dto.LookupResult
import ch.timeon.cafftrack.model.entity.CaffeineEntryEntity
import ch.timeon.cafftrack.model.entity.UserProfileEntity
import ch.timeon.cafftrack.model.enums.Sex
import ch.timeon.cafftrack.repository.CaffeineRepository
import ch.timeon.cafftrack.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaffeineViewModel @Inject constructor(
    private val caffeineRepository: CaffeineRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val getProfile: StateFlow<UserProfileEntity?> =
        userProfileRepository.getProfile()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(weightKg: Int, sex: Sex) {
        viewModelScope.launch {
            userProfileRepository.insertOrUpdateProfile(
                UserProfileEntity(id = 0, weightKg = weightKg, sex = sex)
            )
        }
    }

    val caffeineEntries: StateFlow<List<CaffeineEntryEntity>> =
        caffeineRepository.getAllEntries()
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

    private fun insert(entry: CaffeineEntryEntity) {
        viewModelScope.launch {
            caffeineRepository.insert(entry)
        }
    }

    fun insertEntry(name: String, caffeineMg: Int) {
        insert(CaffeineEntryEntity(name = name, caffeineMg = caffeineMg))
    }

    fun delete(entry: CaffeineEntryEntity) {
        viewModelScope.launch {
            caffeineRepository.delete(entry)
        }
    }

    val currentCaffeineInBlood: StateFlow<Double> =
        combine(caffeineEntries, getProfile) { entries, user ->
            if (user == null) {
                -1.0
            } else {
                val now = System.currentTimeMillis()
                entries.sumOf { entry ->
                    val hours = (now - entry.timestamp) / (1000 * 60 * 60).toDouble()
                    val halfLife = if (user.sex == Sex.Male) 5.0 else 6.0
                    entry.caffeineMg * Math.pow(0.5, hours / halfLife)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1.0)

    private val _errorMessages = MutableSharedFlow<String>()
    val errorMessages = _errorMessages.asSharedFlow()

    fun lookupAndLog(upc: String) {
        viewModelScope.launch {
            when ( val result = caffeineRepository.lookupCaffeineFromUpc(upc) ) {
                is LookupResult.Success -> insert(result.entry)
                LookupResult.NotFound -> _errorMessages.emit("Product not found")
                LookupResult.NoCaffeine -> _errorMessages.emit("This product contains no caffeine")
            }
        }
    }
}