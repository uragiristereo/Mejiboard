package com.github.uragiristereo.mejiboard.util

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import timber.log.Timber


object PermissionHelper {
    fun checkPermission(context: Context): Boolean {
        // todo: migrate to scoped storage
//        return if (SDK_INT >= Build.VERSION_CODES.R) {
//            Environment.isExternalStorageManager()
//        } else {
            val readPermission = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE)
            val writePermission = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
            Timber.i("${readPermission == PackageManager.PERMISSION_GRANTED}")
            Timber.i("${writePermission == PackageManager.PERMISSION_GRANTED}")
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED
//        }
    }

    fun requestPermission(context: Context) {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse(String.format("package:%s", context.packageName))
//                startActivityForResult(intent, 2296)
//            } catch (e: Exception) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                startActivityForResult(intent, 2296)
//            }
//        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), 1)
//        }
    }
}