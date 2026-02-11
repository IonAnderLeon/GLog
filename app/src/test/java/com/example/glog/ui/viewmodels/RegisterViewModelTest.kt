package com.example.glog.ui.viewmodels

import com.example.glog.MainCoroutineRule
import com.example.glog.domain.model.Register
import com.example.glog.domain.repository.RegisterRepository
import com.example.glog.ui.state.RegisterEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val registerRepository: RegisterRepository = mockk()
    private lateinit var viewModel: RegisterViewModel

    @Test
    fun onEvent_LoadRegisters_success_setsRegisters() = runTest {
        val registers = listOf(
            Register(1, "2024-01-01", 2.0, 10, "Zelda", null, 1, "User")
        )
        coEvery { registerRepository.getRegisters(null) } returns Result.success(registers)
        viewModel = RegisterViewModel(registerRepository)

        viewModel.onEvent(RegisterEvent.LoadRegisters)

        val state = viewModel.state.first()
        assertFalse(state.isLoading)
        assertEquals(1, state.registers.size)
        assertEquals("Zelda", state.registers.first().gameName)
    }

    @Test
    fun onEvent_LoadRegisters_failure_setsError() = runTest {
        coEvery { registerRepository.getRegisters(null) } returns Result.failure(RuntimeException("Error"))
        viewModel = RegisterViewModel(registerRepository)

        viewModel.onEvent(RegisterEvent.LoadRegisters)

        val state = viewModel.state.first()
        assertFalse(state.isLoading)
        assertEquals("Error", state.error)
    }

    @Test
    fun onEvent_SearchRegisters_callsRepositoryWithQuery() = runTest {
        coEvery { registerRepository.getRegisters("zelda") } returns Result.success(emptyList())
        viewModel = RegisterViewModel(registerRepository)

        viewModel.onEvent(RegisterEvent.SearchRegisters("zelda"))

        val state = viewModel.state.first()
        assertEquals("zelda", state.searchQuery)
    }
}
