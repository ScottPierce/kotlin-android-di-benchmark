package com.spierce.dibench

import android.accounts.AccountManager
import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.Bundle
import android.os.DropBoxManager
import android.os.PowerManager
import android.os.Vibrator
import android.os.storage.StorageManager
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import android.widget.TextView
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import dagger.Component
import dagger.Module
import dagger.Provides
import kinject.ObjectGraph
import kinject.objectGraph
import javax.inject.Inject
import javax.inject.Singleton

abstract class BenchmarkActivity : AppCompatActivity() {
    companion object {
        val TEST_TIMES = 100
    }

    private var nanosToCreateInjector = 0L
    private var nanosToInject = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_benchmark)

        // Create Injector
        nanosToCreateInjector = 0
        for (i in 0..TEST_TIMES) {
            val start = System.nanoTime()
            createInjector()
            nanosToCreateInjector += System.nanoTime() - start
        }
        nanosToCreateInjector /= TEST_TIMES

        benchmarkInject()
        showData()

        findViewById(R.id.reInject).setOnClickListener {
            benchmarkInject()
            showData()
        }

        findViewById(R.id.dagger2Benchmark).setOnClickListener {
            startActivity(Intent(this@BenchmarkActivity, Dagger2BenchmarkActivity::class.java))
        }

        findViewById(R.id.kodeinBenchmark).setOnClickListener {
            startActivity(Intent(this@BenchmarkActivity, KodeinBenchmarkActivity::class.java))
        }

        findViewById(R.id.kinjectBenchmark).setOnClickListener {
            startActivity(Intent(this@BenchmarkActivity, KinjectBenchmarkActivity::class.java))
        }
    }

    fun benchmarkInject() {
        nanosToInject = 0
        for (i in 0..TEST_TIMES) {
            val start = System.nanoTime()
            inject()
            nanosToInject += System.nanoTime() - start
        }
        nanosToInject /= TEST_TIMES
    }

    fun showData() {
        val dataText = findViewById(R.id.data) as TextView
        dataText.text = "Injector Creation Time:\n" +
                "\t\t\tMillis: ${nanosToCreateInjector / 1000000L}\n" +
                "\t\t\tNanos:  $nanosToCreateInjector\n" +
                "Time to Inject:\n" +
                "\t\t\tMillis: ${nanosToInject / 1000000L}\n" +
                "\t\t\tNanos:  $nanosToInject"
    }

    abstract fun createInjector()
    abstract fun inject()
}

class Dagger2BenchmarkActivity : BenchmarkActivity() {
    @Inject lateinit var dep1: AccessibilityManager
    @Inject lateinit var dep2: AccountManager
    @Inject lateinit var dep3: ActivityManager
    @Inject lateinit var dep4: AlarmManager
    @Inject lateinit var dep5: AudioManager
    @Inject lateinit var dep6: ClipboardManager
    @Inject lateinit var dep7: ConnectivityManager
    @Inject lateinit var dep8: DevicePolicyManager
    @Inject lateinit var dep9: DownloadManager
    @Inject lateinit var dep10: DropBoxManager
    @Inject lateinit var dep11: InputMethodManager
    @Inject lateinit var dep12: KeyguardManager
    @Inject lateinit var dep13: LayoutInflater
    @Inject lateinit var dep14: LocationManager
    @Inject lateinit var dep15: NfcManager
    @Inject lateinit var dep16: NotificationManager
    @Inject lateinit var dep17: PowerManager
    @Inject lateinit var dep18: SearchManager
    @Inject lateinit var dep19: SensorManager
    @Inject lateinit var dep20: StorageManager
    @Inject lateinit var dep21: TelephonyManager
    @Inject lateinit var dep22: TextServicesManager
    @Inject lateinit var dep23: UiModeManager
    @Inject lateinit var dep24: UsbManager
    @Inject lateinit var dep25: Vibrator
    @Inject lateinit var dep27: WifiP2pManager
    @Inject lateinit var dep28: WifiManager
    @Inject lateinit var dep29: WindowManager

    lateinit var component: Dagger2Component

    override fun createInjector() {
        component = DaggerDagger2Component.builder()
                .dagger2Module(Dagger2Module())
                .build()
    }

    override fun inject() {
        component.inject(this)
    }
}

class KodeinBenchmarkActivity : BenchmarkActivity() {
    lateinit var dep1: AccessibilityManager
    lateinit var dep2: AccountManager
    lateinit var dep3: ActivityManager
    lateinit var dep4: AlarmManager
    lateinit var dep5: AudioManager
    lateinit var dep6: ClipboardManager
    lateinit var dep7: ConnectivityManager
    lateinit var dep8: DevicePolicyManager
    lateinit var dep9: DownloadManager
    lateinit var dep10: DropBoxManager
    lateinit var dep11: InputMethodManager
    lateinit var dep12: KeyguardManager
    lateinit var dep13: LayoutInflater
    lateinit var dep14: LocationManager
    lateinit var dep15: NfcManager
    lateinit var dep16: NotificationManager
    lateinit var dep17: PowerManager
    lateinit var dep18: SearchManager
    lateinit var dep19: SensorManager
    lateinit var dep20: StorageManager
    lateinit var dep21: TelephonyManager
    lateinit var dep22: TextServicesManager
    lateinit var dep23: UiModeManager
    lateinit var dep24: UsbManager
    lateinit var dep25: Vibrator
    lateinit var dep27: WifiP2pManager
    lateinit var dep28: WifiManager
    lateinit var dep29: WindowManager

    lateinit var kodein: Kodein

    override fun createInjector() {
        kodein = createKodein()
    }

    override fun inject() {
        dep1 = kodein.instance()
        dep2 = kodein.instance()
        dep3 = kodein.instance()
        dep4 = kodein.instance()
        dep5 = kodein.instance()
        dep6 = kodein.instance()
        dep7 = kodein.instance()
        dep8 = kodein.instance()
        dep9 = kodein.instance()
        dep10 = kodein.instance()
        dep11 = kodein.instance()
        dep12 = kodein.instance()
        dep13 = kodein.instance()
        dep14 = kodein.instance()
        dep15 = kodein.instance()
        dep16 = kodein.instance()
        dep17 = kodein.instance()
        dep18 = kodein.instance()
        dep19 = kodein.instance()
        dep20 = kodein.instance()
        dep21 = kodein.instance()
        dep22 = kodein.instance()
        dep23 = kodein.instance()
        dep24 = kodein.instance()
        dep25 = kodein.instance()
        dep27 = kodein.instance()
        dep28 = kodein.instance()
        dep29 = kodein.instance()
    }
}

class KinjectBenchmarkActivity : BenchmarkActivity() {
    lateinit var dep1: AccessibilityManager
    lateinit var dep2: AccountManager
    lateinit var dep3: ActivityManager
    lateinit var dep4: AlarmManager
    lateinit var dep5: AudioManager
    lateinit var dep6: ClipboardManager
    lateinit var dep7: ConnectivityManager
    lateinit var dep8: DevicePolicyManager
    lateinit var dep9: DownloadManager
    lateinit var dep10: DropBoxManager
    lateinit var dep11: InputMethodManager
    lateinit var dep12: KeyguardManager
    lateinit var dep13: LayoutInflater
    lateinit var dep14: LocationManager
    lateinit var dep15: NfcManager
    lateinit var dep16: NotificationManager
    lateinit var dep17: PowerManager
    lateinit var dep18: SearchManager
    lateinit var dep19: SensorManager
    lateinit var dep20: StorageManager
    lateinit var dep21: TelephonyManager
    lateinit var dep22: TextServicesManager
    lateinit var dep23: UiModeManager
    lateinit var dep24: UsbManager
    lateinit var dep25: Vibrator
    lateinit var dep27: WifiP2pManager
    lateinit var dep28: WifiManager
    lateinit var dep29: WindowManager

    lateinit var objectGraph: ObjectGraph

    override fun createInjector() {
        objectGraph = createObjectGraph()
    }

    override fun inject() {
        dep1 = objectGraph.get()
        dep2 = objectGraph.get()
        dep3 = objectGraph.get()
        dep4 = objectGraph.get()
        dep5 = objectGraph.get()
        dep6 = objectGraph.get()
        dep7 = objectGraph.get()
        dep8 = objectGraph.get()
        dep9 = objectGraph.get()
        dep10 = objectGraph.get()
        dep11 = objectGraph.get()
        dep12 = objectGraph.get()
        dep13 = objectGraph.get()
        dep14 = objectGraph.get()
        dep15 = objectGraph.get()
        dep16 = objectGraph.get()
        dep17 = objectGraph.get()
        dep18 = objectGraph.get()
        dep19 = objectGraph.get()
        dep20 = objectGraph.get()
        dep21 = objectGraph.get()
        dep22 = objectGraph.get()
        dep23 = objectGraph.get()
        dep24 = objectGraph.get()
        dep25 = objectGraph.get()
        dep27 = objectGraph.get()
        dep28 = objectGraph.get()
        dep29 = objectGraph.get()
    }
}

object InjectedItems {
    val ACCESSIBILITY_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val ACCOUNT_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
    val ACTIVITY_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val ALARM_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val AUDIO_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val CLIPBOARD_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val CONNECTIVITY_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val DEVICE_POLICY_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val DOWNLOAD_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val DROPBOX_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.DROPBOX_SERVICE) as DropBoxManager
    val INPUT_METHOD_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val KEYGUARD_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    val LAYOUT_INFLATER_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val LOCATION_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val NFC_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.NFC_SERVICE) as NfcManager
    val NOTIFICATION_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val POWER_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.POWER_SERVICE) as PowerManager
    val SEARCH_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.SEARCH_SERVICE) as SearchManager
    val SENSOR_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val STORAGE_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.STORAGE_SERVICE) as StorageManager
    val TELEPHONY_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val TEXT_SERVICES_MANAGER_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager
    val UI_MODE_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val USB_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.USB_SERVICE) as UsbManager
    val VIBRATOR_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val WIFI_P2P_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    val WIFI_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val WINDOW_SERVICE = DiBenchmarkApplication.instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}

fun createKodein() = Kodein {
    import(Kodein.Module {
        bind() from singleton { InjectedItems.ACCESSIBILITY_SERVICE }
        bind() from singleton { InjectedItems.ACCOUNT_SERVICE }
        bind() from singleton { InjectedItems.ACTIVITY_SERVICE }
        bind() from singleton { InjectedItems.ALARM_SERVICE }
        bind() from singleton { InjectedItems.AUDIO_SERVICE }
        bind() from singleton { InjectedItems.CLIPBOARD_SERVICE }
        bind() from singleton { InjectedItems.CONNECTIVITY_SERVICE }
        bind() from singleton { InjectedItems.DEVICE_POLICY_SERVICE }
        bind() from singleton { InjectedItems.DOWNLOAD_SERVICE }
        bind() from singleton { InjectedItems.DROPBOX_SERVICE }
        bind() from singleton { InjectedItems.INPUT_METHOD_SERVICE }
        bind() from singleton { InjectedItems.KEYGUARD_SERVICE }
        bind() from singleton { InjectedItems.LAYOUT_INFLATER_SERVICE }
        bind() from singleton { InjectedItems.LOCATION_SERVICE }
        bind() from singleton { InjectedItems.NFC_SERVICE  }
        bind() from singleton { InjectedItems.NOTIFICATION_SERVICE }
        bind() from singleton { InjectedItems.POWER_SERVICE }
        bind() from singleton { InjectedItems.SEARCH_SERVICE  }
        bind() from singleton { InjectedItems.SENSOR_SERVICE  }
        bind() from singleton { InjectedItems.STORAGE_SERVICE }
        bind() from singleton { InjectedItems.TELEPHONY_SERVICE }
        bind() from singleton { InjectedItems.TEXT_SERVICES_MANAGER_SERVICE }
        bind() from singleton { InjectedItems.UI_MODE_SERVICE  }
        bind() from singleton { InjectedItems.USB_SERVICE }
        bind() from singleton { InjectedItems.VIBRATOR_SERVICE }
        bind() from singleton { InjectedItems.WIFI_P2P_SERVICE }
        bind() from singleton { InjectedItems.WIFI_SERVICE }
        bind() from singleton { InjectedItems.WINDOW_SERVICE }
    })
}

fun createObjectGraph() = objectGraph {
    singleton { InjectedItems.ACCESSIBILITY_SERVICE }
    singleton { InjectedItems.ACCOUNT_SERVICE }
    singleton { InjectedItems.ACTIVITY_SERVICE }
    singleton { InjectedItems.ALARM_SERVICE }
    singleton { InjectedItems.AUDIO_SERVICE }
    singleton { InjectedItems.CLIPBOARD_SERVICE }
    singleton { InjectedItems.CONNECTIVITY_SERVICE }
    singleton { InjectedItems.DEVICE_POLICY_SERVICE }
    singleton { InjectedItems.DOWNLOAD_SERVICE }
    singleton { InjectedItems.DROPBOX_SERVICE }
    singleton { InjectedItems.INPUT_METHOD_SERVICE }
    singleton { InjectedItems.KEYGUARD_SERVICE }
    singleton { InjectedItems.LAYOUT_INFLATER_SERVICE }
    singleton { InjectedItems.LOCATION_SERVICE }
    singleton { InjectedItems.NFC_SERVICE  }
    singleton { InjectedItems.NOTIFICATION_SERVICE }
    singleton { InjectedItems.POWER_SERVICE }
    singleton { InjectedItems.SEARCH_SERVICE  }
    singleton { InjectedItems.SENSOR_SERVICE  }
    singleton { InjectedItems.STORAGE_SERVICE }
    singleton { InjectedItems.TELEPHONY_SERVICE }
    singleton { InjectedItems.TEXT_SERVICES_MANAGER_SERVICE }
    singleton { InjectedItems.UI_MODE_SERVICE  }
    singleton { InjectedItems.USB_SERVICE }
    singleton { InjectedItems.VIBRATOR_SERVICE }
    singleton { InjectedItems.WIFI_P2P_SERVICE }
    singleton { InjectedItems.WIFI_SERVICE }
    singleton { InjectedItems.WINDOW_SERVICE }
}

@Singleton
@Component(
        modules = arrayOf(Dagger2Module::class)
)
interface Dagger2Component {
    fun inject(obj: Dagger2BenchmarkActivity)
}

@Module
class Dagger2Module() {
    @Provides @Singleton fun provideAccessibilityManager(): AccessibilityManager {
        return InjectedItems.ACCESSIBILITY_SERVICE
    }

    @Provides @Singleton fun provideAccountManager(): AccountManager {
        return InjectedItems.ACCOUNT_SERVICE
    }

    @Provides @Singleton fun provideActivityManager(): ActivityManager {
        return InjectedItems.ACTIVITY_SERVICE
    }

    @Provides @Singleton fun provideAlarmManager(): AlarmManager {
        return InjectedItems.ALARM_SERVICE
    }

    @Provides @Singleton fun provideAudioManager(): AudioManager {
        return InjectedItems.AUDIO_SERVICE
    }

    @Provides @Singleton fun provideClipboardManager(): ClipboardManager {
        return InjectedItems.CLIPBOARD_SERVICE
    }

    @Provides @Singleton fun provideConnectivityManager(): ConnectivityManager {
        return InjectedItems.CONNECTIVITY_SERVICE
    }

    @Provides @Singleton fun provideDevicePolicyManager(): DevicePolicyManager {
        return InjectedItems.DEVICE_POLICY_SERVICE
    }

    @Provides @Singleton fun provideDownloadManager(): DownloadManager {
        return InjectedItems.DOWNLOAD_SERVICE
    }

    @Provides @Singleton fun provideDropBoxManager(): DropBoxManager {
        return InjectedItems.DROPBOX_SERVICE
    }

    @Provides @Singleton fun provideInputMethodManager(): InputMethodManager {
        return InjectedItems.INPUT_METHOD_SERVICE
    }

    @Provides @Singleton fun provideKeyguardManager(): KeyguardManager {
        return InjectedItems.KEYGUARD_SERVICE
    }

    @Provides @Singleton fun provideLayoutInflater(): LayoutInflater {
        return InjectedItems.LAYOUT_INFLATER_SERVICE
    }

    @Provides @Singleton fun provideLocationManager(): LocationManager {
        return InjectedItems.LOCATION_SERVICE
    }

    @Provides @Singleton fun provideNfcManager(): NfcManager {
        return InjectedItems.NFC_SERVICE
    }

    @Provides @Singleton fun provideNotificationManager(): NotificationManager {
        return InjectedItems.NOTIFICATION_SERVICE
    }

    @Provides @Singleton fun providePowerManager(): PowerManager {
        return InjectedItems.POWER_SERVICE
    }

    @Provides @Singleton fun provideSearchManager(): SearchManager {
        return InjectedItems.SEARCH_SERVICE
    }

    @Provides @Singleton fun provideSensorManager(): SensorManager {
        return InjectedItems.SENSOR_SERVICE
    }

    @Provides @Singleton fun provideStorageManager(): StorageManager {
        return InjectedItems.STORAGE_SERVICE
    }

    @Provides @Singleton fun provideTelephonyManager(): TelephonyManager {
        return InjectedItems.TELEPHONY_SERVICE
    }

    @Provides @Singleton fun provideTextServicesManager(): TextServicesManager {
        return InjectedItems.TEXT_SERVICES_MANAGER_SERVICE
    }

    @Provides @Singleton fun provideUiModeManager(): UiModeManager {
        return InjectedItems.UI_MODE_SERVICE
    }

    @Provides @Singleton fun provideUsbManager(): UsbManager {
        return InjectedItems.USB_SERVICE
    }

    @Provides @Singleton fun provideVibrator(): Vibrator {
        return InjectedItems.VIBRATOR_SERVICE
    }

    @Provides @Singleton fun provideWifiP2pManager(): WifiP2pManager {
        return InjectedItems.WIFI_P2P_SERVICE
    }

    @Provides @Singleton fun provideWifiManager(): WifiManager {
        return InjectedItems.WIFI_SERVICE
    }

    @Provides @Singleton fun provideWindowManager(): WindowManager {
        return InjectedItems.WINDOW_SERVICE
    }
}