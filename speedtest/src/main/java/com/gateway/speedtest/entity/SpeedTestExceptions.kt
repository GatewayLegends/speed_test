package com.gateway.speedtest.entity

import java.io.IOException


abstract class SpeedTestExceptions(message: String) : IOException(message)

class InvalidHttpResponse : SpeedTestExceptions("Invalid http response")
class SocketError : SpeedTestExceptions("Socket error")
class SocketTimeout : SpeedTestExceptions("Socket timeout")
class ConnectionError : SpeedTestExceptions("Connection error")
class MalformedURI : SpeedTestExceptions("Malformed URI")
class UnsupportedProtocol : SpeedTestExceptions("Unsupported protocol")
