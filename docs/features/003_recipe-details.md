# Recipe Details

## Status: <span style="color:orange;">🟠 DRAFT</span>

## Feature Overview
A recipe detail view that provides users with all the key recipe information upfront — including title, description, a large hero image, ingredients list, and a list of steps — with a clear call-to-action to start cooking.

## User Story
As a home cook, I want to see the complete recipe details (title, description, image, ingredients, and step list) in one place so I can quickly understand what I need and start cooking.

## Core Functionality
- **Recipe Header**: Title, description, and large image (hero banner)
- **Ingredient List**: Display ingredients with quantities and optional notes
- **Step Summary**: Show an overview of the cooking steps (titles + brief descriptions)
- **Start Cooking Button**: Prominent button that launches the step-by-step cooking experience
- **Optional Actions**: Save favorite, share recipe, or adjust servings

## Technical Implementation
- **Data Model**: Recipe domain model includes `title`, `description`, `imageUrl`, `ingredients`, and `steps`
- **UI Screen**: New `RecipeDetailsScreen` composable/view across platforms
- **Navigation**: 
  - Entry point: from search/results or saved recipes list to recipe details
  - Exit point: "Start Cooking" button launches the step-by-step cooking experience (Basic Step-by-Step Cooking feature)
  - Maintain navigation stack so users can easily return to the recipe details screen
- **State Management**: Load recipe data by ID; handle loading, error, empty states
- **Cross-Platform**: Shared UI/component logic for Kotlin Multiplatform (Android, iOS, Wasm)

## Success Metrics
- Recipe details views per user
- Click-through rate on "Start Cooking" button
- Time spent on recipe details screen
- Conversion to saved favorites

## Current Status: DRAFT
Reason: Foundation for cooking workflow; enables users to view and engage with recipes before starting step-by-step guidance.
