package edu.umg.controlgastos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GastoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoEntity)

    @Update
    suspend fun actualizar(gasto: GastoEntity)

    @Query("DELETE FROM tabla_gastos WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    @Query("SELECT * FROM tabla_gastos WHERE id = :id")
    suspend fun buscarPorId(id: Int): GastoEntity?

    // Más recientes primero: la fecha "yyyy-MM-dd" se ordena bien como texto.
    @Query("SELECT * FROM tabla_gastos ORDER BY fecha DESC, id DESC")
    suspend fun obtenerTodos(): List<GastoEntity>
}
