package chatbot.dsl

import chatbot.api.ChatContext
import chatbot.api.ChatContextsManager
import chatbot.api.LogLevel

@BanningIncorrectDSLConstructionsMarker
interface BaseSetupBuilder {
    fun use(logLevel: LogLevel)

    fun use(contextsManager: ChatContextsManager)

    operator fun LogLevel.unaryPlus(): LogLevel

    fun behaviour(function: BaseBehaviourBuilder<ChatContext>.() -> Unit)
}
