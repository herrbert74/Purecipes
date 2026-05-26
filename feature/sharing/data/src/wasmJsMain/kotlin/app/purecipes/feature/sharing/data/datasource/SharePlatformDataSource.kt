package app.purecipes.feature.sharing.data.datasource

internal actual class SharePlatformDataSource actual constructor() {

	actual fun shareText(text: String, title: String?) {
		if (!wasmTryNavigatorShare(text, title)) {
			wasmCopyTextToClipboard(text)
		}
	}
}

@JsFun(
	"""
	(text, title) => {
		if (typeof navigator === 'undefined' || typeof navigator.share !== 'function') {
			return false;
		}
		try {
			navigator.share({ text: text, title: title || undefined });
			return true;
		} catch (e) {
			return false;
		}
	}
""",
)
private external fun wasmTryNavigatorShare(text: String, title: String?): Boolean

@JsFun(
	"""
	(text) => {
		if (typeof navigator !== 'undefined' && navigator.clipboard && navigator.clipboard.writeText) {
			navigator.clipboard.writeText(text);
			return;
		}
		if (typeof document === 'undefined') {
			return;
		}
		const area = document.createElement('textarea');
		area.value = text;
		document.body.appendChild(area);
		area.select();
		document.execCommand('copy');
		document.body.removeChild(area);
	}
""",
)
private external fun wasmCopyTextToClipboard(text: String)
