package com.purecipes.shared.data.network

private const val LOCAL_BACKEND_BASE_URL = "http://10.0.2.2:9090/"

actual fun localBackendBaseUrl(): String = LOCAL_BACKEND_BASE_URL
