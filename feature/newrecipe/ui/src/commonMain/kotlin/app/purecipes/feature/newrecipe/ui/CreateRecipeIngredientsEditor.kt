package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CreateRecipeIngredientsEditor {

	val rows = mutableStateListOf(IngredientRowInput())

	var showPasteDialog by mutableStateOf(false)

	var pasteText by mutableStateOf("")

	fun onRowChange(index: Int, row: IngredientRowInput) {
		if (index !in rows.indices) {
			return
		}
		rows[index] = row
	}

	fun addRow() {
		rows.add(IngredientRowInput())
	}

	fun removeRow(index: Int) {
		if (rows.size == 1) {
			rows[0] = IngredientRowInput()
		} else if (index in rows.indices) {
			rows.removeAt(index)
		}
	}

	fun addAlternative(index: Int) {
		if (index !in rows.indices) {
			return
		}
		val row = rows[index]
		rows[index] = row.copy(
			alternatives = row.alternatives + IngredientPartInput(),
		)
	}

	fun removeAlternative(rowIndex: Int, alternativeIndex: Int) {
		if (rowIndex !in rows.indices) {
			return
		}
		val row = rows[rowIndex]
		if (alternativeIndex !in row.alternatives.indices) {
			return
		}
		rows[rowIndex] = row.copy(
			alternatives = row.alternatives.filterIndexed { index, _ -> index != alternativeIndex },
		)
	}

	fun pasteLines(text: String) {
		rows.clear()
		rows.addAll(IngredientRowComposer.fromPasteText(text))
	}

	fun openPasteDialog() {
		showPasteDialog = true
	}

	fun onPasteTextChange(value: String) {
		pasteText = value
	}

	fun dismissPasteDialog() {
		showPasteDialog = false
		pasteText = ""
	}

	fun reset() {
		rows.clear()
		rows.add(IngredientRowInput())
		dismissPasteDialog()
	}

	fun replaceFromEditableLines(lines: List<String>) {
		rows.clear()
		rows.addAll(IngredientRowComposer.fromEditableLines(lines))
	}

	fun toLines(): List<String> = IngredientRowComposer.toLines(rows)
}
