package edu.umg.controlgastos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val categoria: String,   // se guarda el name del enum Categoria
    val fecha: String        // "yyyy-MM-dd"
)
