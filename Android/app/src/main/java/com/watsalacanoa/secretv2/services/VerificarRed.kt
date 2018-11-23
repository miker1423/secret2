package com.watsalacanoa.secretv2.services

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by CLN-BRA on 05/09/2018.
 */

// Verificar si existe una conexión a internet

class VerificarRed {
    fun isConnected(context: Context) : Boolean{
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connManager.activeNetworkInfo.isConnected
    }
}