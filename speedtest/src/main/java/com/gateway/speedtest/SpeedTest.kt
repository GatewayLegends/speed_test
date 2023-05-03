package com.gateway.speedtest

import com.gateway.speedtest.entity.MSpeedTestReport
import com.gateway.speedtest.entity.SpeedTestState
import fr.bmartel.speedtest.SpeedTestSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class SpeedTest {

    @Throws(IOException::class)
    suspend fun ping(
        packets: Int = 1,
        link: String = "google.com",
    ): String? = withContext(Dispatchers.IO) {
        return@withContext run {
            val runtime = Runtime.getRuntime()
            val cmd = "/system/bin/ping -c $packets $link "
            val process = runtime.exec(cmd)

            val inputStreamReader = InputStreamReader(process.inputStream)
            val reader = BufferedReader(inputStreamReader)
            val line = StringBuilder()

            while (reader.readLine() != null) {
                line.append(reader.readLine())
            }

            process.waitFor()
            line.toString()
                .split(" ")
                .find { it.contains("time=") }?.let {
                    it.substring(it.indexOf("time=") + "time=".length)
                }
        }
    }

    fun download(
        link: String,
        durationMillis: Int,
        intervalMillis: Int = 1000
    ): Flow<MSpeedTestReport> = callbackFlow {
        val speedTestSocket = SpeedTestSocket()

        speedTestSocket.addSpeedTestListener(SpeedTestListener(listener = {
            trySend(it)

            if (it.speedTestState == SpeedTestState.Complete)
                close()
        }, error = { close(it) }))

        speedTestSocket.startFixedDownload(link, durationMillis, intervalMillis)

        awaitClose { speedTestSocket.closeSocket() }
    }

    fun upload(
        link: String,
        fileSize: Int = 100000000,
        durationMillis: Int,
        intervalMillis: Int = 1000
    ): Flow<MSpeedTestReport> = callbackFlow {
        val speedTestSocket = SpeedTestSocket()

        speedTestSocket.addSpeedTestListener(SpeedTestListener(listener = {
            trySend(it)

            if (it.speedTestState == SpeedTestState.Complete)
                close()
        }, error = { close(it) }))

        speedTestSocket.startFixedUpload(link, fileSize, durationMillis, intervalMillis)

        awaitClose { speedTestSocket.closeSocket() }
    }
}
