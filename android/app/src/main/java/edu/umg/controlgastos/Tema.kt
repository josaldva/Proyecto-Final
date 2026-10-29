package edu.umg.controlgastos

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

/** Guarda y aplica la preferencia de modo oscuro. */
object Tema {
    private const val PREFS = "ajustes"
    private const val CLAVE_OSCURO = "modo_oscuro"

    /** Si el usuario nunca eligió, se sigue el tema del sistema. */
    fun aplicarGuardado(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val modo = when {
            !prefs.contains(CLAVE_OSCURO) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            prefs.getBoolean(CLAVE_OSCURO, false) -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(modo)
    }

    fun estaOscuro(context: Context): Boolean {
        val mascara = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mascara == Configuration.UI_MODE_NIGHT_YES
    }

    /** Cambia entre claro y oscuro; la Activity se recrea sola. */
    fun alternar(context: Context) {
        val nuevoOscuro = !estaOscuro(context)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putBoolean(CLAVE_OSCURO, nuevoOscuro)
        }
        AppCompatDelegate.setDefaultNightMode(
            if (nuevoOscuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
