package com.purecipes.feature.settings.data.repository

import com.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import com.purecipes.feature.settings.data.datasource.MeasurementPreferencesDataSource
import com.purecipes.feature.settings.data.datasource.SettingsMeasurementPreferencesDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SettingsDataModule {

	@Provides
	fun provideMeasurementPreferencesDataSource(): MeasurementPreferencesDataSource {
		return SettingsMeasurementPreferencesDataSource()
	}

	@Provides
	fun provideMeasurementPreferencesRepository(
		localDataSource: MeasurementPreferencesDataSource,
	): MeasurementPreferencesRepository {
		return MeasurementPreferencesAccessor(localDataSource)
	}

	@Provides
	fun provideObserveMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): ObserveMeasurementPreferencesUseCase {
		return ObserveMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideGetMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): GetMeasurementPreferencesUseCase {
		return GetMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideSaveMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): SaveMeasurementPreferencesUseCase {
		return SaveMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideResetMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): ResetMeasurementPreferencesUseCase {
		return ResetMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideMarkMeasurementMismatchSeenUseCase(
		repository: MeasurementPreferencesRepository,
	): MarkMeasurementMismatchSeenUseCase {
		return MarkMeasurementMismatchSeenUseCase(repository)
	}

	@Provides
	fun provideProcessRecipeDetailsForMeasurementPreferencesUseCase():
		ProcessRecipeDetailsForMeasurementPreferencesUseCase {
		return ProcessRecipeDetailsForMeasurementPreferencesUseCase()
	}

	@Provides
	fun provideFilterRecipesForMeasurementPreferencesUseCase(): FilterRecipesForMeasurementPreferencesUseCase {
		return FilterRecipesForMeasurementPreferencesUseCase()
	}
}
