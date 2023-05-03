package com.gateway.speedtest.entity

import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.model.SpeedTestError

internal fun SpeedTestError.toException() = when (this) {
    SpeedTestError.INVALID_HTTP_RESPONSE -> InvalidHttpResponse()
    SpeedTestError.SOCKET_ERROR -> SocketError()
    SpeedTestError.SOCKET_TIMEOUT -> SocketTimeout()
    SpeedTestError.CONNECTION_ERROR -> ConnectionError()
    SpeedTestError.MALFORMED_URI -> MalformedURI()
    SpeedTestError.UNSUPPORTED_PROTOCOL -> UnsupportedProtocol()
}

internal fun SpeedTestReport.mapTo(
    startTime: Long,
    speedTestState: SpeedTestState,
    downloadedPacketSizePerReportIntervalInBits: Long,
    mbps: Double
) = MSpeedTestReport(
    downloadedPacketSize = temporaryPacketSize,
    totalPacketSize = totalPacketSize,
    transferRateBit = transferRateBit.toLong(),
    startTime = startTime,
    endTime = System.currentTimeMillis(),
    speedTestState = speedTestState,
    progressPercent = progressPercent,
    requestNum = requestNum,
    downloadedPacketSizePerReportIntervalInBits = downloadedPacketSizePerReportIntervalInBits,
    mbps = mbps
)
