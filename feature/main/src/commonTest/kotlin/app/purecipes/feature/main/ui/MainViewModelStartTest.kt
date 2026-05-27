package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelStartTest {

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	private val sampleUser = fakeAuthUser(
		displayName = "Taylor",
		provider = AuthProvider.GOOGLE,
	)

	@Test
	fun `start refreshes consent on startup`() = runTest {
		val consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED)
		mainViewModelForTest(
			consentRepository = consentRepository,
			coroutineScope = testViewModelScope(),
		).start()

		consentRepository.refreshConsentCalled shouldBe true
	}

	@Test
	fun `start observes sign in and resumes post login filters navigation`() = runTest {
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			coroutineScope = testViewModelScope(),
		)
		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.RECIPE_SEARCH_FILTERS)
		viewModel.onOpenEmailSignIn(prefilledEmail = sampleUser.email)
		viewModel.start()
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)

		viewModel.authenticationState shouldBe AuthenticationState.SignedIn(sampleUser)
		viewModel.peekBackStack() shouldBe listOf(SearchDestination)
		viewModel.takePendingOpenSearchFilters() shouldBe true
	}

	@Test
	fun `start resumes cookbook share import after sign in`() = runTest {
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			coroutineScope = testViewModelScope(),
		)
		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.COOKBOOK_SHARE_IMPORT)
		viewModel.stageCookbookShareImport(sampleShareToken)
		viewModel.start()
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)

		viewModel.peekBackStack() shouldBe listOf(FavoritesDestination)
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `start routes unsigned cookbook share to account login`() = runTest {
		val links = MutableSharedFlow<PurecipesLink>(extraBufferCapacity = 1)
		val viewModel = mainViewModelForTest(
			incomingLinkRepository = incomingLinksRepository(links),
			coroutineScope = testViewModelScope(),
		)
		viewModel.start()
		links.emit(PurecipesLink.CookbookShare(sampleShareToken))

		viewModel.peekBackStack() shouldBe listOf(AccountDestination)
		viewModel.takePostLoginOriginAfterSignIn() shouldBe PostLoginNavOrigin.COOKBOOK_SHARE_IMPORT
		viewModel.takePendingCookbookShareToken() shouldBe sampleShareToken
	}

	@Test
	fun `start delivers recipe link when signed in`() = runTest {
		val links = MutableSharedFlow<PurecipesLink>(extraBufferCapacity = 1)
		val viewModel = mainViewModelForTest(
			authenticationRepository = FakeAuthenticationRepository(AuthenticationState.SignedIn(sampleUser)),
			incomingLinkRepository = incomingLinksRepository(links),
			coroutineScope = testViewModelScope(),
		)
		viewModel.start()
		links.emit(PurecipesLink.Recipe(77))

		viewModel.peekBackStack() shouldBe listOf(SearchDestination, RecipeDetailsDestination(77))
	}

	@Test
	fun `start invokes pending link delivery when user signs in`() = runTest {
		var deliveryCount = 0
		val authenticationRepository = FakeAuthenticationRepository()
		mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			onDeliverPendingIncomingLink = { deliveryCount++ },
			coroutineScope = testViewModelScope(),
		).start()
		deliveryCount shouldBe 1
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)

		deliveryCount shouldBe 2
	}

	@Test
	fun `start sets analytics user id when user signs in`() = runTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val authenticationRepository = FakeAuthenticationRepository()
		mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			analyticsRepository = analyticsRepository,
			coroutineScope = testViewModelScope(),
		).start()
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)

		analyticsRepository.lastUserId shouldBe sampleUser.id
	}

	@Test
	fun `start clears post login state on sign out`() = runTest {
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			coroutineScope = testViewModelScope(),
		)
		viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.RECIPE_SEARCH_FILTERS)
		viewModel.start()
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)
		viewModel.takePendingOpenSearchFilters() shouldBe true

		authenticationRepository.signOut()

		viewModel.takePendingOpenSearchFilters() shouldBe false
		viewModel.takePostLoginOriginAfterSignIn() shouldBe null
	}
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.testViewModelScope(): CoroutineScope =
	CoroutineScope(SupervisorJob() + UnconfinedTestDispatcher(testScheduler))

private fun incomingLinksRepository(links: MutableSharedFlow<PurecipesLink>): IncomingLinkRepository =
	object : IncomingLinkRepository {
		override fun observeLinks() = links

		override fun deliver(url: String) = Unit
	}
