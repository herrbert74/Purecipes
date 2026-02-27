package com.purecipes.feature.search.repository

import com.github.michaelbull.result.Result

typealias SearchOutcome<T> = Result<T, SearchFailure>
