package com.example.appteca3

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppTecaViewModel : ViewModel() {

    // ── El estado interno ──
    private var query = ""
    private var soloFavoritas = false

    // ── El estado publicado con StateFlow (reemplaza a LiveData) ──
    private val _listaVisible = MutableStateFlow<List<App>>(emptyList())
    val listaVisible: StateFlow<List<App>> = _listaVisible

    private val _modoSoloFavoritas = MutableStateFlow(false)
    val modoSoloFavoritas: StateFlow<Boolean> = _modoSoloFavoritas

    init {
        Log.d("VIDA", "ViewModel → creado (${hashCode()})")
        aplicarFiltros()
    }

    // ── Los eventos que la pantalla puede avisar ──
    fun buscar(texto: String) {
        query = texto.trim()
        aplicarFiltros()
    }

    fun alternarModo() {
        soloFavoritas = !soloFavoritas
        aplicarFiltros()
    }

    fun alternarFavorita(app: App) {
        // En Compose no mutamos el objeto suelto; reemplazamos con una copia en la lista
        val nuevas = Catalogo.apps.map {
            if (it.id == app.id) it.copy(esFavorita = !it.esFavorita) else it
        }
        Catalogo.apps.clear()
        Catalogo.apps.addAll(nuevas)
        aplicarFiltros()
    }

    // ── La función de filtrado adaptada ──
    private fun aplicarFiltros() {
        var lista: List<App> = Catalogo.apps.toList()

        if (query.isNotEmpty()) {
            lista = lista.filter {
                it.nombre.contains(query, true) || it.categoria.contains(query, true)
            }
        }

        if (soloFavoritas) {
            lista = lista.filter { it.esFavorita }
        }

        _listaVisible.value = lista
        _modoSoloFavoritas.value = soloFavoritas
    }

    override fun onCleared() {
        Log.d("VIDA", "ViewModel → onCleared (destruido de verdad)")
    }
}