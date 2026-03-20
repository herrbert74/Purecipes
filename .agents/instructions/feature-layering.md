# Feature Layering

Use the feature layers consistently:
- `data source -> repository -> use case -> presentation`
- Data sources belong to feature `data` modules.
- Repository interfaces belong to feature `domain` modules, and repository implementations in `data` delegate to data sources.
- Use cases belong to feature `domain` modules and sit between repositories and presentation.

Use data sources for:
- Mapping transport or persistence models to shared domain models.
- Threading and execution-context concerns when they are not already handled by the lower API layer.
- Wrapping lower-level calls in `Outcome` or other result transformations.
- Other data-oriented transformations before repository boundaries.

Use cases for:
- Combining multiple repository calls.
- Combining repository and other use-case calls.
- Presentation-facing transformations and orchestration.
- Simple pass-through calls as the default for now, so the layer is present everywhere even when it is thin.

Naming rules:
- Data source interfaces use the `DataSource` suffix and can expose nested `Local` and `Remote` interfaces.
- Concrete data sources use `LocalDataSource` or `RemoteDataSource` suffixes.
- Repository implementations use the `Accessor` suffix.
- Use cases use the `UseCase` suffix.
