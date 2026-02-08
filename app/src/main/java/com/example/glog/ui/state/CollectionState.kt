package com.example.glog.ui.state

import com.example.glog.domain.model.Collection

data class CollectionState(
    val collections: List<Collection> = emptyList(),
    val selectedCollection: Collection? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String? = null
)

sealed class CollectionEvent {
    object LoadCollections : CollectionEvent()
    data class SearchCollections(val query: String?) : CollectionEvent()
    data class SelectCollection(val collection: Collection?) : CollectionEvent()
    data class UpdateCollection(val collection: Collection) : CollectionEvent()
}
