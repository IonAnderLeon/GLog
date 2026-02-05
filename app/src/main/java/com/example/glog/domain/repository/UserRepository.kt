package com.example.glog.domain.repository

import com.example.glog.domain.model.User

interface UserRepository {
    suspend fun getUsers(search: String? = null): Result<List<User>>
    suspend fun getUserById(id: Long): Result<User>
}