package chatbot.dsl

import chatbot.api.*


@BanningIncorrectDSLConstructionsMarker
abstract class BaseBehaviourBuilder<T : ChatContext?> {
    class MyPair<T, E>(val predicate: T, val function: E)
    abstract class BaseFunction<T : ChatContext?>(val function: MessageProcessorContext<T>.() -> Unit)

    open fun onCommand(command: String, function: MessageProcessor<T>) {}

    open fun onMessage(predicate: MessagePredicate, function: MessageProcessor<T>) {}

    open fun onMessageContains(text: String, function: MessageProcessor<T>) {}

    open fun onMessagePrefix(prefix: String, function: MessageProcessor<T>) {}

    open fun onMessage(messageTextExactly: String, function: MessageProcessor<T>) {}

    open fun onMessage(function: MessageProcessor<T>) {}

    open fun getContextManager(): ChatContextsManager {
        return object : ChatContextsManager {
            override fun getContext(chatId: ChatId): ChatContext? {
                TODO("Not yet implemented")
            }

            override fun setContext(chatId: ChatId, newState: ChatContext?) {}
        }
    }

    fun setSetupContextType(
        name: String?,
        list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>,
    ) {
        putToSetup(name, list)
    }

    protected abstract fun putToSetup(
        name: String?,
        list: MutableList<out MyPair<ChatBot.(Message) -> Boolean, out BaseFunction<out T>>>,
    )

    protected abstract fun putToSetup(
        o: ChatContext?,
        list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>,
    )

    inline fun <reified E : T> into(function: BaseBehaviourBuilder<E>.() -> Unit) {
        val builder = BehaviourBuilder<E>(getContextManager())
        builder.function()
        setSetupContextType(E::class.simpleName, builder.getSetup().getSetup())
    }

    infix fun T.into(function: BaseBehaviourBuilder<T>.() -> Unit) {
        val builder = BehaviourBuilder<T>(getContextManager())
        builder.function()
        putToSetup(this, builder.getSetup().getSetup())
    }

    abstract fun getFunction(chatBot: ChatBot, message: Message): MessageProcessor<T?>
}
