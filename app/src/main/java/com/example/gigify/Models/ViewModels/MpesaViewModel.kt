package com.example.gigify.Models.ViewModels

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigify.Network.*
import com.example.gigify.Utils.MpesaConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MpesaViewModel : ViewModel() {

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus = _paymentStatus.asStateFlow()

    private val mpesaService: MpesaService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://sandbox.safaricom.co.ke/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MpesaService::class.java)
    }

    fun initiateSTKPush(phoneNumber: String, amount: Int) {
        viewModelScope.launch {
            try {
                val auth = Base64.encodeToString(
                    "${MpesaConfig.CONSUMER_KEY}:${MpesaConfig.CONSUMER_SECRET}".toByteArray(),
                    Base64.NO_WRAP
                )
                val tokenResponse = mpesaService.getAccessToken("Basic $auth")

                if (tokenResponse.isSuccessful) {
                    val accessToken = tokenResponse.body()?.accessToken ?: return@launch

                    val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                    val password = Base64.encodeToString(
                        "${MpesaConfig.BUSINESS_SHORT_CODE}${MpesaConfig.PASSKEY}$timestamp".toByteArray(),
                        Base64.NO_WRAP
                    )

                    val request = STKPushRequest(
                        businessShortCode = MpesaConfig.BUSINESS_SHORT_CODE,
                        password = password,
                        timestamp = timestamp,
                        amount = amount,
                        partyA = phoneNumber, // User's phone
                        partyB = MpesaConfig.BUSINESS_SHORT_CODE,
                        phoneNumber = phoneNumber,
                        callBackURL = "https://your-domain.com/callback",
                        accountReference = "Gigify Payment",
                        transactionDesc = "Payment for Job"
                    )

                    val response = mpesaService.sendSTKPush("Bearer $accessToken", request)
                    if (response.isSuccessful) {
                        _paymentStatus.value = "Prompt sent to $phoneNumber"
                    } else {
                        _paymentStatus.value = "Error: ${response.message()}"
                    }
                } else {
                    _paymentStatus.value = "Failed to get token"
                }
            } catch (e: Exception) {
                _paymentStatus.value = "Exception: ${e.message}"
            }
        }
    }
}
