package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class FirebaseSyncService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .writeTimeout(18, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun testConnection(firebaseUrl: String, apiKey: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val cleanUrl = firebaseUrl.trim().trimEnd('/')
        if (cleanUrl.isBlank()) {
            return@withContext Result.failure(Exception("Firebase Database URL cannot be empty."))
        }
        if (!cleanUrl.startsWith("https://") && !cleanUrl.startsWith("http://")) {
            return@withContext Result.failure(Exception("Firebase URL must start with https://"))
        }

        try {
            // Test Firebase Realtime Database or Firestore REST endpoint
            val testTarget = if (cleanUrl.contains("firestore.googleapis.com")) {
                cleanUrl
            } else {
                val authParam = if (!apiKey.isNullOrBlank()) "?auth=$apiKey" else ""
                "$cleanUrl/.json$authParam"
            }

            val request = Request.Builder()
                .url(testTarget)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 200 || response.code == 204) {
                    Result.success("Connected to Firebase Database successfully! (${cleanUrl.take(45)}...)")
                } else if (response.code == 401 || response.code == 403) {
                    Result.failure(Exception("Firebase Permission Denied (HTTP ${response.code}). Check database security rules or API key."))
                } else {
                    Result.success("Firebase endpoint reachable (HTTP ${response.code})")
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Firebase connection failed: ${e.message}"))
        }
    }

    suspend fun uploadBackupPayload(
        firebaseUrl: String,
        apiKey: String? = null,
        payloadJson: String,
        recordCount: Long
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanUrl = firebaseUrl.trim().trimEnd('/')
        if (cleanUrl.isBlank()) {
            return@withContext Result.failure(Exception("Firebase Database URL is required for cloud sync."))
        }

        try {
            val timestamp = System.currentTimeMillis()
            val targetUrl = if (cleanUrl.contains("firestore.googleapis.com")) {
                cleanUrl
            } else {
                val authParam = if (!apiKey.isNullOrBlank()) "?auth=$apiKey" else ""
                "$cleanUrl/escape_clothing_pos/backups/latest.json$authParam"
            }

            val request = Request.Builder()
                .url(targetUrl)
                .put(payloadJson.toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 200 || response.code == 204) {
                    Result.success("Cloud backup snapshot successfully synced to Firebase ($recordCount records)")
                } else if (response.code == 401 || response.code == 403) {
                    Result.failure(Exception("Firebase access denied (HTTP ${response.code}). Please verify Firebase rules allow write."))
                } else {
                    Result.failure(Exception("Firebase sync returned HTTP ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Firebase sync network error: ${e.message}"))
        }
    }
}
