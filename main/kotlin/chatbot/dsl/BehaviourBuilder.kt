package chatbot.dsl

import chatbot.api.*

typealias MessagePredicate = ChatBot.(Message) -> Boolean

@DslMarker
annotation class BanningIncorrectDSLConstructionsMarker

@BanningIncorrectDSLConstructionsMarker
class BehaviourBuilder<T : ChatContext?>(
    private val contextManager: ChatContextsManager,
) : BaseBehaviourBuilder<T>() {
    class Function<T : ChatContext?>(function: MessageProcessorContext<T>.() -> Unit) : BaseFunction<T>(function)
    inner class Setup {
        private val setup: MutableList<MyPair<MessagePredicate, out Function<out T>>> = mutableListOf()
        private val setupContextType:
            MutableMap<String?, MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>> =
            HashMap()
        private val setupContextObject:
            MutableMap<ChatContext?, MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>> =
            HashMap()

        fun getSetup() = setup

        fun getFunction(chatBot: ChatBot, message: Message): MessageProcessor<T?> {
            val context = contextManager.getContext(message.chatId) ?: object : ChatContext {}
            return getFunction(
                chatBot,
                message,
                setupContextObject.getOrDefault(
                    context,
                    setupContextType.getOrDefault(context::class.simpleName, setup),
                ),
            )
        }

        private fun getFunction(
            chatBot: ChatBot,
            message: Message,
            list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>,
        ): MessageProcessor<T?> {
            var function: MessageProcessor<T?> = {}
            for (p in list) {
                if (p.predicate(chatBot, message)) {
                    function = p.function.function as MessageProcessor<T?>
                    break
                }
            }
            return function
        }

        fun putToSetup(pair: MyPair<MessagePredicate, Function<T>>) {
            if (!setup.contains(pair)) setup.add(pair)
        }

        fun putToSetup(name: String?, list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>) {
            setupContextType[name] = list
        }

        fun putToSetup(o: ChatContext?, list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>) {
            setupContextObject[o] = list
        }
    }

    private var canChange = true
    private val setup = Setup()

    fun getSetup() = setup

    override fun getContextManager() = contextManager

    override fun getFunction(chatBot: ChatBot, message: Message): MessageProcessor<T?> {
        return setup.getFunction(chatBot, message)
    }

    private fun putToSetup(predicate: MessagePredicate, function: MessageProcessor<T>) {
        if (canChange) {
            setup.putToSetup(MyPair(predicate, Function(function)))
        }
    }

    override fun putToSetup(name: String?, list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>) {
        setup.putToSetup(name, list)
    }

    override fun putToSetup(o: ChatContext?, list: MutableList<out MyPair<MessagePredicate, out BaseFunction<out T>>>) {
        setup.putToSetup(o, list)
    }

    override fun onCommand(command: String, function: MessageProcessor<T>) {
        putToSetup({ it.text.split(" ")[0] == "/$command" }, function)
    }

    override fun onMessage(predicate: MessagePredicate, function: MessageProcessor<T>) {
        putToSetup(predicate, function)
    }

    override fun onMessageContains(text: String, function: MessageProcessor<T>) {
        putToSetup({ it.text.contains(text) }, function)
    }

    override fun onMessagePrefix(prefix: String, function: MessageProcessor<T>) {
        putToSetup({ it.text.startsWith(prefix) }, function)
    }

    override fun onMessage(messageTextExactly: String, function: MessageProcessor<T>) {
        putToSetup({ it.text == messageTextExactly }, function)
    }

    override fun onMessage(function: MessageProcessor<T>) {
        putToSetup({ true }, function)
        canChange = false
    }
}
