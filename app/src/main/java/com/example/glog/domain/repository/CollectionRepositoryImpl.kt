package com.example.glog.domain.repository

import com.example.glog.data.mapper.CollectionMapper
import com.example.glog.data.network.api.GLogApiService
import com.example.glog.data.network.dto.AddGameToCollectionDTO
import com.example.glog.domain.model.Collection
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val apiService: GLogApiService,
    private val collectionMapper: CollectionMapper
) : CollectionRepository {

    override suspend fun getCollections(search: String?): Result<List<Collection>> {
        return try {
            val dtos = apiService.getCollections(search)
            val collections = dtos.map { collectionMapper.toEntity(it) }
            Result.success(collections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCollectionById(id: Long): Result<Collection> {
        return try {
            val dto = apiService.getCollectionById(id)
            val collection = collectionMapper.toEntity(dto)
            Result.success(collection)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCollection(collection: Collection): Result<Collection> {
        return try {
            val dto = collectionMapper.toDto(collection)
            val response = apiService.createCollection(dto)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val created = collection.copy(
                        id = body.idCollection ?: 0,
                        name = body.name ?: collection.name,
                        description = body.description ?: collection.description
                    )
                    Result.success(created)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCollection(id: Long, collection: Collection): Result<Collection> {
        return try {
            val dto = collectionMapper.toDto(collection)
            val response = apiService.updateCollection(id, dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(collection)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCollection(id: Long): Result<Collection> {
        return try {
            val response = apiService.deleteCollection(id)
            if (response.isSuccessful) {
                Result.success(Collection(id = id.toInt(), name = "", description = null))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addGameToCollection(collectionId: Long, gameId: Int): Result<Unit> {
        return try {
            val response = apiService.addGameToCollection(
                collectionId = collectionId,
                body = AddGameToCollectionDTO(idGame = gameId)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeGameFromCollection(collectionId: Long, gameId: Int): Result<Unit> {
        return try {
            val response = apiService.removeGameFromCollection(
                collectionId = collectionId,
                gameId = gameId
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
