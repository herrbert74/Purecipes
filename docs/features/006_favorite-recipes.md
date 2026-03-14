# Favorite Recipes

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview
Implement a comprehensive favorite recipes system that allows users to save, organize, and quickly access their most-loved recipes with personal tagging and collections management.

## User Story
As a user, I want to save my favorite recipes and organize them into personal collections so I can quickly find and cook the recipes I love most.

## Core Functionality
- **Favorite Toggle**: One-click favorite/unfavorite for any recipe
- **Personal Collections**: Create custom collections to organize favorites
- **Smart Tagging**: Add personal tags to favorite recipes
- **Quick Access**: Dedicated favorites section with fast loading
- **Offline Sync**: Favorite recipes available offline
- **Search & Filter**: Search within favorites with advanced filters

## Technical Implementation

### Database Schema Extensions
```sql
-- Favorites table
CREATE TABLE favorites (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id)
);

-- Collections table
CREATE TABLE collections (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(7), -- hex color for UI
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipe collections junction table
CREATE TABLE recipe_collections (
    id SERIAL PRIMARY KEY,
    collection_id INTEGER REFERENCES collections(id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(collection_id, recipe_id)
);

-- Personal tags table
CREATE TABLE personal_tags (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id, tag)
);
```

### KMP Shared Module
```kotlin
// Favorite recipe data model
data class FavoriteRecipe(
    val recipe: Recipe,
    val isFavorite: Boolean,
    val collections: List<Collection>,
    val personalTags: List<String>,
    val favoritedAt: Instant
)

// Collection data model
data class Collection(
    val id: String,
    val name: String,
    val description: String?,
    val color: String,
    val recipeCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)

// Favorites repository interface
interface FavoritesRepository {
    suspend fun toggleFavorite(recipeId: String): Result<Boolean>
    suspend fun getFavoriteRecipes(): Result<List<FavoriteRecipe>>
    suspend fun addToCollection(recipeId: String, collectionId: String): Result<Unit>
    suspend fun removeFromCollection(recipeId: String, collectionId: String): Result<Unit>
    suspend fun createCollection(name: String, description: String?, color: String): Result<Collection>
    suspend fun addPersonalTag(recipeId: String, tag: String): Result<Unit>
    suspend fun removePersonalTag(recipeId: String, tag: String): Result<Unit>
    suspend fun searchFavorites(query: String, tags: List<String>): Result<List<FavoriteRecipe>>
}
```

### User Interface Components

#### Favorite Button
- Heart icon that toggles between filled/unfilled states
- Animated feedback when favoriting/unfavoriting
- Quick access from recipe cards and detail views
- Batch operations for multiple recipes

#### Collections Management
- Create, edit, and delete personal collections
- Drag-and-drop recipe organization
- Color-coded collections for visual organization
- Collection sharing with other users (future feature)

#### Favorites Dashboard
- Grid/list view toggle for favorite recipes
- Smart filtering by tags, collections, and recipe attributes
- Recently favorited recipes section
- Quick cooking mode for favorites

## User Experience Flow

### Adding to Favorites
1. User browsing recipes sees heart icon on each recipe
2. Tap heart to add to favorites with animation feedback
3. Option to add to specific collection immediately
4. Confirmation toast with quick collection access

### Organizing Favorites
1. Access favorites section from main navigation
2. View all favorites in grid or list format
3. Create new collections with custom names and colors
4. Drag recipes to collections or use bulk actions
5. Add personal tags for better organization

### Finding Favorites
1. Quick search within favorites only
2. Filter by collections, tags, or recipe attributes
3. Sort by favorited date, last cooked, or custom order
4. Offline access to all favorite recipes

## Platform-Specific Features

### Android
- **Widget**: Home screen widget for quick favorite access
- **Notifications**: Reminders to cook favorite recipes
- **Share Integration**: Share favorite recipes to other apps

### iOS
- **Siri Shortcuts**: "Cook my favorite pasta recipe"
- **Apple Watch**: Quick access to favorites on Watch
- **Widgets**: iOS 14+ home screen widgets

### Web
- **Browser Extension**: Quick favorite from any website
- **Keyboard Shortcuts**: Fast navigation and actions
- **Print-Friendly**: Print favorite recipes with notes

## Performance Considerations

### Caching Strategy
- **Local Storage**: Favorite recipes cached for offline access
- **Smart Sync**: Incremental sync of favorite changes
- **Background Updates**: Refresh favorite recipe data in background
- **Memory Management**: Efficient loading of large favorite lists

### Database Optimization
- **Indexing**: Proper indexes on user_id, recipe_id, and collection_id
- **Query Optimization**: Efficient queries for favorite operations
- **Batch Operations**: Bulk operations for multiple favorites
- **Connection Pooling**: Optimized database connections

## Success Metrics
- **Feature Adoption**: >60% of active users use favorites
- **Engagement**: 3x more recipe views for favorited recipes
- **Retention**: Users with favorites have 2x higher retention
- **Organization**: Average of 3 collections per active user
- **Usage Frequency**: Favorites accessed 5+ times per week

## Dependencies
- **Database**: PostgreSQL with extensions for favorites
- **Backend API**: RESTful endpoints for favorites operations
- **Authentication**: User authentication required
- **Storage**: Local caching for offline access
- **UI Components**: Cross-platform UI components

## Privacy and Data
- **User Privacy**: Favorites are private by default
- **Data Export**: Users can export their favorites data
- **GDPR Compliance**: Right to delete favorite data
- **Analytics**: Anonymous usage statistics only

## Future Enhancements
- **Social Sharing**: Share collections with friends and family
- **AI Recommendations**: Smart suggestions based on favorites
- **Meal Planning**: Integrate favorites into meal planning
- **Cooking History**: Track when favorites were last cooked
- **Recipe Notes**: Add personal notes to favorite recipes

## Current Status: ACCEPTED
Reason: Favorite recipes is a core engagement feature that will significantly improve user retention and satisfaction. The personal organization capabilities will make Purecipes more valuable as a daily cooking tool.

## Implementation Priority
- **Phase 1**: Core favorites functionality with basic collections
- **Phase 2**: Advanced organization features and personal tags
- **Phase 3**: Social features and sharing capabilities
- **Phase 4**: AI-powered recommendations and insights

## Testing Strategy
- **Unit Tests**: Repository and business logic validation
- **Integration Tests**: End-to-end favorites workflow
- **Performance Tests**: Large favorites list handling
- **Usability Tests**: User experience and interface testing
- **Sync Tests**: Cross-platform synchronization validation
