package chatbot.dsl

import chatbot.api.Keyboard

internal class MessageBuilder : BaseMessageBuilder() {
    private var keyboard: Keyboard? = null
    private var isValidKeyboard = false

    fun getValid() = isValidKeyboard

    fun getKeyboard() = keyboard

    override fun removeKeyboard() {
        isValidKeyboard = true
        keyboard = Keyboard.Remove
    }

    private fun checkKeyboard(keyboard: MutableList<MutableList<Keyboard.Button>>): Boolean {
        return keyboard.any { it.isNotEmpty() }
    }

    override fun withKeyboard(function: BaseKeyboardBuilder.() -> Unit) {
        val builder = KeyboardBuilder()
        builder.function()
        isValidKeyboard = checkKeyboard(builder.keyboard)
        keyboard = Keyboard.Markup(builder.oneTime, builder.keyboard)
    }
}
