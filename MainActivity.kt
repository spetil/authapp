package com.example.authapp
import androidx.compose.runtime.*


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authapp.repository.AuthRepository
import com.example.authapp.remote.RetrofitClient
import com.example.authapp.ui.screen.HomeScreen
import com.example.authapp.ui.screen.LoginScreen
import com.example.authapp.ui.screen.RegisterScreen
import com.example.authapp.ui.theme.AuthappTheme
import com.example.authapp.viewmodel.AuthViewModel
import com.example.authapp.viewmodel.AuthState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cria o ViewModel com o repositório corretamente instanciado
        val repository = AuthRepository(
            api = RetrofitClient.instance.create(com.example.authapp.remote.AuthApi::class.java),
            context = applicationContext
        )
        val viewModel = AuthViewModel(repository)

        setContent {
            AuthappTheme {
                // Estado atual de autenticação
                val state = viewModel.authState.collectAsState().value

                // Gerencia transições simples sem NavController
                var currentScreen = remember { mutableStateOf("login") }

                when {
                    state is AuthState.Authenticated -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onLogout = { currentScreen.value = "login" }
                        )
                    }
                    currentScreen.value == "register" -> {
                        RegisterScreen(
                            viewModel = viewModel,
                            onRegisterSuccess = { currentScreen.value = "login" },
                            onNavigateToLogin = { currentScreen.value = "login" }
                        )
                    }
                    else -> {
                        LoginScreen(
                            viewModel = viewModel,
                            onAuthenticated = { /* nada, o estado já muda automaticamente */ },
                            onNavigateToRegister = { currentScreen.value = "register" }
                        )
                    }
                }
            }
        }

        // Ao abrir, verifica se há token válido salvo
        viewModel.checkAuth()
    }
}
