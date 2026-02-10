package com.example.glog.domain.repository

import com.example.glog.data.mapper.UserMapper
import com.example.glog.data.network.api.GLogApiService
import com.example.glog.data.network.dto.UserDTO
import com.example.glog.domain.model.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: GLogApiService,
    private val userMapper: UserMapper
) : UserRepository {

    override suspend fun getUsers(search: String?): Result<List<User>> {
        return try {
            val dtos = apiService.getUsers(search)
            val users = dtos.map { userMapper.toEntity(it) }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(id: Long): Result<User> {
        return try {
            val dto = apiService.getUserById(id)
            val user = userMapper.toEntity(dto)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(id: Long, nickname: String?, image: String?): Result<User> {
        return try {
            val dto = UserDTO.forUpdate(id.toInt(), nickname, image)
            val response = apiService.updateUser(id, dto)
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(userMapper.toEntity(body!!))
            } else {
                Result.failure(Exception("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}