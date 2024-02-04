package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.ChatContextsManager
import chatbot.api.ChatId
import chatbot.api.LogLevel

internal class SetupBuilder : BaseSetupBuilder {
    var logLevel = LogLevel.ERROR
    var contextsManager: ChatContextsManager = object : ChatContextsManager {
        override fun getContext(chatId: ChatId): ChatContext {
            return object : ChatContext {}
        }

        override fun setContext(chatId: ChatId, newState: ChatContext?) {}
    }
    private var behaviour = BehaviourBuilder<ChatContext>(contextsManager)

    override fun use(logLevel: LogLevel) {
        this.logLevel = logLevel
    }

    override fun use(contextsManager: ChatContextsManager) {
        this.contextsManager = contextsManager
    }

    fun getBehaviour(): BaseBehaviourBuilder<ChatContext> {
        return behaviour
    }

    override operator fun LogLevel.unaryPlus(): LogLevel {
        logLevel = this
        return logLevel
    }

    override fun behaviour(function: BaseBehaviourBuilder<ChatContext>.() -> Unit) {
        val builder = BehaviourBuilder<ChatContext>(contextsManager)
        builder.function()
        behaviour = builder
    }
}
