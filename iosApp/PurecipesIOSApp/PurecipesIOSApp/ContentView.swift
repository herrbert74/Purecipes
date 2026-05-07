//
//  ContentView.swift
//  PurecipesIOSApp
//
//  Created by Zsolt Bertalan on 24/02/2026.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        RecipeSearchContainerView()
            .ignoresSafeArea()
    }
}

private struct RecipeSearchContainerView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        UmbrellaBridge.recipeSearchViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

#Preview {
    ContentView()
}
