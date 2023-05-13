package io.jonathanlee.sparrowexpressapikotlin.service.profile.impl

import io.jonathanlee.sparrowexpressapikotlin.service.profile.ActiveProfileService
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Service
class ActiveProfileServiceImpl(
    private val environment: Environment
) : ActiveProfileService {

    companion object {
        const val LOCAL_PROFILE_NAME = "local"
        const val PRODUCTION_PROFILE_NAME = "production"
    }

    override fun isLocalActiveProfile(): Boolean {
        return this.containsActiveProfile(LOCAL_PROFILE_NAME)
    }

    override fun isProductionActiveProfile(): Boolean {
        return this.containsActiveProfile(PRODUCTION_PROFILE_NAME)
    }

    private fun containsActiveProfile(profile: String): Boolean {
        val doesContain = AtomicBoolean(false)
        this.environment.activeProfiles.forEach {
            if (it == profile) {
                doesContain.set(true)
            }
        }
        return doesContain.get()
    }

}
