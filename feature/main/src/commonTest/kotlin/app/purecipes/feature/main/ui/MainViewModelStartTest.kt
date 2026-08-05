package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import app.purecipes.shared.ui.navigation.PostLoginAction
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelStartTest {

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	private val sampleUser = fakeAuthUser(
		displayName = "Taylor",
		provider = AuthProvider.GOOGLE,
	)

	@Test
	fun `start refreshes consent on startup`() = runUnconfinedViewModelTest {
		val consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED)
		mainViewModelForTest(
			consentRepository = consentRepository,
		).start()

		consentRepository.refreshConsentCalled shouldBe true
	}

	@Test
	fun `start observes sign in and resumes post login filters navigation`() = runUnconfinedViewModelTest {
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
		)
		viewModel.requestLoginForPostLoginAction(PostLoginAction.OpenSearchFilters)
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
		viewModel.peekBackStack() shouldBe listOf(SearchDestination(openFiltersOnStart = true))
	}

	@Test
	fun `start resumes cookbook share import after sign in`() = runUnconfinedViewModelTest {
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
		)
		viewModel.requestLoginForPostLoginAction(PostLoginAction.ImportCookbookShare(sampleShareToken))
		viewModel.start()
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)

		viewModel.peekBackStack() shouldBe listOf(
			FavoritesDestination(cookbookShareToken = sampleShareToken),
		)
	}

	@Test
	fun `start routes unsigned cookbook share to account login then favorites with token`() =
		runUnconfinedViewModelTest {
			val links = MutableSharedFlow<PurecipesLink>(extraBufferCapacity = 1)
			val authenticationRepository = FakeAuthenticationRepository()
			val viewModel = mainViewModelForTest(
				authenticationRepository = authenticationRepository,
				incomingLinkRepository = incomingLinksRepository(links),
			)
			viewModel.start()
			links.emit(PurecipesLink.CookbookShare(sampleShareToken))

			viewModel.peekBackStack() shouldBe listOf(AccountDestination)
			authenticationRepository.signInWithGoogle(
				GoogleAuthenticationProfile(
					idToken = sampleUser.id,
					email = sampleUser.email,
					displayName = sampleUser.displayName,
					profileImageUrl = sampleUser.profileImageUrl,
				),
			)

			viewModel.peekBackStack() shouldBe listOf(
				FavoritesDestination(cookbookShareToken = sampleShareToken),
			)
		}

	@Test
	fun `start delivers recipe link when signed in`() = runUnconfinedViewModelTest {
		val links = MutableSharedFlow<PurecipesLink>(extraBufferCapacity = 1)
		val viewModel = mainViewModelForTest(
			authenticationRepository = FakeAuthenticationRepository(AuthenticationState.SignedIn(sampleUser)),
			incomingLinkRepository = incomingLinksRepository(links),
		)
		viewModel.start()
		links.emit(PurecipesLink.Recipe(77))

		viewModel.peekBackStack() shouldBe listOf(
			SearchDestination(),
			RecipeDetailsDestination(77, origin = AnalyticsOrigin.DEEP_LINK.value),
		)
	}

	@Test
	fun `start invokes pending link delivery when user signs in`() = runUnconfinedViewModelTest {
		var deliveryCount = 0
		val authenticationRepository = FakeAuthenticationRepository()
		mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			onDeliverPendingIncomingLink = { deliveryCount++ },
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
	fun `start sets analytics user id when user signs in`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val authenticationRepository = FakeAuthenticationRepository()
		mainViewModelForTest(
			authenticationRepository = authenticationRepository,
			analyticsRepository = analyticsRepository,
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
	fun `start clears signed in state when session validation signs out`() = runUnconfinedViewModelTest {
		val authenticationRepository = FakeAuthenticationRepository(
			initialState = AuthenticationState.SignedIn(sampleUser),
			signOutOnValidateSession = true,
		)
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
		)

		viewModel.start()

		authenticationRepository.validateSessionCallCount shouldBe 1
		viewModel.authenticationState shouldBe AuthenticationState.SignedOut
	}

	@Test
	fun `start clears post login state on sign out`() = runUnconfinedViewModelTest {
		val authenticationRepository = FakeAuthenticationRepository()
		val viewModel = mainViewModelForTest(
			authenticationRepository = authenticationRepository,
		)
		viewModel.requestLoginForPostLoginAction(PostLoginAction.OpenSearchFilters)
		viewModel.start()
		authenticationRepository.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = sampleUser.id,
				email = sampleUser.email,
				displayName = sampleUser.displayName,
				profileImageUrl = sampleUser.profileImageUrl,
			),
		)
		viewModel.peekBackStack() shouldBe listOf(SearchDestination(openFiltersOnStart = true))

		authenticationRepository.signOut()

		viewModel.takePendingPostLoginAction() shouldBe null
	}
}

private fun incomingLinksRepository(links: MutableSharedFlow<PurecipesLink>): IncomingLinkRepository =
	object : IncomingLinkRepository {
		override fun observeLinks() = links

		override fun deliver(url: String) = Unit
	}
