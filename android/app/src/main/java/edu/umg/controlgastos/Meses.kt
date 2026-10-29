package edu.umg.controlgastos

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Utilidades para trabajar con claves de mes con formato "yyyy-MM" (ej. "2026-10"). */
object Meses {
    private val nombres = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    private val formatoMes = SimpleDateFormat("yyyy-MM", Locale.US)

    /** "2026-10-03" -> "2026-10" */
    fun claveDeFecha(fecha: String): String = fecha.take(7)

    fun claveActual(): String = formatoMes.format(Date())

    fun claveAnterior(): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)   // evita saltos raros el día 31
        cal.add(Calendar.MONTH, -1)
        return formatoMes.format(cal.time)
    }

    /** "2026-10" -> "Octubre 2026" */
    fun nombreLegible(clave: String): String {
        val partes = clave.split("-")
        val anio = partes.getOrNull(0) ?: return clave
        val mes = partes.getOrNull(1)?.toIntOrNull() ?: return clave
        return "${nombres.getOrElse(mes - 1) { clave }} $anio"
    }
}
