package dev.niggelgame.spotify

import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.Token
import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.*
import kotlin.time.ExperimentalTime

const val codeVerifier = "pkFg9w7gAJVnJGfcXKqkOBna8sUJTWSjODlXagSG0hXnZkFAcIpnqE9fIeekzZdN"

@OptIn(ExperimentalPathApi::class, ExperimentalTime::class, ObsoleteCoroutinesApi::class)
suspend fun main() {
    val path = Path(Config.CONFIG_PATH)

    val apiToken = if (path.isRegularFile()) {
        val token = path.readText()
        Json.decodeFromString(token)
    } else {
        getToken()
    }

    var api = getApiClient(apiToken)

    val filePath = Path(Config.LOCAL_FILE)
    val ticker = ticker(Duration.ofSeconds(10).toMillis(), 0)
    for (unit in ticker) {
        val song = try {
            api.player.getCurrentlyPlaying()?.track ?: continue
        } catch (e: SpotifyException.AuthenticationException) {
            val token = getToken()
            api = getApiClient(token)
            api.player.getCurrentlyPlaying()?.track ?: continue
        }
        filePath.writeText(" ${song.artists.joinToString(", ") { it.name }} - ${song.name} |")
        println(song.name)
        println(song.artists.joinToString(", ") { it.name })
    }
}

suspend fun getApiClient(t: Token) : SpotifyClientApi {
    return try {
        spotifyClientPkceApi(
            Config.CLIENT_ID,
            null,
            token = t
        ).build()
    } catch (e: SpotifyException.AuthenticationException) {
        getApiClient(getToken())
    }
}

@OptIn(ExperimentalPathApi::class)
suspend fun getToken(): Token {
    val code = getAuthCode()

    val api = spotifyClientPkceApi(
        Config.CLIENT_ID, // optional. include for token refresh
        "http://localhost:${Config.REDIRECT_PORT}/redirect", // optional. include for token refresh
        code,
        codeVerifier, // the same code verifier you used to generate the code challenge
    ).build()

    val tokenString = Json.encodeToString(api.token)
    Path(Config.CONFIG_PATH).writeText(tokenString)

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

    println("Open this URL: $url")
    // Desktop.getDesktop().browse(URI.create(url))
}