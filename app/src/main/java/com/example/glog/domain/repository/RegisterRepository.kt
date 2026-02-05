package com.example.glog.domain.repository

import com.example.glog.domain.model.Register

interface RegisterRepository {
    suspend fun getRegisters(search: String? = null): Result<List<Register>>
    suspend fun getRegistersByUser(userId: Long): Result<List<Register>>
    suspend fun createRegister(register: Register): Result<Register>
    suspend fun deleteRegister(register: Register): Result<Register>
    suspend fun updateRegister(register: Register): Result<Register>
}