package com.example.glog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glog.domain.repository.CollectionRepository
import com.example.glog.ui.state.CollectionEvent
import com.example.glog.ui.state.CollectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CollectionState())
    val state = _state.asStateFlow()

    fun onEvent(event: CollectionEvent) {
        when (event) {
            is CollectionEvent.LoadCollections -> loadCollections()
            is CollectionEvent.SearchCollections -> loadCollections(event.query)
            is CollectionEvent.SelectCollection -> selectCollection(event.collection)
            is CollectionEvent.UpdateCollection -> updateCollection(event.collection)
            is CollectionEvent.CreateCollection -> createCollection(event.name, event.description)
            is CollectionEvent.AddGamesToCollection -> addGamesToCollection(event.collection, event.games)
            is CollectionEvent.DeleteCollection -> deleteCollection(event.collection)
        }
    }

    private fun deleteCollection(collection: com.example.glog.domain.model.Collection) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            collectionRepository.deleteCollection(collection.id.toLong()).fold(
                onSuccess = {
                    val newCollections = _state.value.collections.filter { it.id != collection.id }
                    _state.value = _state.value.copy(
                        collections = newCollections,
                        selectedCollection = if (_state.value.selectedCollection?.id == collection.id) null else _state.value.selectedCollection,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun addGamesToCollection(collection: com.example.glog.domain.model.Collection, games: List<com.example.glog.domain.model.Game>) {
        val newGames = games.filter { g -> !collection.games.any { it.id == g.id } }
        if (newGames.isEmpty()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            var allOk = true
            for (game in newGames) {
                collectionRepository.addGameToCollection(collection.id.toLong(), game.id).fold(
                    onSuccess = { },
                    onFailure = {
                        _state.value = _state.value.copy(
                            error = it.message,
                            isLoading = false
                        )
                        allOk = false
                        return@launch
                    }
                )
            }
            if (allOk) {
                loadCollectionDetail(collection.id.toLong())
            }
        }
    }

    private fun createCollection(name: String, description: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val newCollection = com.example.glog.domain.model.Collection(
                id = 0,
                name = name,
                description = description,
                gameIds = emptyList(),
                games = emptyList()
            )
            collectionRepository.createCollection(newCollection).fold(
                onSuccess = { created ->
                    _state.value = _state.value.copy(
                        collections = _state.value.collections + created,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun updateCollection(collection: com.example.glog.domain.model.Collection) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            collectionRepository.updateCollection(collection.id.toLong(), collection).fold(
                onSuccess = { updated ->
                    _state.value = _state.value.copy(
                        selectedCollection = updated,
                        collections = _state.value.collections.map { if (it.id == updated.id) updated else it },
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun loadCollections(search: String? = _state.value.searchQuery) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            collectionRepository.getCollections(search).fold(
                onSuccess = { collections ->
                    _state.value = _state.value.copy(
                        collections = collections,
                        searchQuery = search,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    private fun selectCollection(collection: com.example.glog.domain.model.Collection?) {
        _state.value = _state.value.copy(selectedCollection = collection)
    }

    fun loadCollectionDetail(id: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            collectionRepository.getCollectionById(id).fold(
                onSuccess = { collection ->
                    _state.value = _state.value.copy(
                        selectedCollection = collection,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }
}
