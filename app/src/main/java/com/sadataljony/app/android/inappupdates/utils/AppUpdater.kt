package com.sadataljony.app.android.inappupdates.utils

import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

const val APP_UPDATE_CODE = 1001

open class AppUpdater(val context: FragmentActivity) {

    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener

    init {
        appUpdateManager = AppUpdateManagerFactory.create(context)
        installStateUpdatedListener = installStateUpdatedListener()
        appUpdateManager?.registerListener(installStateUpdatedListener)
    }

    private val updateFlowResultLauncher =
        context.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Handle successful app update
                Log.d("App Update Status", "onActivityResult: ${result.resultCode}")
            } else {
                Log.e("App Update Status", "onActivityResult: failure - ${result.resultCode}")
            }
        }

    open fun updateApp() {
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/)
            ) {
                val starter =
                    IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                        val request = IntentSenderRequest.Builder(intent)
                            .setFillInIntent(fillInIntent)
                            .setFlags(flagsValues, flagsMask)
                            .build()
                        updateFlowResultLauncher.launch(request)
                    }
                try {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE /*AppUpdateType.IMMEDIATE*/,
                        starter,
                        APP_UPDATE_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                    println("exp =1= " + e.printStackTrace())
                } catch (e: Exception) {
                    println("exp == " + e.printStackTrace())
                }
            } else if (appUpdateInfo.installStatus() === InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                context.showSuccessDialog("App Update Available!", "Please, update this App.")
                {
                    if (appUpdateManager != null) {
                        appUpdateManager?.completeUpdate()
                    }
                }
            } else {
                Log.e("App Update Status", "checkForAppUpdateAvailability: something else")
            }
        }

        appUpdateManager?.appUpdateInfo?.addOnFailureListener {

        }
    }

    private fun installStateUpdatedListener() = object : InstallStateUpdatedListener {
        override fun onStateUpdate(state: InstallState) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                context.showSuccessDialog("App Update", "Please Update Your App.")
                {
                    if (appUpdateManager != null) {
                        appUpdateManager?.completeUpdate()
                    }
                }
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                appUpdateManager?.unregisterListener(this)
            } else {
                Log.i(
                    "App Update Status",
                    "InstallStateUpdatedListener: state: " + state.installStatus()
                )
            }
        }
    }

    open fun stop() {
        appUpdateManager?.let {
            it.unregisterListener(installStateUpdatedListener)
        }
    }

}