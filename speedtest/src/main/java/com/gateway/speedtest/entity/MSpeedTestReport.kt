package com.gateway.speedtest.entity

data class MSpeedTestReport(
    val downloadedPacketSize: Long,
    val totalPacketSize: Long,
    val transferRateBit: Long,
    val startTime: Long,
    val endTime: Long,
    val speedTestState: SpeedTestState,
    val progressPercent: Float,
    val requestNum: Int,
    val downloadedPacketSizePerReportIntervalInBits: Long,
    val mbps: Double
)

sealed class SpeedTestState {
    object Progress : SpeedTestState()
    object Complete : SpeedTestState()
}
