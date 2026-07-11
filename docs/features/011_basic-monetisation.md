# Basic Monetisation

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview
Implement a freemium monetisation model using RevenueCat for subscription management, with ads for free users and the foundation for premium features. This establishes the revenue infrastructure while keeping core cooking features accessible.

## User Story
As a business, I want to implement a sustainable monetisation strategy that allows free users to access basic features with ads, while providing premium features for paying users and exploring additional revenue streams like sponsorships.

## Core Functionality
- **Freemium Model**: Free tier with ads, premium tier without ads
- **RevenueCat Integration**: Unified subscription management across platforms
- **Ad Implementation**: Non-intrusive ads for free users
- **Paywall Foundation**: Infrastructure for future premium features
- **Sponsorship Ready**: Framework for brand partnerships

## Technical Implementation

### Primary Solution: RevenueCat + AdMob
- **Subscription Manager**: RevenueCat for cross-platform subscription handling
- **Ad Service**: Google AdMob for mobile ads
- **Paywall System**: Custom UI for subscription offers
- **Analytics**: Revenue tracking and user conversion metrics

### Architecture Components

#### 1. Subscription Manager
```kotlin
// Common interface
expect class SubscriptionManager() {
    suspend fun initialize()
    suspend fun getSubscriptionStatus(): SubscriptionStatus
    suspend fun purchasePremium(): PurchaseResult
    suspend fun restorePurchases(): RestoreResult
    val subscriptionState: Flow<SubscriptionState>
}

enum class SubscriptionStatus {
    FREE, PREMIUM, EXPIRED, UNKNOWN
}

data class SubscriptionState(
    val status: SubscriptionStatus,
    val isActive: Boolean,
    val expiryDate: Date?,
    val trialActive: Boolean
)
```

#### 2. Ad Manager
```kotlin
// Common interface
expect class AdManager() {
    suspend fun initialize()
    suspend fun loadBannerAd(): BannerAd
    suspend fun loadInterstitialAd(): InterstitialAd
    suspend fun loadRewardedAd(): RewardedAd
    fun shouldShowAds(): Boolean
}

data class AdConfig(
    val bannerAdUnitId: String,
    val interstitialAdUnitId: String,
    val rewardedAdUnitId: String,
    val testMode: Boolean = false
)
```

#### 3. Paywall System
```kotlin
data class PremiumFeature(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val available: Boolean = false
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val price: String,
    val duration: String,
    val features: List<PremiumFeature>,
    val trialDays: Int? = null
)
```

### Platform-Specific Implementation

#### Android
- **RevenueCat SDK**: Android implementation with Google Play billing
- **AdMob Integration**: Banner and interstitial ads
- **Purchase Flow**: Google Play subscription management
- **Ad Placement**: Recipe list, between cooking steps

#### iOS
- **RevenueCat SDK**: iOS implementation with App Store billing
- **AdMob Integration**: Native iOS ad formats
- **Purchase Flow**: App Store subscription management
- **Ad Placement**: Recipe browsing, cooking completion

#### Web (Wasm)
- **Stripe Integration**: Web subscription payments
- **AdSense Integration**: Web ad implementation
- **Purchase Flow**: Stripe checkout integration
- **Ad Placement**: Sidebar, between recipe sections

### Monetisation Strategy

#### Freemium Tiers
**Free Tier**
- All basic cooking features
- Recipe search and browsing (dietary, cuisine, meal type, time, difficulty, method)
- Pantry matching and excluded ingredients
- Step-by-step cooking instructions
- Banner ads in search, favorites, and recipe details
- Occasional pre-cook interstitial ads

**Premium Tier**
- Ad-free experience
- Key ingredients filter
- Calorie range and nutrition filters
- Future premium features

#### Backend enforcement
- RevenueCat webhook (`POST /webhooks/revenuecat`) updates `app_users.is_premium`
- Search strips calorie/nutrition filters and key ingredients for free users

#### Ad Implementation Strategy
- **Non-Intrusive**: Inline banners in search/favorites lists; bottom banner on recipe details
- **Contextual**: Food and cooking-related ads
- **Frequency Control**: Pre-cook interstitial at ~50% for free users
- **User Respect**: No ads during active cooking sessions

#### Subscription Pricing
- **Monthly**: $4.99/month
- **Annual**: $39.99/year (33% discount)
- **Trial Period**: via store / RevenueCat offering configuration

### RevenueCat Configuration

#### Product Setup
```kotlin
// RevenueCat products
class Products {
    companion object {
        const val PREMIUM_MONTHLY = "premium_monthly_v1"
        const val PREMIUM_ANNUAL = "premium_annual_v1"
    }
}

// Offering configuration
data class Offering(
    val identifier: String,
    val serverDescription: String,
    val products: List<Package>
)
```

#### Subscription Logic
```kotlin
class MonetisationRepository(
    private val subscriptionManager: SubscriptionManager,
    private val adManager: AdManager
) {
    fun isPremiumUser(): Flow<Boolean> {
        return subscriptionManager.subscriptionState.map { 
            it.status == SubscriptionStatus.PREMIUM 
        }
    }
    
    suspend fun showPaywall(context: Context): PurchaseResult {
        // Show subscription options and handle purchase
    }
    
    fun shouldShowAd(): Boolean {
        return !isPremiumUser() && adManager.shouldShowAds()
    }
}
```

### Ad Strategy Implementation

#### Ad Types
- **Banner Ads**: Recipe list bottom, search results
- **Interstitial Ads**: Between recipe categories, after cooking completion
- **Rewarded Ads**: Optional ad for premium feature trial
- **Native Ads**: Integrated into recipe recommendations

#### Ad Placement Rules
- **No Cooking Interruption**: Never show ads during active cooking
- **Contextual Relevance**: Food, kitchen, and recipe-related ads
- **Frequency Limits**: Maximum 3 ads per session
- **Time-Based**: No ads within 5 minutes of last ad

### Sponsorship Framework

#### Brand Partnership Types
- **Ingredient Brands**: Featured in relevant recipes
- **Kitchen Equipment**: Product placement in cooking guides
- **Food Services**: Integration with delivery services
- **Recipe Content**: Sponsored recipe collections

#### Implementation Structure
```kotlin
data class Sponsorship(
    val brandName: String,
    val contentType: SponsorshipType,
    val duration: DateRange,
    val compensation: CompensationModel,
    val requirements: List<Requirement>
)

enum class SponsorshipType {
    RECIPE_PLACEMENT, BANNER_SPONSORSHIP, PRODUCT_FEATURE
}
```

## Success Metrics
- **Conversion Rate**: >2% free-to-premium conversion
- **Revenue Per User**: $1.50+ ARPU for free users
- **Ad Revenue**: $0.50+ CPM for banner ads
- **Retention Impact**: <5% churn increase from ads
- **User Satisfaction**: >3.5 rating for ad experience

## Dependencies
- **RevenueCat**: Subscription management and analytics
- **Google AdMob**: Mobile advertising platform
- **Stripe**: Web payment processing
- **Analytics**: Revenue tracking and user behavior
- **Backend**: Subscription validation and user management

## Privacy & Compliance

#### User Data Protection
- **Minimal Collection**: Only necessary payment and usage data
- **Secure Processing**: PCI-compliant payment handling
- **Transparent Policies**: Clear privacy policy for data usage
- **GDPR Compliance**: User consent and data rights

#### Ad Privacy
- **Limited Tracking**: Minimal user data for ad targeting
- **Contextual Ads**: Prefer content-based over behavioral targeting
- **User Control**: Options to limit ad personalization
- **Child Safety**: COPPA compliance for younger users

## Current Status: ACCEPTED
Reason: Basic monetisation is essential for app sustainability. The freemium model with RevenueCat provides a proven foundation that can scale with premium features while maintaining accessibility for core cooking functionality.

## Implementation Priority
- **Phase 0**: RevenueCat integration and basic subscription logic
- **Phase 1**: AdMob implementation and banner ads
- **Phase 2**: Paywall UI and subscription flow
- **Phase 3**: Advanced ad formats and sponsorship framework

## Testing Strategy
- **Unit Tests**: Subscription logic and ad loading
- **Integration Tests**: RevenueCat and AdMob connectivity
- **Platform Tests**: Cross-platform subscription handling
- **User Experience Tests**: Paywall conversion and ad acceptance
- **Revenue Tests**: Purchase flow and ad revenue tracking

## Future Enhancements
- **Premium Features**: Advanced recipe tools, AI features
- **Dynamic Pricing**: Regional pricing and promotions
- **Referral Program**: User acquisition incentives
- **Corporate Plans**: Family and business subscriptions
- **E-commerce Integration**: Ingredient purchasing partnerships
