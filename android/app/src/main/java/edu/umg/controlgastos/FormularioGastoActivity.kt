package edu.umg.controlgastos

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.umg.controlgastos.databinding.ActivityFormularioGastoBinding
import edu.umg.programacion2.gastos.modelo.Categoria
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FormularioGastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioGastoBinding
    private lateinit var db: AppDatabase
    private var gastoEditandoId: Int = 0
    private var fechaOriginal: String? = null   // se conserva al editar, para no mover el gasto de mes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioGastoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        respetarBarrasDelSistema()

        db = AppDatabase.getDatabase(this)

        binding.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Categoria.entries
        )

        val id = intent.getIntExtra(EXTRA_ID_GASTO, 0)
        if (id != 0) {
            gastoEditandoId = id
            binding.btnEliminar.visibility = View.VISIBLE   // solo se puede eliminar un gasto existente
            lifecycleScope.launch { cargarGasto(id) }
        }

        binding.btnGuardar.setOnClickListener { guardar() }
        binding.btnCancelar.setOnClickListener { finish() }   // salir sin guardar
        binding.btnEliminar.setOnClickListener { confirmarEliminar() }
    }

    private suspend fun cargarGasto(id: Int) {
        val gasto = db.gastoDao().buscarPorId(id)
        if (gasto == null) {
            Toast.makeText(this, "No existe el gasto #$id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvTitulo.text = "Editar gasto"
        binding.btnGuardar.text = "Guardar cambios"
        fechaOriginal = gasto.fecha
        binding.etDescripcion.setText(gasto.descripcion)
        binding.etMonto.setText(gasto.monto.toString())

        val categoriaEnum = Categoria.valueOf(gasto.categoria)
        binding.spCategoria.setSelection(categoriaEnum.ordinal)
    }

    private fun guardar() {
        val descripcion = binding.etDescripcion.text.toString().trim()
        val monto = binding.etMonto.text.toString().toDoubleOrNull()

        binding.tilDescripcion.error = if (descripcion.isEmpty()) "Escribe una descripción" else null
        binding.tilMonto.error = if (monto == null) "Ingresa un monto válido" else null
        if (descripcion.isEmpty() || monto == null) return

        val categoria = binding.spCategoria.selectedItem as Categoria

        lifecycleScope.launch {
            if (gastoEditandoId == 0) {
                // INSERTAR NUEVO GASTO EN SQLITE
                val nuevoGasto = GastoEntity(
                    descripcion = descripcion,
                    monto = monto,
                    categoria = categoria.name,
                    fecha = fechaDeHoy()
                )
                db.gastoDao().insertar(nuevoGasto)
            } else {
                // ACTUALIZAR GASTO EN SQLITE
                val gastoActualizado = GastoEntity(
                    id = gastoEditandoId,
                    descripcion = descripcion,
                    monto = monto,
                    categoria = categoria.name,
                    fecha = fechaOriginal ?: fechaDeHoy()
                )
                db.gastoDao().actualizar(gastoActualizado)
            }

            avisarSiExcedePresupuesto()
            Toast.makeText(this@FormularioGastoActivity, "Gasto guardado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun confirmarEliminar() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar gasto")
            .setMessage("¿Seguro que quieres eliminar este gasto? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    db.gastoDao().eliminarPorId(gastoEditandoId)
                    Toast.makeText(this@FormularioGastoActivity, "Gasto eliminado", Toast.LENGTH_SHORT).show()
                    finish()   // al volver, onResume() recarga la lista y recalcula totales y presupuesto
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private suspend fun avisarSiExcedePresupuesto() {
        val presupuesto = Presupuesto.obtener(this)
        if (presupuesto <= 0.0) return

        val mesActual = Meses.claveActual()
        val gastado = db.gastoDao().obtenerTodos()
            .filter { Meses.claveDeFecha(it.fecha) == mesActual }
            .sumOf { it.monto }

        if (gastado > presupuesto) {
            Toast.makeText(this, "Superaste tu presupuesto mensual", Toast.LENGTH_LONG).show()
        }
    }

    private fun fechaDeHoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun respetarBarrasDelSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.updatePadding(top = barras.top, bottom = barras.bottom)
            insets
        }
    }

    companion object {
        const val EXTRA_ID_GASTO = "id_gasto"
    }
}