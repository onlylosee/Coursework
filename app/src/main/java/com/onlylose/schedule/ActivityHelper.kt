package com.onlylose.schedule

import android.app.Activity
import android.content.pm.ActivityInfo


object ActivityHelper {
    fun initialize(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}