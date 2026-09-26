package com.example.appteca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appteca3.ui.theme.AppTeca3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTeca3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Aquí arranca la pantalla principal conectada al ViewModel
                        PantallaAppTeca()
                    }
                }
            }
        }
    }
}

// ── PASO 3: Pantalla principal que maneja el estado, buscador y navegación condicional ──
@Composable
fun PantallaAppTeca(vm: AppTecaViewModel = viewModel()) {
    val lista by vm.listaVisible.collectAsStateWithLifecycle()
    val modoFav by vm.modoSoloFavoritas.collectAsStateWithLifecycle()

    var textoBusqueda by rememberSaveable { mutableStateOf("") }

    // Estado de navegación: si es null muestra la lista, si tiene una app muestra el detalle
    var appSeleccionada by remember { mutableStateOf<App?>(null) }

    if (appSeleccionada != null) {
        // ── PASO 2: Si hay una app seleccionada, mostramos el Detalle ──
        DetalleApp(
            app = appSeleccionada!!,
            onVolver = { appSeleccionada = null },
            onFavoritoClick = {
                vm.alternarFavorita(appSeleccionada!!)
                // Actualizamos la app local para reflejar el cambio al instante en la vista de detalle
                appSeleccionada = Catalogo.apps.find { it.id == appSeleccionada!!.id }
            }
        )
    } else {
        // Si no hay app seleccionada, mostramos la lista principal con buscador y filtro
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { nuevo ->
                    textoBusqueda = nuevo
                    vm.buscar(nuevo)
                },
                label = { Text("Buscar por nombre o categoría...") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Button(
                onClick = { vm.alternarModo() },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(if (modoFav) "★ Solo favoritas" else "☆ Todas")
            }

            ListaApps(
                apps = lista,
                onAppClick = { app ->
                    // Al hacer clic en una app, cambiamos el estado para abrir el detalle
                    appSeleccionada = app
                },
                onFavoritoClick = { app ->
                    vm.alternarFavorita(app)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ── PASO 2: Composable de la pantalla de Detalle ──
@Composable
fun DetalleApp(
    app: App,
    onVolver: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    // BackHandler intercepta el botón físico de "Atrás" del celular para volver a la lista
    BackHandler {
        onVolver()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = app.nombre, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = app.categoria, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = app.descripcion, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onFavoritoClick) {
            Text(if (app.esFavorita) "★ Quitar de favoritos" else "☆ Marcar como favorita")
        }
    }
}

@Composable
fun ListaApps(
    apps: List<App>,
    onAppClick: (App) -> Unit,
    onFavoritoClick: (App) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(apps, key = { it.id }) { app ->
            FilaApp(
                app = app,
                onClick = { onAppClick(app) },
                onFavoritoClick = { onFavoritoClick(app) }
            )
        }
    }
}

@Composable
fun FilaApp(
    app: App,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(app.nombre, style = MaterialTheme.typography.titleMedium)
            Text(app.categoria, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = if (app.esFavorita) "★" else "☆",
            fontSize = 24.sp,
            modifier = Modifier
                .clickable { onFavoritoClick() }
                .padding(8.dp)
        )
    }
}