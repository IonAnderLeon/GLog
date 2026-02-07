package com.example.glog.domain.repository

import com.example.glog.data.mapper.RegisterMapper
import com.example.glog.data.network.api.GLogApiService
import com.example.glog.domain.model.Register
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val apiService: GLogApiService,
    private val registerMapper: RegisterMapper
) : RegisterRepository {

    override suspend fun getRegisters(search: String?): Result<List<Register>> {
        return try {
            val dtos = apiService.getRegisters(search)
            val registers = dtos.map { registerMapper.toEntity(it) }
            Result.success(registers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRegistersByUser(userId: Long): Result<List<Register>> {
        return try {
            val dtos = apiService.getRegistersByUser(userId)
            val registers = dtos.map { registerMapper.toEntity(it) }
            Result.success(registers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRegister(register: Register): Result<Register> {
        return try {
            val dto = registerMapper.toDto(register)
            val response = apiService.createRegister(dto)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val created = register.copy(
                        id = body.idRegister ?: 0,
                        date = body.date,
                        playtime = body.playtime,
                        gameId = body.idGame,
                        userId = body.idUsuario
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

    override suspend fun updateRegister(register: Register): Result<Register> {
        return try {
            val dto = registerMapper.toDto(register)
            val response = apiService.updateRegister(register.id.toLong(), dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(register)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRegister(register: Register): Result<Register> {
        return try {
            val response = apiService.deleteRegister(register.id.toLong())
            if (response.isSuccessful) {
                Result.success(register)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
