package org.jaaksi.coicache.extention

import kotlinx.coroutines.flow.flow
import org.jaaksi.coicache.RequestApi


suspend fun <T> buildApi(block: suspend () -> T): RequestApi<T> {
    return RequestApi(
        flow { emit(block()) }
    )
}
