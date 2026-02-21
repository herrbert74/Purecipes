# Recipe Website Scraping

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview
Implement automated recipe data extraction from external websites using SimpleScraper for URL discovery and recipe-scrapers (https://docs.recipe-scrapers.com/) for structured data extraction, with local PostgreSQL database storage for scalability and offline access.

## User Story
As a user, I want to import recipes from my favorite cooking websites so I can build my personal recipe collection without manually entering each recipe.

## Core Functionality
- **URL Discovery**: Extract recipe URLs from websites using SimpleScraper
- **Recipe Scraping**: Parse structured recipe data using recipe-scrapers library
- **Data Storage**: Store scraped recipes in local PostgreSQL database
- **Batch Processing**: Process multiple URLs/recipes in batches
- **Error Handling**: Robust error handling for failed scrapes
- **Duplicate Detection**: Prevent duplicate recipe imports

## Technical Implementation

### URL Discovery with SimpleScraper
- **Service**: https://simplescraper.io/extracturls
- **Purpose**: Extract all recipe URLs from a given website or domain
- **Output Format**: CSV or TXT format containing URLs
- **Integration**: API calls to SimpleScraper service
- **Filtering**: Post-process URLs to identify recipe-specific links

### Recipe Data Extraction with recipe-scrapers
- **Library**: recipe-scrapers (Python)
- **Supported Sites**: 611+ popular recipe websites
- **Wild Mode**: Extended support for sites following common patterns
- **Output Format**: JSON with structured recipe data

### Recipe Data Structure (JSON Output)
```json
{
  "title": "Recipe Title",
  "ingredients": ["ingredient 1", "ingredient 2"],
  "instructions": "Step-by-step instructions",
  "total_time": 30,
  "prep_time": 15,
  "cook_time": 15,
  "yields": "4 servings",
  "image": "https://example.com/image.jpg",
  "nutrients": {
    "calories": 250,
    "protein": 15,
    "carbs": 30,
    "fat": 8
  },
  "language": "en",
  "links": {
    "canonical": "https://example.com/recipe"
  }
}
```

### Initial PostgreSQL Database Schema
```sql
-- Recipes table
CREATE TABLE recipes (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    instructions TEXT,
    total_time INTEGER,
    prep_time INTEGER,
    cook_time INTEGER,
    yields VARCHAR(100),
    image_url TEXT,
    language VARCHAR(10) DEFAULT 'en',
    source_url TEXT UNIQUE,
    scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ingredients table
CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    ingredient TEXT NOT NULL,
    order_index INTEGER
);

-- Nutrition table
CREATE TABLE nutrition (
    id SERIAL PRIMARY KEY,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    calories DECIMAL(10,2),
    protein DECIMAL(10,2),
    carbohydrates DECIMAL(10,2),
    fat DECIMAL(10,2),
    fiber DECIMAL(10,2),
    sugar DECIMAL(10,2),
    sodium DECIMAL(10,2)
);
```

## Phase 0 MVP Methodology

This feature will be developed as a Phase 0 MVP to establish the scraping methodology and database foundation, with iterative improvements throughout Phase 1.

### Kotlin-Based Scraping Pipeline
- **URL Discovery**: Use SimpleScraper API via Kotlin scripts to extract URLs from target websites
- **Recipe Filtering**: Implement Kotlin-based filtering to identify recipe-specific URLs from extracted links
- **Individual Scraping**: Call recipe-scrapers library (Python) one-by-one for each filtered recipe URL
- **Data Processing**: Convert JSON output to PostgreSQL format using Kotlin data processing scripts

### Development Phases
- **Phase 0** (February-March): Create MVP scraping methodology for a few target websites
- **Phase 1** (April-June): Improve scraping process, expand website support, and iterate database schema
- **Phase 1 Final**: Finalize database schema with first release

### Target Websites (Phase 0)
Focus on 3-5 popular recipe websites for initial MVP:
- AllRecipes
- Food Network
- Bon Appétit
- Epicurious
- Serious Eats

## Architecture Components

### 1. URL Discovery Service
```python
class URLDiscoveryService:
    def extract_urls(self, domain: str) -> List[str]:
        """Extract recipe URLs from a domain using SimpleScraper API"""
        pass
    
    def filter_recipe_urls(self, urls: List[str]) -> List[str]:
        """Filter URLs to identify recipe-specific links"""
        pass
```

### 2. Recipe Scraping Service
```python
class RecipeScrapingService:
    def scrape_recipe(self, url: str) -> Optional[RecipeData]:
        """Scrape recipe data from a URL using recipe-scrapers"""
        pass
    
    def batch_scrape(self, urls: List[str]) -> List[RecipeData]:
        """Process multiple URLs in batch"""
        pass
```

### 3. Database Service
```python
class RecipeDatabaseService:
    def save_recipe(self, recipe: RecipeData) -> int:
        """Save recipe to PostgreSQL database"""
        pass
    
    def check_duplicate(self, source_url: str) -> bool:
        """Check if recipe already exists"""
        pass
```

## Implementation Flow

### 1. Website URL Extraction
1. User provides website domain or specific URL
2. System calls SimpleScraper API to extract all URLs
3. Filter URLs to identify recipe-specific pages
4. Present filtered URLs to user for selection

### 2. Recipe Data Scraping
1. For each selected URL, fetch HTML content
2. Use recipe-scrapers to extract structured data
3. Validate extracted data completeness
4. Handle errors and retry failed attempts

### 3. Database Storage
1. Check for duplicate recipes using source URL
2. Parse and validate recipe data
3. Store recipe in PostgreSQL with related data
4. Index for search and retrieval

## Error Handling & Edge Cases

### Scraping Failures
- **Network Errors**: Retry with exponential backoff
- **Parsing Errors**: Log failed URLs for manual review
- **Rate Limiting**: Respect website rate limits
- **Blocked Requests**: Rotate user agents and use proxies

### Data Quality Issues
- **Missing Fields**: Flag incomplete recipes for review
- **Invalid Data**: Validate data types and ranges
- **Duplicate Content**: Detect near-duplicate recipes
- **Language Detection**: Identify and categorize by language

## Performance Considerations

### Scalability
- **Batch Processing**: Process URLs in configurable batches
- **Async Operations**: Use async/await for concurrent scraping
- **Rate Limiting**: Implement configurable rate limits
- **Caching**: Cache scraping results to avoid duplicate work

### Database Optimization
- **Indexing**: Proper indexes on search fields
- **Connection Pooling**: Efficient database connections
- **Bulk Inserts**: Batch database writes for performance
- **Data Archival**: Archive old or unused recipes

## Success Metrics
- **Scraping Success Rate**: >85% successful recipe extraction
- **Data Quality Score**: >90% complete recipe data
- **Processing Speed**: <5 seconds per recipe on average
- **User Adoption**: >1000 recipes imported in first month
- **Error Rate**: <5% failed scraping attempts

## Dependencies
- **Python 3.8+**: Primary development language
- **recipe-scrapers**: Recipe data extraction library
- **requests**: HTTP client for web requests
- **psycopg2**: PostgreSQL adapter for Python
- **asyncio**: Async programming support
- **BeautifulSoup4**: HTML parsing fallback
- **SimpleScraper API**: URL discovery service

## Legal & Compliance Considerations

### Website Terms of Service
- **Respect robots.txt**: Follow website crawling policies
- **Rate Limiting**: Implement reasonable request intervals
- **User Agent**: Identify crawler appropriately
- **Terms Review**: Review major sites' terms of service

### Copyright & Attribution
- **Source Attribution**: Store and display original source
- **Fair Use**: Ensure compliance with copyright laws
- **User Responsibility**: Make users aware of copyright implications
- **DMCA Compliance**: Implement takedown procedures

## Current Status: ACCEPTED
Reason: Recipe scraping is approved as a Phase 0 MVP feature to establish the methodology and database foundation. This will provide substantial value by expanding the recipe database and improving user acquisition. The iterative approach through Phase 1 allows for continuous improvement while delivering early value.

## Implementation Priority
- **Phase 0** (February-March): MVP scraping methodology with Kotlin scripts and basic database
- **Phase 1** (April-June): Improved scraping process, expanded website support, database schema iterations
- **Phase 1 Final**: Finalized database schema and optimized scraping pipeline
- **Future**: Advanced filtering, quality controls, ML-based enhancements

## Testing Strategy
- **Unit Tests**: Individual component testing
- **Integration Tests**: End-to-end scraping workflow
- **Performance Tests**: Load testing with concurrent scraping
- **Data Quality Tests**: Validation of scraped data accuracy
- **Legal Compliance Tests**: robots.txt and terms compliance

## Future Enhancements
- **AI-Powered Cleaning**: Use AI to improve data quality
- **Automatic Categorization**: ML-based recipe categorization
- **Nutrition Calculation**: Enhanced nutritional analysis
- **Image Processing**: Recipe image analysis and enhancement
- **Social Features**: Share imported recipes with community
