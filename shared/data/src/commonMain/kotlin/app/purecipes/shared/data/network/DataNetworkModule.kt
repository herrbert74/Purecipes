package app.purecipes.shared.data.network

import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.data.session.SessionTokenStore
import com.diamondedge.logging.Logger
import com.diamondedge.logging.logging
import de.jensklingenberg.ktorfit.Ktorfit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
interface DataNetworkModule {

	@Provides
	fun provideHttpClient(sessionTokenStore: SessionTokenStore): HttpClient {
		return createPurecipesHttpClient(sessionTokenStore)
	}

	@Provides
	fun providePurecipesApi(httpClient: HttpClient, purecipesConfig: PurecipesConfig): PurecipesApi {
		val backendBaseUrl = backendBaseUrl(
			purecipesConfig.buildType(),
			purecipesConfig.debugBackendHostOverride(),
		)
		val ktorfit = Ktorfit.Builder()
			.baseUrl(if (backendBaseUrl.endsWith("/")) backendBaseUrl else "$backendBaseUrl/")
			.httpClient(httpClient)
			.build()
		return ktorfit.createPurecipesApi()
	}
}

internal expect fun createPurecipesHttpClient(sessionTokenStore: SessionTokenStore): HttpClient

internal fun HttpClientConfig<*>.configurePurecipesHttpClient(sessionTokenStore: SessionTokenStore) {
	expectSuccess = true

	install(DefaultRequest) {
		sessionTokenStore.currentAccessToken()?.takeIf { it.isNotBlank() }?.let { token ->
			headers.remove(HttpHeaders.Authorization)
			headers.append(HttpHeaders.Authorization, "Bearer $token")
		}
	}

	install(ContentNegotiation) {
		json(purecipesJson())
	}
	install(Logging) {
		logger = object : Logger, io.ktor.client.plugins.logging.Logger {
			override fun log(message: String) {
				logging().d { message }
			}

			override fun verbose(tag: String, msg: String) {
				TODO("Not yet implemented")
			}

			override fun debug(tag: String, msg: String) {
				TODO("Not yet implemented")
			}

			override fun info(tag: String, msg: String) {
				TODO("Not yet implemented")
			}

			override fun warn(tag: String, msg: String, t: Throwable?) {
				TODO("Not yet implemented")
			}

			override fun error(tag: String, msg: String, t: Throwable?) {
				TODO("Not yet implemented")
			}

			override fun isLoggingVerbose(): Boolean {
				TODO("Not yet implemented")
			}

			override fun isLoggingDebug(): Boolean {
				TODO("Not yet implemented")
			}

			override fun isLoggingInfo(): Boolean {
				TODO("Not yet implemented")
			}

			override fun isLoggingWarning(): Boolean {
				TODO("Not yet implemented")
			}

			override fun isLoggingError(): Boolean {
				TODO("Not yet implemented")
			}
		}
		level = io.ktor.client.plugins.logging.LogLevel.ALL
	}
}

internal fun purecipesJson() = Json {
	ignoreUnknownKeys = true
	explicitNulls = false
}
