package com.purecipes.shared

class Greeting {
    fun greet(): String {
        return "Hello, ${Platform().platform}!"
    }
}
