package ch.timeon.cafftrack.model.dto

import ch.timeon.cafftrack.model.entity.CaffeineEntryEntity

sealed class LookupResult {
    data class Success(val entry: CaffeineEntryEntity): LookupResult()
    data object NotFound: LookupResult()
    data object NoCaffeine: LookupResult()
}