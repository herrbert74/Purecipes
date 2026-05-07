package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val token = purecipesConfig.mixpanelProjectToken().orEmpty()
	private var isTrackingEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	init {
		mixpanelEnsureInitialized(token)
		setTrackingEnabled(isTrackingEnabled)
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled || token.isBlank()) {
			return
		}
		mixpanelTrackEvent(eventName, properties.toAnalyticsJson())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		isTrackingEnabled = isEnabled
		if (token.isBlank()) {
			return
		}
		if (isEnabled) {
			mixpanelOptInTracking()
		} else {
			mixpanelOptOutTracking()
		}
	}

	actual override fun setUserId(userId: String?) {
		if (token.isBlank()) {
			return
		}
		if (userId.isNullOrBlank()) {
			mixpanelReset()
		} else {
			mixpanelIdentify(userId)
		}
	}
}

@JsFun(
	"""
	(token) => {
		if (!token || !globalThis.document || globalThis.__purecipesMixpanelInit) {
			return;
		}
		(function (f, b) {
			if (!b.__SV) {
				var e, g, i, h;
				window.mixpanel = b;
				b._i = [];
				b.init = function (e, f, c) {
					function g(a, d) {
						var b = d.split('.');
						2 == b.length && ((a = a[b[0]]), (d = b[1]));
						a[d] = function () {
							a.push([d].concat(Array.prototype.slice.call(arguments, 0)));
						};
					}
					var a = b;
					'undefined' !== typeof c ? (a = b[c] = []) : (c = 'mixpanel');
					a.people = a.people || [];
					a.toString = function (a) {
						var d = 'mixpanel';
						'mixpanel' !== c && (d += '.' + c);
						a || (d += ' (stub)');
						return d;
					};
					a.people.toString = function () { return a.toString(1) + '.people (stub)'; };
					i = ('disable time_event track track_pageview track_links track_forms track_with_groups add_group set_group remove_group register register_once alias unregister identify name_tag set_config reset opt_in_tracking opt_out_tracking has_opted_in_tracking has_opted_out_tracking clear_opt_in_out_tracking start_batch_senders people.set people.set_once people.unset people.increment people.append people.union people.track_charge people.clear_charges people.delete_user people.remove').split(' ');
					for (h = 0; h < i.length; h++) g(a, i[h]);
					var j = 'set set_once union unset remove delete'.split(' ');
					b.get_group = function () {
						function a(c) {
							d[c] = function () {
								var args = arguments;
								var call2 = [c].concat(Array.prototype.slice.call(args, 0));
								b.push([e, call2]);
							};
						}
						for (var d = {}, e = ['get_group'].concat(Array.prototype.slice.call(arguments, 0)), c = 0; c < j.length; c++) a(j[c]);
						return d;
					};
					b._i.push([e, f, c]);
				};
				b.__SV = 1.2;
				e = f.createElement('script');
				e.type = 'text/javascript';
				e.async = !0;
				e.src = 'https://cdn.mxpnl.com/libs/mixpanel-2-latest.min.js';
				g = f.getElementsByTagName('script')[0];
				g.parentNode.insertBefore(e, g);
			}
		})(document, window.mixpanel || []);
		globalThis.mixpanel.init(token, { persistence: 'localStorage', autocapture: false, track_pageview: false, opt_out_tracking_by_default: true });
		globalThis.__purecipesMixpanelInit = true;
	}
"""
)
private external fun mixpanelEnsureInitialized(token: String)

@JsFun(
	"""
	(eventName, propertiesJson) => {
		if (!globalThis.mixpanel) {
			return;
		}
		globalThis.mixpanel.track(eventName, propertiesJson ? JSON.parse(propertiesJson) : {});
	}
"""
)
private external fun mixpanelTrackEvent(eventName: String, propertiesJson: String)

@JsFun(
	"""
	(userId) => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.identify(userId);
		}
	}
"""
)
private external fun mixpanelIdentify(userId: String)

@JsFun(
	"""
	() => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.reset();
		}
	}
"""
)
private external fun mixpanelReset()

@JsFun(
	"""
	() => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.opt_in_tracking();
		}
	}
"""
)
private external fun mixpanelOptInTracking()

@JsFun(
	"""
	() => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.opt_out_tracking();
		}
	}
"""
)
private external fun mixpanelOptOutTracking()
