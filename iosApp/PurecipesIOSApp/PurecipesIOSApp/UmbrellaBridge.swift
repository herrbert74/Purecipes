import Foundation
import UIKit
import umbrella

enum UmbrellaBridge {
    static func recipeSearchViewController() -> UIViewController {
        RecipeSearchViewControllerFactory().make()
    }
}
