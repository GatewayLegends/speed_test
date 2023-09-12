package com.gateway.speedtest

import com.gateway.speedtest.entity.SpeedTestReport
import com.gateway.speedtest.entity.SpeedTestExceptions
import com.gateway.speedtest.entity.SpeedTestState
import com.gateway.speedtest.utils.bitToMb
import com.gateway.speedtest.utils.mapTo
import com.gateway.speedtest.utils.toException
import fr.bmartel.speedtest.SpeedTestReport as LibSpeedTestReport
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError


internal class SpeedTestListener(
    private val listener: (SpeedTestReport) -> Unit,
    private val error: (SpeedTestExceptions) -> Unit
) : ISpeedTestListener {

    private var reportList = mutableListOf<SpeedTestReport>()
    private var startTime: Long = System.currentTimeMillis()
    private var lastDownloadedPacketSizePerReportIntervalInBits: Long = 0

    override fun onCompletion(report: LibSpeedTestReport) {
        // process and calculate speed test report
        val speedTestReport = processSpeedTestReport(report = report, startTime = startTime)
        reportList.add(speedTestReport)

        // sort report based on mbps by descending
        val mbps = reportList.sortedByDescending { it.mbps }
            .subList(0, reportList.size / 2)
            .sumOf { it.mbps } / (reportList.size / 2)

        val totalSpeedTestReport = report.mapTo(
            startTime = startTime,
            speedTestState = SpeedTestState.Complete,
            downloadedPacketSizePerReportIntervalInBits = speedTestReport.downloadedPacketSizePerReportIntervalInBits,
            mbps = mbps
        )

        listener(totalSpeedTestReport)
    }

    override fun onProgress(percent: Float, report: LibSpeedTestReport) {
        val startTime = System.currentTimeMillis()

        val speedTestReport = processSpeedTestReport(report = report, startTime = startTime)

        reportList.add(speedTestReport)
        listener(speedTestReport)
    }

    override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
        error(speedTestError.toException())
    }

    private fun processSpeedTestReport(report: LibSpeedTestReport, startTime: Long): SpeedTestReport {
        val downloadedPacketSizePerReportIntervalInBits =
            report.temporaryPacketSize - lastDownloadedPacketSizePerReportIntervalInBits

        lastDownloadedPacketSizePerReportIntervalInBits = report.temporaryPacketSize

        val mb = downloadedPacketSizePerReportIntervalInBits.bitToMb()

        return report.mapTo(
            startTime = startTime,
            speedTestState = SpeedTestState.Progress,
            downloadedPacketSizePerReportIntervalInBits = downloadedPacketSizePerReportIntervalInBits,
            mbps = mb
        )
    }
}
