package io.jonathanlee.sparrowexpressapikotlin.service.random

interface RandomService {

    fun generateNewId(): String

    fun generateNewTokenValue(): String

}
