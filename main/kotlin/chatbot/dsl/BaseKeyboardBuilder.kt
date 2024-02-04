package chatbot.dsl

import chatbot.api.Keyboard

abstract class BaseKeyboardBuilder {
    var keyboard: MutableList<MutableList<Keyboard.Button>> = mutableListOf()
    var oneTime = false

    open fun row(function: BaseButtonBuilder.() -> Unit) {}
}
