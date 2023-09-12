package com.gateway.speedtest

import com.gateway.speedtest.entity.SpeedTestReport
import com.gateway.speedtest.entity.SpeedTestState
import fr.bmartel.speedtest.SpeedTestSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader


class SpeedTest {

    suspend fun ping(
        link: String,
        packets: Int = PING_PACKETS_COUNT,
    ): Flow<Int> = flow {
        runCatching {
            val runtime = Runtime.getRuntime()
            val cmd = "$PING_COMMAND $packets $link"
            val process = runtime.exec(cmd)

            val pattern = "time=(\\d+)".toRegex()
            val inputStreamReader = InputStreamReader(process.inputStream)
            val reader = BufferedReader(inputStreamReader)
            var sum = 0
            while (reader.readLine() != null) {
                val line = reader.readLine()
                val value = pattern.find(line)?.groupValues?.getOrNull(1)
                value?.toIntOrNull()?.let {
                    emit(it)
                    sum += it
                }
            }

            if (sum != 0)
                emit(sum / packets)

            process.waitFor()
        }
    }.flowOn(Dispatchers.IO)


    fun download(
        link: String,
        durationMillis: Int,
        intervalMillis: Int = DOWNLOAD_INTERVAL_IN_MILLIS
    ): Flow<SpeedTestReport> = callbackFlow {
        val speedTestSocket = SpeedTestSocket()

        speedTestSocket.addSpeedTestListener(
            SpeedTestListener(
                listener = {
                    trySend(it)

                    if (it.speedTestState == SpeedTestState.Complete)
                        close()
                },
                error = { close(it) })
        )

        speedTestSocket.startFixedDownload(link, durationMillis, intervalMillis)

        awaitClose { speedTestSocket.closeSocket() }
    }

    fun upload(
        link: String,
        fileSize: Int = UPLOAD_FILE_SIZE,
        durationMillis: Int,
        intervalMillis: Int = UPLOAD_INTERVAL_IN_MILLIS
    ): Flow<SpeedTestReport> = callbackFlow {
        val speedTestSocket = SpeedTestSocket()

        speedTestSocket.addSpeedTestListener(
            SpeedTestListener(
                listener = {
                    trySend(it)

                    if (it.speedTestState == SpeedTestState.Complete)
                        close()
                },
                error = { close(it) }
            )
        )

        speedTestSocket.startFixedUpload(link, fileSize, durationMillis, intervalMillis)

        awaitClose { speedTestSocket.closeSocket() }
    }

    companion object {
        const val PING_COMMAND = "/system/bin/ping -c"
        const val PING_PACKETS_COUNT = 1
        const val UPLOAD_FILE_SIZE = 100_000_000
        const val DOWNLOAD_INTERVAL_IN_MILLIS = 1_000
        const val UPLOAD_INTERVAL_IN_MILLIS = 1_000
    }
}
