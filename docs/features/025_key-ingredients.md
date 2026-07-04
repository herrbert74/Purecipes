# Feature 025: Key Ingredients Search Filter

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview

Let signed-in users designate **key ingredients** on the Recipe filters tab so search results must contain every selected ingredient. Key ingredients are session-scoped search intent (not persisted to settings) and are sent on each `POST /recipes/search` request.

This complements pantry matching (recipes you can make with what you have) and excluded ingredients (recipes to hide).

## User Story

As a home cook, I want to require specific ingredients in my search results so I can find recipes that use what I am in the mood for, even when I am not filtering by full pantry coverage.

## Semantics

| Filter | Meaning |
|--------|---------|
| **Key ingredients** | Recipe must contain **all** selected ingredients (AND logic) |
| **Pantry** | Recipe must be fully makeable with pantry items |
| **Excluded** | Recipe must not contain any excluded ingredient |

Filter order on the backend: excluded → key ingredients → pantry coverage.

## Core Functionality

### Key ingredients section (Recipe filters tab)

Collapsible section at the top of the Recipe filters tab:

- Intro: recipes must include all selected ingredients
- Selected chips (removable)
- **From your pantry** quick picks (pantry items not yet selected)
- **Clear all** when at least one key ingredient is selected

### Persistence

- **Session-only** in `RecipeSearchViewModel` memory
- Not saved to `search_filters` JSON or a new database table
- Cleared when the user clears the section or restarts the app process

### API

`SearchRequest.keyIngredients: Set<String>` in the search request body. Applied server-side via post-filter using existing `IngredientNameMatching` rules.

## UI Layout

```
Recipe filters tab
├── Key ingredients (collapsible)
│   ├── Selected chips
│   ├── From your pantry (quick picks)
│   └── Clear all
├── Intro
└── Diet, Cuisine, Time, …
```

## Technical Implementation

| Layer | Work |
|-------|------|
| **shared/domain** | `SearchRequest.keyIngredients` |
| **backend** | `recipeContainsAllKeyIngredients()`, wired in `SearchRecipeRepository.matchesIngredientFilters` |
| **feature/search/data** | Pass `keyIngredients` through search pipeline |
| **feature/search/ui** | `KeyIngredientsSection`, `KeyIngredientChip`, ViewModel state |

## Platform Considerations

- **Android / iOS / Wasm:** shared Compose UI in `feature/search/ui`
- Filter sheet remains sign-in gated (same as other filters today)
- Key ingredients in the request body would work for guests if the sheet gate is relaxed later

## Dependencies

- Feature 002: Authentication (filter sheet)
- Feature 005: Basic recipe search
- Feature 012: Advanced recipe search and pantry filter sheet
- Feature 013: Custom pantry ingredients (add-ingredient dialog reuse)

## Out of Scope (v1)

- OR logic across key ingredients
- Persisting key ingredients to settings
- Full catalogue picker in the key ingredients section
- SQL-level ingredient filtering

## Current Status: ACCEPTED

Reason: Gives users a clear “must include” search constraint separate from pantry inventory, with minimal new infrastructure by reusing ingredient matching and add-ingredient UI.
