import GoogleMobileAds
import UIKit
import umbrella

final class IosAdsController: NSObject {
    static let shared = IosAdsController()

    private var interstitialAdUnitId = ""
    private var interstitialAd: InterstitialAd?
    private var interstitialCallbacks: InterstitialCallbacks?

    private override init() {
        super.init()
    }

    func registerWithKotlinBridge() {
        IosAdsNativeBridge.shared.registerHandlers(
            initialize: { [weak self] _, interstitialAdUnitId in
                self?.initialize(interstitialAdUnitId: interstitialAdUnitId)
            },
            showInterstitial: { [weak self] adUnitId, onDismissed, onImpression, onClicked in
                self?.showInterstitial(
                    adUnitId: adUnitId,
                    onDismissed: { _ = onDismissed() },
                    onImpression: { _ = onImpression() },
                    onClicked: { _ = onClicked() }
                )
            },
            createBanner: { adUnitId, onImpression, onClick in
                IosBannerAdView(
                    adUnitId: adUnitId,
                    onImpression: { _ = onImpression() },
                    onClick: { _ = onClick() }
                )
            }
        )
    }

    private func initialize(interstitialAdUnitId: String) {
        self.interstitialAdUnitId = interstitialAdUnitId
        MobileAds.shared.start()
        preloadInterstitial()
    }

    private func showInterstitial(
        adUnitId: String,
        onDismissed: @escaping () -> Void,
        onImpression: @escaping () -> Void,
        onClicked: @escaping () -> Void
    ) {
        if !adUnitId.isEmpty {
            interstitialAdUnitId = adUnitId
        }
        guard let viewController = Self.rootViewController(),
              let loadedAd = interstitialAd else {
            preloadInterstitial()
            onDismissed()
            return
        }
        interstitialCallbacks = InterstitialCallbacks(
            onDismissed: onDismissed,
            onImpression: onImpression,
            onClicked: onClicked
        )
        loadedAd.fullScreenContentDelegate = self
        interstitialAd = nil
        loadedAd.present(from: viewController)
    }

    private func preloadInterstitial() {
        guard !interstitialAdUnitId.isEmpty else {
            return
        }
        InterstitialAd.load(with: interstitialAdUnitId, request: Request()) { [weak self] ad, _ in
            self?.interstitialAd = ad
        }
    }

    static func rootViewController() -> UIViewController? {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
            .first(where: \.isKeyWindow)?
            .rootViewController
    }

    private struct InterstitialCallbacks {
        let onDismissed: () -> Void
        let onImpression: () -> Void
        let onClicked: () -> Void
    }
}

extension IosAdsController: FullScreenContentDelegate {
    func adDidRecordImpression(_ ad: FullScreenPresentingAd) {
        interstitialCallbacks?.onImpression()
    }

    func adDidRecordClick(_ ad: FullScreenPresentingAd) {
        interstitialCallbacks?.onClicked()
    }

    func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        let onDismissed = interstitialCallbacks?.onDismissed
        interstitialCallbacks = nil
        preloadInterstitial()
        onDismissed?()
    }

    func ad(
        _ ad: FullScreenPresentingAd,
        didFailToPresentFullScreenContentWithError error: Error
    ) {
        let onDismissed = interstitialCallbacks?.onDismissed
        interstitialCallbacks = nil
        preloadInterstitial()
        onDismissed?()
    }
}

final class IosBannerAdView: UIView, BannerViewDelegate {
    private let bannerView: BannerView
    private let onImpression: () -> Void
    private let onClick: () -> Void

    init(
        adUnitId: String,
        onImpression: @escaping () -> Void,
        onClick: @escaping () -> Void
    ) {
        self.bannerView = BannerView(adSize: AdSizeBanner)
        self.onImpression = onImpression
        self.onClick = onClick
        super.init(frame: .zero)
        bannerView.translatesAutoresizingMaskIntoConstraints = false
        bannerView.adUnitID = adUnitId
        bannerView.delegate = self
        addSubview(bannerView)
        NSLayoutConstraint.activate([
            bannerView.centerXAnchor.constraint(equalTo: centerXAnchor),
            bannerView.topAnchor.constraint(equalTo: topAnchor),
            bannerView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func didMoveToWindow() {
        super.didMoveToWindow()
        guard window != nil, bannerView.rootViewController == nil else {
            return
        }
        bannerView.rootViewController = IosAdsController.rootViewController()
        bannerView.load(Request())
    }

    func bannerViewDidRecordImpression(_ bannerView: BannerView) {
        onImpression()
    }

    func bannerViewDidRecordClick(_ bannerView: BannerView) {
        onClick()
    }
}
