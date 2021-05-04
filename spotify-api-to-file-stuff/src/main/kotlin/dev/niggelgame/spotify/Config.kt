package dev.niggelgame.spotify

import dev.schlaubi.envconf.environment
import dev.schlaubi.envconf.getEnv

object Config {
    val CLIENT_ID by environment
    val LOCAL_FILE by environment
    val REDIRECT_PORT by getEnv { it.toInt() }
}
