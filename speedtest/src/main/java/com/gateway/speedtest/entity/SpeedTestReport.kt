package com.gateway.speedtest.entity

data class SpeedTestReport(
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
