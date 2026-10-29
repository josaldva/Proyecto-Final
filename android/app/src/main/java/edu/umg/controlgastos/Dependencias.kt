package edu.umg.controlgastos

import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria
import edu.umg.programacion2.gastos.servicio.ControlGastos

object Dependencias {
    val control = ControlGastos(GastoRepositoryEnMemoria())
}