package com.vtl.javicalendar.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingWorkPolicy
import java.time.Duration

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (
        intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
    ) {
      Log.v("BootReceiver", "Device rebooted or App updated. Triggering immediate widget update.")

      // Trigger an immediate update to ensure widget is fresh
      DailyUpdateWorker.schedule(context, Duration.ZERO, ExistingWorkPolicy.REPLACE)

      // Ensure holiday sync is also set up
      HolidaySyncWorker.setupPeriodicWork(context)
    }
  }
}
