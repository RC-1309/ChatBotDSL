package chatbot.dsl

import chatbot.api.MessageId

abstract class BaseMessageBuilder {
    var text: String = ""
    var replyTo: MessageId? = null

    open fun removeKeyboard() {}

    open fun withKeyboard(function: BaseKeyboardBuilder.() -> Unit) {}
}
