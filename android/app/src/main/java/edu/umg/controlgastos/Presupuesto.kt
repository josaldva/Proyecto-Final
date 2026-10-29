package edu.umg.controlgastos

import android.content.Context
import androidx.core.content.edit

/** Guarda y lee el presupuesto mensual. 0.0 significa "sin presupuesto". */
object Presupuesto {
    private const val PREFS = "ajustes"
    private const val CLAVE = "presupuesto_mensual"

    fun obtener(context: Context): Double =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(CLAVE, null)?.toDoubleOrNull() ?: 0.0

    fun guardar(context: Context, monto: Double) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putString(CLAVE, monto.toString())
        }
    }
}
