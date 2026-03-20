# Skill: Feature Layering

Use this skill when you add or refactor feature module data flow.

## Goal

Keep feature modules aligned to the repository architecture used in this repository:
- data sources own low-level data transformations
- repositories expose feature-facing data access contracts
- use cases mediate between repositories and presentation

## Apply This Skill When

- adding a new feature module
- introducing a new repository function
- refactoring presentation code that currently depends on repositories directly
- moving mapping, `Outcome`, or threading concerns out of UI-facing code

## Expected Structure

For each feature flow, prefer this dependency chain:
- `ui -> domain use case -> domain repository -> data accessor -> data source -> api/db/service`

## Implementation Rules

- Keep repository interfaces in feature `domain`.
- Keep repository implementations in feature `data`.
- Add at least one use case per repository flow, even if it currently delegates 1:1.
- Put DTO, DBO, and result-wrapper transformations into data sources.
- Keep shared business entities in `shared/domain`.
- Do not bypass use cases from presentation unless the architecture rule is explicitly changed.

## Checklist

- added or updated data source interface and implementation
- repository implementation delegates to data source
- use case added or updated in domain
- presentation depends on use case, not repository
- DI graph provides the new layers
- focused tests still cover the affected seam points
