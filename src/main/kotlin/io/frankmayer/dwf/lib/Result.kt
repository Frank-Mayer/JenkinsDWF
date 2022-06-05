package io.frankmayer.dwf.lib

class Result<T, E> {
    private val value: T?
    private val error: E?
    private val isOk: Boolean

    companion object {
        fun <T, E> success(value: T): Result<T, E> {
            return Result(value, null, true)
        }

        fun <T, E> failure(error: E): Result<T, E> {
            return Result(null, error, false)
        }
    }

    private constructor(value: T?, error: E?, ok: Boolean) {
        this.value = value
        this.error = error
        this.isOk = ok
    }

    fun <R> map(okSuccess: (T) -> R, onFailure: (E) -> R): R {
        return if (isOk) {
            okSuccess(value!!)
        } else {
            onFailure(error!!)
        }
    }
}
