package chatbot.dsl

import chatbot.api.Keyboard

internal class KeyboardBuilder : BaseKeyboardBuilder() {
    class ButtonBuilder : BaseButtonBuilder {
        private val row: MutableList<Keyboard.Button> = mutableListOf()

        fun getRow() = row

        override fun button(text: String) {
            addButton(text)
        }

        private fun addButton(text: String) {
            row.add(Keyboard.Button(text))
        }

        override operator fun String.unaryMinus(): String {
            addButton(this)
            return this
        }
    }

    override fun row(function: BaseButtonBuilder.() -> Unit) {
        val builder = ButtonBuilder()
        builder.function()
        keyboard.add(builder.getRow())
    }
}
