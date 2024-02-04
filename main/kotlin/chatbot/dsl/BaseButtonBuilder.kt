package chatbot.dsl

interface BaseButtonBuilder {
    fun button(text: String)

    operator fun String.unaryMinus(): String
}
