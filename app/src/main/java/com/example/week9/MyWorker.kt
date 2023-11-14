package com.example.week9

import android.content.Context
import androidx.activity.viewModels
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        try {

        } catch (e: Exception) {
            return Result.retry() // 네트워크 오류 등으로 다시 시도 필요
        }
        return Result.success() // 성공일 경우 리턴
    }
    companion object { // worker 식별자로 사용할 이름
        const val name = "com.example.repository_pattern.MyWorker"
    }
}