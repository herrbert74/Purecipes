package app.purecipes.shared.data.network

import app.purecipes.shared.domain.model.AppleSignInRequest
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.EmailSignInRequest
import app.purecipes.shared.domain.model.FacebookSignInRequest
import app.purecipes.shared.domain.model.GoogleSignInRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface PurecipesAuthApi {

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("auth/apple")
	suspend fun signInWithApple(@Body request: AppleSignInRequest): AuthenticatedSession

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("auth/facebook")
	suspend fun signInWithFacebook(@Body request: FacebookSignInRequest): AuthenticatedSession

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("auth/google")
	suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): AuthenticatedSession

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("auth/email")
	suspend fun signInWithEmail(@Body request: EmailSignInRequest): AuthenticatedSession

	@GET("auth/session")
	suspend fun getCurrentSession(): AuthenticatedSession

	@POST("auth/sign-out")
	suspend fun signOut()

	@DELETE("auth/account")
	suspend fun deleteAccount()
}
