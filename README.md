# ControlGastos

Aplicación Android desarrollada en Kotlin para registrar, consultar y administrar gastos personales en quetzales (Q). Los gastos se organizan por mes y la aplicación permite establecer un presupuesto mensual. La información se almacena localmente en el dispositivo, por lo que las funciones principales pueden utilizarse sin conexión a internet.

Proyecto del curso de **Programación 2** de la **Universidad Mariano Gálvez de Guatemala (UMG)**.

## Autor

- **Nombre:** Jose Julian Aldana Valle
- **Carnet:** 1690-24-11323
- **Universidad:** Universidad Mariano Gálvez de Guatemala (UMG)
- **Curso:** Programación 2

## Repositorio

[https://github.com/josaldva/Proyecto-Final](https://github.com/josaldva/Proyecto-Final)

## Funciones principales

- Mostrar los gastos en una lista agrupada por mes. Cada mes incluye un encabezado con el total gastado.
- Consultar el total de gastos del mes actual y del mes anterior.
- Registrar gastos indicando descripción, monto y categoría: Alimentación, Transporte, Estudios, Entretenimiento, Salud y Otros.
- Editar gastos existentes. Al modificarlos se conserva su fecha original, por lo que permanecen asociados al mismo mes.
- Eliminar gastos con una confirmación previa.
- Definir, modificar o quitar un presupuesto mensual.
- Visualizar una barra de progreso del presupuesto, que indica cuánto se ha gastado, cuánto queda o si se ha excedido.
- Mostrar un aviso cuando se guarda un gasto que supera el presupuesto mensual.
- Guardar la información de forma persistente en una base de datos local SQLite mediante Room.
- Alternar entre modo claro y modo oscuro, conservando la preferencia seleccionada.

## Flujo básico de la aplicación

1. **Pantalla principal:** muestra la tarjeta del presupuesto, el resumen de gastos del mes actual y del mes anterior, y la lista de gastos agrupados por mes.
2. **Presupuesto:** permite establecer, actualizar o quitar el monto del presupuesto mensual.
3. **Nuevo gasto:** el formulario permite ingresar la descripción, el monto y la categoría. Al guardar, la aplicación valida los datos y actualiza la lista.
4. **Editar gasto:** al seleccionar un gasto se abre el formulario con sus datos para modificarlos.
5. **Eliminar gasto:** desde el formulario de edición se puede eliminar el registro después de confirmar la acción.
6. **Modo oscuro:** el control de tema de la barra superior permite alternar entre la apariencia clara y oscura.

## Tecnologías utilizadas

| Área | Tecnología |
|---|---|
| Lenguaje de programación | Kotlin |
| Interfaz | Vistas XML, View Binding, Material Components 3 y RecyclerView |
| Base de datos local | SQLite con Room 2.8.4 |
| Procesamiento de anotaciones | KSP 2.3.11 |
| Preferencias | SharedPreferences |
| Concurrencia | Corrutinas de Kotlin (`suspend` y `lifecycleScope`) |
| Lógica de dominio | Librería propia `gastos-core` (`umg.edu.gt.progra2:gastos-core:1.1.0`) |
| Compilación Android | Android Gradle Plugin 9.4.1 y Gradle 9.6.0 |
| Versión mínima de Android | API 24 |
| SDK objetivo | API 37 |

## Estructura del proyecto

El repositorio está organizado en dos partes principales: `android`, que contiene la aplicación móvil, y `gastos-core`, que contiene la librería con las clases del dominio.

```text
Proyecto-Final/
├── android/
│   ├── app/
│   │   └── src/
│   │       └── main/
│   │           ├── java/
│   │           │   └── edu/umg/controlgastos/
│   │           │       ├── ControlGastosApp.kt
│   │           │       ├── MainActivity.kt
│   │           │       ├── FormularioGastoActivity.kt
│   │           │       ├── GastoAdapter.kt
│   │           │       ├── Meses.kt
│   │           │       ├── Presupuesto.kt
│   │           │       ├── Tema.kt
│   │           │       ├── AppDatabase.kt
│   │           │       ├── GastoDao.kt
│   │           │       └── GastoEntity.kt
│   │           └── res/
│   │               ├── layout/
│   │               ├── values/
│   │               ├── values-night/
│   │               ├── menu/
│   │               └── drawable/
│   ├── build.gradle.kts
│   └── settings.gradle.kts
│
├── gastos-core/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── kotlin/
│
└── README.md
```

### Descripción de los archivos principales

Dentro de `android/app/src/main/java/edu/umg/controlgastos/`:

| Archivo | Responsabilidad |
|---|---|
| `ControlGastosApp.kt` | Inicializa la aplicación y aplica el tema guardado. |
| `MainActivity.kt` | Gestiona la pantalla principal, el presupuesto, los resúmenes y la lista de gastos. |
| `FormularioGastoActivity.kt` | Permite crear, editar y eliminar gastos. |
| `GastoAdapter.kt` | Presenta los encabezados de mes y los gastos en el RecyclerView. |
| `Meses.kt` | Contiene funciones para identificar el mes actual y el anterior. |
| `Presupuesto.kt` | Gestiona la lectura y el almacenamiento del presupuesto mensual. |
| `Tema.kt` | Guarda y aplica la preferencia de modo claro u oscuro. |
| `AppDatabase.kt` | Define la base de datos Room (`gastos_db`). |
| `GastoDao.kt` | Contiene las operaciones para insertar, actualizar, eliminar, buscar y listar gastos. |
| `GastoEntity.kt` | Define la entidad correspondiente a la tabla `tabla_gastos`. |

La carpeta `android/app/src/main/res/` contiene los recursos de interfaz, como diseños XML, colores, temas, menús e iconos.

La carpeta `gastos-core/` contiene la librería Kotlin que proporciona las clases del dominio, como `Gasto` y `Categoria`, y no depende de las clases de Android.

## Requisitos

- Android Studio reciente, compatible con la versión de Android Gradle Plugin utilizada por el proyecto.
- JDK compatible con la configuración de Gradle del proyecto.
- Maven instalado para compilar e instalar la librería `gastos-core`.
- Un emulador o dispositivo Android con versión 7.0 (API 24) o superior.
- Conexión a internet durante la primera sincronización de Gradle y para descargar las dependencias necesarias.

## Instalación y ejecución

### 1. Clonar el repositorio

Abre una terminal y ejecuta:

```bash
git clone https://github.com/josaldva/Proyecto-Final.git
cd Proyecto-Final
```

### 2. Instalar la librería `gastos-core`

La aplicación utiliza la librería `umg.edu.gt.progra2:gastos-core:1.1.0`, que debe estar disponible en el repositorio Maven local.

Desde la carpeta raíz del proyecto, ejecuta:

```bash
cd gastos-core
mvn install
```

Cuando Maven termine correctamente, regresa a la carpeta raíz:

```bash
cd ..
```

### 3. Abrir el proyecto Android

1. Abre Android Studio.
2. Selecciona **Open**.
3. Busca y selecciona la carpeta `android` del repositorio.
4. Espera a que finalice la sincronización de Gradle.
5. Si aparece un error indicando que no encuentra `gastos-core`, verifica que el comando `mvn install` haya terminado correctamente.

### 4. Ejecutar la aplicación

1. Selecciona un emulador o conecta un dispositivo Android.
2. Espera a que el dispositivo esté disponible.
3. Presiona **Run** en Android Studio.

## Prueba de agrupación por mes (opcional)

La aplicación registra los gastos con la fecha actual. Para comprobar la agrupación de gastos de meses diferentes, se pueden insertar registros de prueba desde **View > Tool Windows > App Inspection > Database Inspector**.

Selecciona la base de datos y ejecuta:

```sql
INSERT INTO tabla_gastos (descripcion, monto, categoria, fecha) VALUES
('Supermercado', 320.75, 'ALIMENTACION', '2026-09-03'),
('Bus', 25.00, 'TRANSPORTE', '2026-09-14');
```

Los valores de categoría deben coincidir con los nombres del enum:

```text
ALIMENTACION
TRANSPORTE
ESTUDIOS
ENTRETENIMIENTO
SALUD
OTROS
```

La fecha debe utilizar el formato `yyyy-MM-dd`.

## Generar el APK

Para generar el archivo APK desde Android Studio:

1. Abre el proyecto Android.
2. Selecciona **Build > Generate App Bundles or APKs > Generate APKs**.
3. Espera a que finalice la compilación.

En una compilación de depuración, el APK se genera normalmente en:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

---

**Proyecto académico — Programación 2**  
**Universidad Mariano Gálvez de Guatemala (UMG)**  
**Jose Julian Aldana Valle — Carnet 1690-24-11323**
