# Measurement Systems

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview
Implement comprehensive measurement system support allowing users to choose between imperial and metric systems, with intelligent default selection based on location and flexible handling of recipe format conversion.

## User Story
As a user, I want to view recipes in my preferred measurement system (imperial or metric) so I can cook comfortably with measurements I understand, while having control over how to handle recipes in different formats.

## Core Functionality
- **System Selection**: Choose between imperial and metric measurement systems
- **Location-Based Defaults**: Automatic default selection (imperial for US users, metric for others)
- **Recipe Format Handling**: Three options for recipes not in user's preferred format:
  - Keep as original format
  - Filter out from search results
  - Convert to user's preferred format
- **Smart Notifications**: One-time notification in recipe pages for format mismatch
- **Preference Persistence**: Remember user choices to avoid repeated notifications

## Technical Implementation

### User Preference Management
```kotlin
// Measurement system enum
enum class MeasurementSystem {
    IMPERIAL,    // cups, ounces, pounds, Fahrenheit
    METRIC       // grams, liters, kilograms, Celsius
}

// Recipe format handling options
enum class RecipeFormatHandling {
    KEEP_AS_IS,      // Display in original format
    FILTER_OUT,      // Hide from search results
    CONVERT_TO_USER  // Convert to user's preferred system
}

// User preferences data class
data class MeasurementPreferences(
    val preferredSystem: MeasurementSystem,
    val formatHandling: RecipeFormatHandling,
    val location: String,
    val hasSeenNotification: Set<String> = emptySet() // Recipe IDs where notification was shown
)
```

### Location Detection
```kotlin
// Location-based default selection
class LocationDetector {
    fun detectUserLocation(): String {
        // Use device locale, GPS, or IP-based detection
        // Return country code (e.g., "US", "GB", "CA")
    }
    
    fun getDefaultMeasurementSystem(countryCode: String): MeasurementSystem {
        return if (countryCode == "US") MeasurementSystem.IMPERIAL 
        else MeasurementSystem.METRIC
    }
}
```

### Measurement Conversion Engine
```kotlin
// Conversion service
interface MeasurementConverter {
    fun convertWeight(value: Double, from: MeasurementSystem, to: MeasurementSystem): Double
    fun convertVolume(value: Double, from: MeasurementSystem, to: MeasurementSystem): Double
    fun convertTemperature(value: Double, from: MeasurementSystem, to: MeasurementSystem): Double
    fun convertIngredient(ingredient: Ingredient, to: MeasurementSystem): Ingredient
}

// Ingredient data model with conversion support
data class Ingredient(
    val name: String,
    val amount: Double,
    val unit: String,
    val originalSystem: MeasurementSystem,
    val convertedAmount: Double? = null,
    val convertedUnit: String? = null
)
```

### Recipe Processing Pipeline
```kotlin
// Recipe processing service
class RecipeProcessingService {
    fun processRecipeForUser(
        recipe: Recipe, 
        userPreferences: MeasurementPreferences
    ): ProcessedRecipe {
        
        return when (userPreferences.formatHandling) {
            RecipeFormatHandling.KEEP_AS_IS -> 
                recipe.copy(showFormatNotification = shouldShowNotification(recipe, userPreferences))
            
            RecipeFormatHandling.FILTER_OUT -> 
                if (recipe.measurementSystem != userPreferences.preferredSystem) 
                    null else recipe
                
            RecipeFormatHandling.CONVERT_TO_USER -> 
                convertRecipeMeasurements(recipe, userPreferences.preferredSystem)
        }
    }
    
    private fun shouldShowNotification(
        recipe: Recipe, 
        preferences: MeasurementPreferences
    ): Boolean {
        return recipe.measurementSystem != preferences.preferredSystem && 
               !preferences.hasSeenNotification.contains(recipe.id)
    }
}
```

### Database Schema Extensions
```sql
-- User measurement preferences table
CREATE TABLE user_measurement_preferences (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE,
    preferred_system VARCHAR(20) NOT NULL, -- 'IMPERIAL' or 'METRIC'
    format_handling VARCHAR(20) NOT NULL, -- 'KEEP_AS_IS', 'FILTER_OUT', 'CONVERT_TO_USER'
    detected_location VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipe measurement system tracking
ALTER TABLE recipes ADD COLUMN measurement_system VARCHAR(20) NOT NULL DEFAULT 'METRIC';
ALTER TABLE recipes ADD COLUMN original_measurements JSONB; -- Store original measurements

-- Notification tracking
CREATE TABLE measurement_notifications_shown (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    recipe_id VARCHAR(255),
    shown_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id)
);
```

## User Experience Flow

### Initial Setup
1. User opens app for first time
2. System detects location via device locale/IP
3. Sets default measurement system (imperial for US, metric otherwise)
4. Sets default format handling to "KEEP_AS_IS"
5. User can change preferences in settings anytime

### Recipe Search & Browsing
1. User searches or browses recipes
2. System applies user's format handling preference:
   - **Keep as is**: All recipes shown, format mismatch noted
   - **Filter out**: Only recipes in user's preferred system shown
   - **Convert**: All recipes converted to user's preferred system

### Recipe View with Format Mismatch
1. User opens recipe in different measurement system
2. If "KEEP_AS_IS" and notification not shown before:
   - Show modal/notification explaining the format difference
   - Present three options:
     - "Keep as is" (dismiss notification permanently for this recipe)
     - "Convert to metric/imperial" (change preference for this recipe)
     - "Update my preferences" (go to settings)
3. User's choice is remembered for future visits

### Settings Interface
```kotlin
// Settings screen components
@Composable
fun MeasurementSystemSettings(
    preferences: MeasurementPreferences,
    onPreferencesChanged: (MeasurementPreferences) -> Unit
) {
    Column {
        // System selection
        RadioGroup(
            options = listOf("Imperial (US)", "Metric"),
            selected = preferences.preferredSystem,
            onSelectionChanged = { system -> 
                onPreferencesChanged(preferences.copy(preferredSystem = system))
            }
        )
        
        // Format handling options
        RadioGroup(
            title = "Recipes in other measurement systems:",
            options = listOf(
                "Keep as they are",
                "Filter out from search",
                "Convert to my system"
            ),
            selected = preferences.formatHandling,
            onSelectionChanged = { handling ->
                onPreferencesChanged(preferences.copy(formatHandling = handling))
            }
        )
        
        // Location display
        Text("Detected location: ${preferences.location}")
        Button("Reset to location-based default") {
            val defaultSystem = LocationDetector.getDefaultMeasurementSystem(preferences.location)
            onPreferencesChanged(preferences.copy(preferredSystem = defaultSystem))
        }
    }
}
```

## Platform-Specific Implementation

### Android
- **Locale Detection**: Use `Locale.getDefault()` for location detection
- **Settings Storage**: SharedPreferences for preferences
- **Notifications**: Material Design dialogs for format notifications
- **Conversion**: Local conversion algorithms for offline use

### iOS
- **Locale Detection**: Use `Locale.current` for location detection
- **Settings Storage**: UserDefaults for preferences
- **Notifications**: UIAlertController for format notifications
- **Conversion**: Same conversion engine shared across platforms

### Web
- **Locale Detection**: Use `navigator.language` and IP geolocation
- **Settings Storage**: LocalStorage for preferences
- **Notifications**: Modal dialogs or toast notifications
- **Conversion**: Client-side conversion with fallback to server

## Conversion Rules & Accuracy

### Weight Conversions
- 1 ounce = 28.35 grams
- 1 pound = 453.59 grams
- 1 kilogram = 2.20462 pounds

### Volume Conversions
- 1 cup = 236.588 mL
- 1 tablespoon = 14.787 mL
- 1 teaspoon = 4.929 mL
- 1 liter = 4.22675 cups

### Temperature Conversions
- Celsius to Fahrenheit: (C × 9/5) + 32
- Fahrenheit to Celsius: (F - 32) × 5/9

### Ingredient-Specific Considerations
- **Flour**: 1 cup ≈ 120g (varies by type)
- **Sugar**: 1 cup ≈ 200g
- **Butter**: 1 cup ≈ 227g
- **Liquids**: Use standard volume conversions

## Success Metrics
- **Feature Adoption**: >80% of users set measurement preferences
- **User Satisfaction**: >4.5 rating for measurement experience
- **Conversion Accuracy**: >95% user satisfaction with converted recipes
- **Notification Effectiveness**: <10% users change preferences after notification
- **Search Filtering**: Users who filter out other systems have higher recipe completion rates

## Dependencies
- **Location Detection**: Device locale/IP geolocation services
- **Conversion Engine**: Custom measurement conversion algorithms
- **User Preferences**: Cross-platform preference storage
- **Database**: Extended schema for measurement tracking
- **UI Components**: Settings screens and notification dialogs

## Privacy & Compliance
- **Location Data**: Minimal location detection (country level only)
- **User Preferences**: Stored securely with user consent
- **Data Minimization**: Only necessary preference data collected
- **GDPR Compliance**: Clear consent for location detection

## Potential Challenges
- **Conversion Accuracy**: Ensuring precise ingredient conversions
- **User Education**: Helping users understand conversion options
- **Recipe Quality**: Maintaining recipe quality after conversion
- **Performance**: Fast conversion without impacting app performance
- **Edge Cases**: Handling unusual measurements and units

## Future Enhancements
- **Advanced Conversions**: AI-powered ingredient-specific conversions
- **Mixed Systems**: Support for users who prefer mixed systems
- **Custom Units**: User-defined measurement units
- **Recipe Annotations**: Allow users to add conversion notes
- **Smart Suggestions**: Suggest conversions based on cooking context

## Current Status: ACCEPTED
Reason: Measurement system support is essential for international user adoption and user experience. This feature addresses a fundamental pain point for users cooking with recipes from different regions, making Purecipes truly global and user-friendly. The location-based defaults and flexible handling options provide a smooth, intuitive experience while maintaining user control.
