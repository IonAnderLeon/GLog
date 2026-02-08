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
