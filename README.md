[![](https://jitpack.io/v/GatewayLegends/speed_test.svg)](https://jitpack.io/#GatewayLegends/speed_test)

# Speed Test

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

Step 2. Add the dependency

```gradle
dependencies {
	        implementation 'com.github.GatewayLegends:speed_test:<VERSION>'
}
```

## Usage

```kotlin
...
import com.gateway.speedtest.SpeedTest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ...

        // initialize
        val speedtest = SpeedTest()

        CoroutineScope(Dispatchers.IO).launch {

            // Collect ping updates as flow
            speedtest.ping(
                link = "google.com",
                packets = 10 // Optional
            ).collect {
                Log.d("TESTING", "PING: $it")
            }

            speedtest.download(
                link = "http://212.183.159.230/512MB.zip",
                durationMillis = 10_000,
                intervalMillis = 1000 // Optional
            ).collect {
                // Do Something
                Log.d("TESTING", "DOWNLOAD: $it")
            }

            speedtest.upload(
                link = "https://lille.testdebit.info/",
                durationMillis = 10_000,
                fileSize = 100_000_000, // Optional
                intervalMillis = 1000 // Optional
            ).collect {
                // Do Something
                Log.d("TESTING", "UPLOAD: $it")
            }
        }
    }
}

```
