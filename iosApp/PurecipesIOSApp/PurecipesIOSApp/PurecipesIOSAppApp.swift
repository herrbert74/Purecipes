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

#if canImport(FirebaseCore)
import FirebaseCore
#endif

#if canImport(GoogleSignIn)
import GoogleSignIn
#endif
import Mixpanel
import UsercentricsUI

class AppDelegate: NSObject, UIApplicationDelegate {
    private var mixpanelInstance: MixpanelInstance?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        #if canImport(FirebaseCore)
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        #endif

        IosCrashlyticsSetup.shared.setup()

        #if canImport(FBSDKCoreKit)
        ApplicationDelegate.shared.application(
            application,
            didFinishLaunchingWithOptions: launchOptions
        )
        #endif

        IosNotifierInitializer.shared.initialize()

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
            }
        )

        IosAnalyticsNativeBridge.shared.registerConsentHandlers { [weak self] in
            self?.showConsentForm()
        }
    }

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
        }
    }
}
