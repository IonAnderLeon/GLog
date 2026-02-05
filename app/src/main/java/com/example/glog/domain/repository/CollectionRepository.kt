package com.example.glog.domain.repository

import com.example.glog.domain.model.Collection

interface CollectionRepository {
    suspend fun getCollections(search: String? = null): Result<List<Collection>>
    suspend fun getCollectionById(id: Long): Result<Collection>
    suspend fun createCollection(collection: Collection): Result<Collection>
    suspend fun deleteCollection(id: Long): Result<Collection>
    suspend fun updateCollection(id: Long): Result<Collection>

}