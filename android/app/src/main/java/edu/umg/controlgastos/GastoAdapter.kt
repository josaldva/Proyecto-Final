package edu.umg.controlgastos

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.umg.controlgastos.databinding.ItemGastoBinding
import edu.umg.controlgastos.databinding.ItemMesBinding
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.formatearQuetzales

/** Cada fila de la lista es un encabezado de mes o un gasto. */
sealed class FilaLista {
    data class EncabezadoMes(val titulo: String, val total: Double) : FilaLista()
    data class FilaGasto(val gasto: Gasto) : FilaLista()
}

class GastoAdapter(
    private val alTocarGasto: (Gasto) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filas: List<FilaLista> = emptyList()

    class MesViewHolder(val binding: ItemMesBinding) : RecyclerView.ViewHolder(binding.root)
    class GastoViewHolder(val binding: ItemGastoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = filas.size

    override fun getItemViewType(position: Int): Int = when (filas[position]) {
        is FilaLista.EncabezadoMes -> TIPO_MES
        is FilaLista.FilaGasto -> TIPO_GASTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TIPO_MES) {
            MesViewHolder(ItemMesBinding.inflate(inflater, parent, false))
        } else {
            GastoViewHolder(ItemGastoBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val fila = filas[position]) {
            is FilaLista.EncabezadoMes -> {
                val b = (holder as MesViewHolder).binding
                b.tvMes.text = fila.titulo
                b.tvTotalMes.text = formatearQuetzales(fila.total)
            }
            is FilaLista.FilaGasto -> {
                val b = (holder as GastoViewHolder).binding
                val gasto = fila.gasto
                b.tvDescripcion.text = gasto.descripcion
                b.tvDetalle.text = "${gasto.categoria} · ${gasto.fecha}"
                b.tvMonto.text = formatearQuetzales(gasto.monto)
                b.root.setOnClickListener { alTocarGasto(gasto) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun mostrar(nuevas: List<FilaLista>) {
        filas = nuevas
        notifyDataSetChanged()
    }

    private companion object {
        const val TIPO_MES = 0
        const val TIPO_GASTO = 1
    }
}
