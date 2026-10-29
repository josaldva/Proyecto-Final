package edu.umg.controlgastos

import android.content.Intent
import android.text.InputType
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import edu.umg.controlgastos.databinding.ActivityMainBinding
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.formatearQuetzales
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GastoAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        respetarBarrasDelSistema()

        db = AppDatabase.getDatabase(this)

        adapter = GastoAdapter { gasto -> abrirFormulario(gasto.id) }
        binding.rvGastos.layoutManager = LinearLayoutManager(this)
        binding.rvGastos.adapter = adapter

        binding.btnNuevo.setOnClickListener { abrirFormulario(null) }
        binding.btnPresupuesto.setOnClickListener { mostrarDialogoPresupuesto() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { cargarGastos() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_presupuesto) {
            mostrarDialogoPresupuesto()
            return true
        }
        if (item.itemId == R.id.action_tema) {
            Tema.alternar(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun cargarGastos() {
        // 1) Leer de SQLite y convertir a objetos del dominio
        val gastos = db.gastoDao().obtenerTodos().map { e ->
            Gasto(
                id = e.id,
                descripcion = e.descripcion,
                monto = e.monto,
                categoria = Categoria.valueOf(e.categoria),
                fecha = e.fecha
            )
        }

        // 2) Agrupar por mes ("2026-10"), del más reciente al más antiguo
        val porMes = gastos
            .groupBy { Meses.claveDeFecha(it.fecha) }
            .toSortedMap(reverseOrder())

        // 3) Construir las filas: encabezado (con total del mes) + sus gastos
        val filas = mutableListOf<FilaLista>()
        for ((clave, lista) in porMes) {
            filas += FilaLista.EncabezadoMes(Meses.nombreLegible(clave), lista.sumOf { it.monto })
            lista.forEach { filas += FilaLista.FilaGasto(it) }
        }
        adapter.mostrar(filas)

        // 4) Tarjeta de resumen: mes actual y mes anterior.
        //    Al cambiar de mes, "anterior" pasa a ser el mes que acaba de terminar.
        val claveActual = Meses.claveActual()
        val claveAnterior = Meses.claveAnterior()
        val totalActual = porMes[claveActual]?.sumOf { it.monto } ?: 0.0
        val totalAnterior = porMes[claveAnterior]?.sumOf { it.monto } ?: 0.0

        binding.tvMesActual.text = Meses.nombreLegible(claveActual)
        binding.tvTotalActual.text = formatearQuetzales(totalActual)
        binding.tvMesAnterior.text =
            "${Meses.nombreLegible(claveAnterior)}: ${formatearQuetzales(totalAnterior)}"

        actualizarPresupuesto(totalActual)
    }

    private fun actualizarPresupuesto(gastado: Double) {
        val presupuesto = Presupuesto.obtener(this)
        val attr = com.google.android.material.R.attr.colorOnSurface
        val colorNormal = MaterialColors.getColor(binding.tvDisponible, attr)
        val colorPrimario = MaterialColors.getColor(
            binding.progresoPresupuesto, com.google.android.material.R.attr.colorPrimary
        )
        val colorError = MaterialColors.getColor(
            binding.tvDisponible, com.google.android.material.R.attr.colorError
        )

        // La barra SIEMPRE se muestra; sin presupuesto queda vacía.
        if (presupuesto <= 0.0) {
            binding.tvPresupuesto.text = "Presupuesto: sin definir"
            binding.progresoPresupuesto.setIndicatorColor(colorPrimario)
            binding.progresoPresupuesto.setProgressCompat(0, true)
            binding.tvDisponible.text = "Pulsa el botón Presupuesto para definirlo"
            binding.tvDisponible.setTextColor(colorNormal)
            return
        }

        binding.tvPresupuesto.text = "Presupuesto: ${formatearQuetzales(presupuesto)}"

        val porcentaje = ((gastado / presupuesto) * 100).toInt().coerceIn(0, 100)
        binding.progresoPresupuesto.setProgressCompat(porcentaje, true)

        val restante = presupuesto - gastado
        if (restante >= 0) {
            binding.tvDisponible.text =
                "Gastado ${formatearQuetzales(gastado)} · Te quedan ${formatearQuetzales(restante)}"
            binding.tvDisponible.setTextColor(colorNormal)
            binding.progresoPresupuesto.setIndicatorColor(colorPrimario)
        } else {
            binding.tvDisponible.text = "Te pasaste por ${formatearQuetzales(-restante)}"
            binding.tvDisponible.setTextColor(colorError)
            binding.progresoPresupuesto.setIndicatorColor(colorError)
        }
    }

    private fun mostrarDialogoPresupuesto() {
        val campo = TextInputEditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Monto en Q"
            val actual = Presupuesto.obtener(this@MainActivity)
            if (actual > 0) setText(actual.toString())
        }
        val contenedor = FrameLayout(this).apply {
            val margen = (24 * resources.displayMetrics.density).toInt()
            setPadding(margen, 8, margen, 0)
            addView(campo)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Presupuesto mensual")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                // replace(',', '.') por si el teclado escribe coma decimal
                val valor = campo.text.toString().replace(',', '.').toDoubleOrNull()
                if (valor == null || valor < 0) {
                    Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                } else {
                    Presupuesto.guardar(this, valor)
                    lifecycleScope.launch { cargarGastos() }
                }
            }
            .setNeutralButton("Quitar") { _, _ ->
                Presupuesto.guardar(this, 0.0)
                lifecycleScope.launch { cargarGastos() }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirFormulario(id: Int?) {
        val intent = Intent(this, FormularioGastoActivity::class.java)
        if (id != null) {
            intent.putExtra(FormularioGastoActivity.EXTRA_ID_GASTO, id)
        }
        startActivity(intent)
    }

    private fun respetarBarrasDelSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.updatePadding(top = barras.top, bottom = barras.bottom)
            insets
        }
    }
}
