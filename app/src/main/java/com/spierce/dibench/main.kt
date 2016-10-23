package com.spierce.dibench

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class DiBenchmarkApplication : Application() {
    companion object {
        lateinit var instance: DiBenchmarkApplication
    }

    override fun onCreate() {
        super.onCreate()
        DiBenchmarkApplication.instance = this

        // Ensure that InjectedItems has been loaded
        InjectedItems.CONNECTIVITY_SERVICE.addDefaultNetworkActiveListener {  }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById(R.id.dagger2Benchmark).setOnClickListener {
            startActivity(Intent(this@MainActivity, Dagger2BenchmarkActivity::class.java))
        }

        findViewById(R.id.kodeinBenchmark).setOnClickListener {
            startActivity(Intent(this@MainActivity, KodeinBenchmarkActivity::class.java))
        }

        findViewById(R.id.kinjectBenchmark).setOnClickListener {
            startActivity(Intent(this@MainActivity, KinjectBenchmarkActivity::class.java))
        }
    }
}