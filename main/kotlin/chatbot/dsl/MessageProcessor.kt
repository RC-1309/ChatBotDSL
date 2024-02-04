package chatbot.dsl

import chatbot.api.*

@BanningIncorrectDSLConstructionsMarker
class MessageProcessorContext<C : ChatContext?>(
    val message: Message,
    val client: Client,
    val context: C,
    val setContext: (c: ChatContext?) -> Unit,
)

typealias MessageProcessor<C> = MessageProcessorContext<C>.() -> Unit

fun chatBot(client: Client, function: BaseSetupBuilder.() -> Unit): ChatBot {
    val builder = SetupBuilder()
    builder.function()
    return object : ChatBot {
        override fun processMessages(message: Message) {
            val messageProcessorContext = MessageProcessorContext(
                message,
                client,
                builder.contextsManager.getContext(message.chatId),
            ) { builder.contextsManager.setContext(message.chatId, it) }
            builder.getBehaviour().getFunction(this, message)(messageProcessorContext)
        }

        override val logLevel: LogLevel = builder.logLevel
    }
}

fun <T : ChatContext?> MessageProcessorContext<T>.sendMessage(chatId: ChatId, function: BaseMessageBuilder.() -> Unit) {
    val builder = MessageBuilder()
    builder.function()
    if (builder.text.isNotEmpty() || builder.getValid()) {
        client.sendMessage(chatId, builder.text, builder.getKeyboard(), builder.replyTo)
    }
}
