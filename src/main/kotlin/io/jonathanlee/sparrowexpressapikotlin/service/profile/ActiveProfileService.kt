package io.jonathanlee.sparrowexpressapikotlin.service.profile

interface ActiveProfileService {

    fun isLocalActiveProfile(): Boolean

    fun isProductionActiveProfile(): Boolean

}