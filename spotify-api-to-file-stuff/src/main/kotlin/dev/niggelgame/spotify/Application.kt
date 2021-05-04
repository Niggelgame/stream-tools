package dev.niggelgame.spotify

import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.Token
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.channels.ticker
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.awt.Desktop
import java.net.URI
import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption
import kotlin.time.ExperimentalTime

const val configPath = "config.asdj"

const val codeVerifier = "pkFg9w7gAJVnJGfcXKqkOBna8sUJTWSjODlXagSG0hXnZkFAcIpnqE9fIeekzZdN"

@OptIn(ExperimentalPathApi::class, ExperimentalTime::class, ObsoleteCoroutinesApi::class)
suspend fun main() {
    val path = Path(configPath)

    val apiToken = if (path.isRegularFile()) {
        val token = Path("config.asdj").readText()
        Json.decodeFromString(token)
    } else {
        val token = getToken()
        val tokenString = Json.encodeToString(token)
        path.writeText(tokenString)
        token
    }

    val api = spotifyClientPkceApi(
        null,
        null,
        token = apiToken
    ).build()
    val filePath = Path(Config.LOCAL_FILE)
    val ticker = ticker(Duration.ofSeconds(10).toMillis(), 0)
    for (unit in ticker) {
        val song = api.player.getCurrentlyPlaying()?.track ?: continue
        filePath.writeText("${song.artists.joinToString(", ") { it.name }} - ${song.name}")
        println(song.name)
        println(song.artists.joinToString(", ") { it.name })
    }
}


suspend fun getToken(): Token {
    val code = getAuthCode()

    val api = spotifyClientPkceApi(
        Config.CLIENT_ID, // optional. include for token refresh
        "http://localhost:${Config.REDIRECT_PORT}/redirect", // optional. include for token refresh
        code,
        codeVerifier, // the same code verifier you used to generate the code challenge
    ).build()


    return api.token
}

suspend fun getAuthCode(): String = suspendCoroutine { cont ->
    embeddedServer(Netty, port = Config.REDIRECT_PORT) {
        routing {
            get("/redirect") {
                val code = context.parameters["code"]
                    ?: return@get
                println("Got key...")
                val environment = call.application.environment
                environment.monitor.raise(ApplicationStopPreparing, environment)
                if (environment is ApplicationEngineEnvironment) {
                    environment.stop()
                } else {
                    application.dispose()
                }

                cont.resume(code)
            }
        }
    }.start()


    val codeChallenge = getSpotifyPkceCodeChallenge(codeVerifier) // helper method
    val url: String = getSpotifyPkceAuthorizationUrl(
        SpotifyScope.USER_READ_CURRENTLY_PLAYING,
        clientId = Config.CLIENT_ID,
        redirectUri = "http://localhost:${Config.REDIRECT_PORT}/redirect",
        codeChallenge = codeChallenge
    )

    Desktop.getDesktop().browse(URI.create(url))
}