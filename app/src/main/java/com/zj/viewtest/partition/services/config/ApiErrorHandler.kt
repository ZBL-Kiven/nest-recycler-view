package com.zj.viewtest.partition.services.config

import android.util.Log
import com.zj.api.interfaces.ErrorHandler
import retrofit2.HttpException
import java.net.UnknownHostException

object ApiErrorHandler : ErrorHandler {

    override fun onError(throwable: Throwable?): Pair<Boolean, Any?> {
        if (throwable is HttpException) {
            try {
                val errorInfo = throwable.response()?.body()?.toString()
                val errorString = throwable.response()?.errorBody()?.string()
                val errorCode = throwable.code()
                val errorBodyCode = throwable.response()?.code()
                Log.e("http result error", " ----- case: $errorInfo \ndetail = $errorString  \nerrorCode = $errorCode \nerrorBodyCode = $errorBodyCode")
            } catch (e: Exception) {
                Log.e("http test", "onHttpError ----- case: ${e.message} \n original error is :\n$throwable")
            }

        } else {
            try {
                if (throwable is UnknownHostException) {
                    Log.e("http test", "net work error")
                } else {
                    val msg = throwable?.message
                    if (msg.isNullOrEmpty()) return Pair(false, null)
                    Log.e("http test", "onHttpError ----- case: $msg")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        throwable?.printStackTrace()
        return Pair(false, null)
    }
}