//
//  PurecipesIOSAppApp.swift
//  PurecipesIOSApp
//
//  Created by Zsolt Bertalan on 24/02/2026.
//

import SwiftUI
import umbrella

#if canImport(FBSDKCoreKit)
import FBSDKCoreKit
#endif

#if canImport(GoogleSignIn)
import GoogleSignIn
#endif
import Mixpanel
import UsercentricsUI

#if canImport(Usercentrics)
import Usercentrics
#endif

class AppDelegate: NSObject, UIApplicationDelegate {
    private var mixpanelInstance: MixpanelInstance?
    private var configuredUsercentricsSettingsId: String?
    private var consentUpdatedSubscription: Any?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        IosFirebaseSetup.shared.configureIfNeeded()

        IosCrashlyticsSetup.shared.setup()

        #if canImport(FBSDKCoreKit)
        ApplicationDelegate.shared.application(
            application,
            didFinishLaunchingWithOptions: launchOptions
        )
        #endif

        IosNotifierInitializer.shared.initialize()

        IosSubscriptionInitializer.shared.initialize()

        IosAdsInitializer.shared.initialize()

        installAnalyticsBridges()

        return true
    }

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        handleOpenURL(url, options: options)
    }

    func handleOpenURL(
        _ url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        if deliverPurecipesDeepLink(url) {
            return true
        }

        #if canImport(GoogleSignIn)
        if GIDSignIn.sharedInstance.handle(url) {
            return true
        }
        #endif

        #if canImport(FBSDKCoreKit)
        if ApplicationDelegate.shared.application(
            UIApplication.shared,
            open: url,
            options: options
        ) {
            return true
        }
        #endif

        return false
    }

    func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void
    ) -> Bool {
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              let url = userActivity.webpageURL else {
            return false
        }
        return deliverPurecipesDeepLink(url)
    }

    private func deliverPurecipesDeepLink(_ url: URL) -> Bool {
        let urlString = url.absoluteString
        let isPurecipesScheme = url.scheme?.lowercased() == "purecipes"
        let isPurecipesWeb = url.host?.lowercased() == "purecipes.app"
            || url.host?.lowercased() == "www.purecipes.app"
        guard isPurecipesScheme || isPurecipesWeb else {
            return false
        }
        IosIncomingLinkHandler.shared.handle(url: urlString)
        return true
    }

    private func installAnalyticsBridges() {
        IosAnalyticsNativeBridge.shared.registerMixpanelHandlers(
            initialize: { [weak self] token in
                self?.initializeMixpanel(token: token)
            },
            trackEvent: { [weak self] eventName, propertiesJSON in
                self?.trackMixpanelEvent(eventName: eventName, propertiesJSON: propertiesJSON)
            },
            setTrackingEnabled: { [weak self] isEnabled in
                self?.setMixpanelTrackingEnabled(isEnabled.boolValue)
            },
            setUserId: { [weak self] userId in
                self?.setMixpanelUserId(userId)
            },
            registerSuperProperties: { [weak self] propertiesJSON in
                self?.registerMixpanelSuperProperties(propertiesJSON: propertiesJSON)
            }
        )

        IosAnalyticsNativeBridge.shared.registerConsentHandlers(
            showConsentForm: { [weak self] in
                self?.showConsentForm()
            },
            refreshConsent: { [weak self] settingsId, onResult in
                self?.refreshConsent(settingsId: settingsId) { state in
                    _ = onResult(state)
                }
            },
            startObserving: { [weak self] settingsId, onUpdate in
                self?.startObservingConsent(settingsId: settingsId) { state in
                    _ = onUpdate(state)
                }
            }
        )
    }

    private func configureUsercentricsIfNeeded(settingsId: String) {
        #if canImport(Usercentrics)
        guard configuredUsercentricsSettingsId != settingsId else {
            return
        }
        let options = UsercentricsOptions(settingsId: settingsId)
        options.loggerLevel = UsercentricsLoggerLevel.none
        UsercentricsCore.configure(options: options)
        configuredUsercentricsSettingsId = settingsId
        #endif
    }

    private func refreshConsent(settingsId: String, onResult: @escaping (String) -> Void) {
        #if canImport(Usercentrics)
        configureUsercentricsIfNeeded(settingsId: settingsId)
        UsercentricsCore.isReady(onSuccess: { [weak self] status in
            onResult(self?.consentBridgeState(from: status) ?? "UNKNOWN")
        }, onFailure: { _ in
            onResult("UNKNOWN")
        })
        #else
        onResult("UNKNOWN")
        #endif
    }

    private func startObservingConsent(settingsId: String, onUpdate: @escaping (String) -> Void) {
        #if canImport(Usercentrics)
        configureUsercentricsIfNeeded(settingsId: settingsId)
        consentUpdatedSubscription = UsercentricsEvent.shared.onConsentUpdated { [weak self] payload in
            onUpdate(self?.consentBridgeState(from: payload) ?? "UNKNOWN")
        }
        #endif
    }

    #if canImport(Usercentrics)
    private func consentBridgeState(from status: UsercentricsReadyStatus) -> String {
        if status.shouldCollectConsent {
            return "REQUIRED"
        }
        return consentBridgeState(from: status.consents)
    }

    private func consentBridgeState(from payload: UpdatedConsentPayload) -> String {
        consentBridgeState(from: payload.consents)
    }

    private func consentBridgeState(from consents: [UsercentricsServiceConsent]) -> String {
        let nonEssentialConsents = consents.filter { !$0.isEssential }
        if nonEssentialConsents.isEmpty {
            return "OBTAINED"
        }
        if nonEssentialConsents.contains(where: { $0.status }) {
            return "OBTAINED"
        }
        return "DENIED"
    }
    #endif

    private func initializeMixpanel(token: String) {
        #if canImport(Mixpanel)
        guard mixpanelInstance == nil else {
            return
        }
        guard !token.isEmpty else {
            return
        }
        mixpanelInstance = Mixpanel.initialize(
            token: token,
            trackAutomaticEvents: true,
            optOutTrackingByDefault: false,
            useUniqueDistinctId: false,
            superProperties: nil,
            useGzipCompression: false
        )
        #endif
    }

    private func trackMixpanelEvent(eventName: String, propertiesJSON: String) {
        #if canImport(Mixpanel)
        mixpanelInstance?.track(event: eventName, properties: mixpanelProperties(from: propertiesJSON))
        #endif
    }

    private func setMixpanelTrackingEnabled(_ isEnabled: Bool) {
        #if canImport(Mixpanel)
        if isEnabled {
            mixpanelInstance?.optInTracking()
        } else {
            mixpanelInstance?.optOutTracking()
        }
        #endif
    }

    private func setMixpanelUserId(_ userId: String?) {
        #if canImport(Mixpanel)
        if let userId, !userId.isEmpty {
            mixpanelInstance?.identify(distinctId: userId, usePeople: false)
        } else {
            mixpanelInstance?.reset()
        }
        #endif
    }

    private func registerMixpanelSuperProperties(propertiesJSON: String) {
        #if canImport(Mixpanel)
        guard let properties = mixpanelProperties(from: propertiesJSON) else {
            return
        }
        mixpanelInstance?.registerSuperProperties(properties)
        #endif
    }

    private func showConsentForm() {
        #if canImport(UsercentricsUI)
        DispatchQueue.main.async {
            UsercentricsBanner().showSecondLayer { _ in
            }
        }
        #endif
    }

    private func mixpanelProperties(from json: String) -> Properties? {
        #if canImport(Mixpanel)
        guard let data = json.data(using: .utf8) else {
            return nil
        }
        guard let value = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            return nil
        }
        return value.reduce(into: Properties()) { partialResult, entry in
            if let mixpanelValue = mixpanelValue(from: entry.value) {
                partialResult[entry.key] = mixpanelValue
            }
        }
        #else
        return nil
        #endif
    }

    private func mixpanelValue(from value: Any) -> MixpanelType? {
        #if canImport(Mixpanel)
        switch value {
        case let string as String:
            return string
        case let number as NSNumber:
            return number
        case let bool as Bool:
            return bool
        case let array as [Any]:
            let converted = array.compactMap { mixpanelValue(from: $0) }
            return converted.count == array.count ? converted : nil
        case let dictionary as [String: Any]:
            let converted = dictionary.reduce(into: Properties()) { partialResult, entry in
                if let nestedValue = mixpanelValue(from: entry.value) {
                    partialResult[entry.key] = nestedValue
                }
            }
            return converted.count == dictionary.count ? converted : nil
        case _ as NSNull:
            return NSNull()
        default:
            return nil
        }
        #else
        return nil
        #endif
    }
}

@main
struct PurecipesIOSAppApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    _ = appDelegate.handleOpenURL(url)
                }
                .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { activity in
                    guard let url = activity.webpageURL else {
                        return
                    }
                    _ = appDelegate.handleOpenURL(url)
                }
        }
    }
}
