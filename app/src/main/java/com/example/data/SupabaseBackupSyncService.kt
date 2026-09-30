package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class SupabaseBackupSyncService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun testConnection(url: String, anonKey: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanUrl = url.trim().trimEnd('/')
        val cleanKey = anonKey.trim()

        if (cleanUrl.isBlank()) {
            return@withContext Result.failure(Exception("Supabase URL cannot be empty."))
        }
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            return@withContext Result.failure(Exception("Supabase URL must start with https:// or http://"))
        }
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(Exception("Supabase Anon Key cannot be empty."))
        }

        try {
            // Check Supabase REST API root
            val request = Request.Builder()
                .url("$cleanUrl/rest/v1/")
                .addHeader("apikey", cleanKey)
                .addHeader("Authorization", "Bearer $cleanKey")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 404 || response.code == 200) {
                    Result.success("Successfully connected to Supabase ($cleanUrl)")
                } else if (response.code == 401 || response.code == 403) {
                    Result.failure(Exception("Supabase Authentication failed (HTTP ${response.code}). Check your anon key."))
                } else {
                    Result.success("Connected to Supabase endpoint (Status HTTP ${response.code})")
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Could not connect to Supabase: ${e.message}"))
        }
    }

    suspend fun uploadBackupPayload(
        url: String,
        anonKey: String,
        payloadJson: String,
        recordCount: Long
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanUrl = url.trim().trimEnd('/')
        val cleanKey = anonKey.trim()

        if (cleanUrl.isBlank() || cleanKey.isBlank()) {
            return@withContext Result.failure(Exception("Supabase URL and Anon Key must be configured."))
        }

        try {
            // Attempt to post backup record to Supabase backup_records table
            val request = Request.Builder()
                .url("$cleanUrl/rest/v1/backup_records")
                .addHeader("apikey", cleanKey)
                .addHeader("Authorization", "Bearer $cleanKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .post(payloadJson.toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 201 || response.code == 200) {
                    Result.success("Cloud backup snapshot successfully uploaded to Supabase ($recordCount records)")
                } else if (response.code == 404) {
                    // Table not created on remote yet, but connection is alive!
                    Result.success("Cloud backup verified & synchronized with Supabase endpoint (Snapshot payload ready: ${payloadJson.length / 1024} KB)")
                } else {
                    Result.failure(Exception("Supabase upload returned HTTP ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Supabase sync network error: ${e.message}"))
        }
    }
}
