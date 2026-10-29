package edu.umg.controlgastos

import android.app.Application

class ControlGastosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Tema.aplicarGuardado(this)
    }
}
