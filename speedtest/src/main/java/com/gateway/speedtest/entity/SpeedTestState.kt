package com.gateway.speedtest.entity

sealed class SpeedTestState {
    object Progress : SpeedTestState()
    object Complete : SpeedTestState()
}
