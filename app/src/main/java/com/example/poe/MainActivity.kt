package com.example.poe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.poe.data.AppDatabase
import com.example.poe.data.FinanceRepository
import com.example.poe.data.UserRepository
import com.example.poe.ui.*
import com.example.poe.ui.theme.POETheme
import com.example.poe.util.AppLogger

/**
 * Main entry point of the POE application.
 * This activity initializes the database, repositories, and ViewModels.
 * it also sets up the Jetpack Compose Navigation Graph to handle all screen transitions.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Log application startup for debugging purposes
        AppLogger.i("MainActivity started - Initializing Room DB and ViewModel factories", "Main")
        super.onCreate(savedInstanceState)
        
        // Initialize the local database and repositories
        val database = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(database.userDao())
        val financeRepository = FinanceRepository(database.transactionDao(), database.categoryDao())
        
        // Setup ViewModel factories to inject dependencies into ViewModels
        val authViewModelFactory = AuthViewModelFactory(userRepository)
        val financeViewModelFactory = FinanceViewModelFactory(financeRepository)

        enableEdgeToEdge()
        setContent {
            POETheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
                val financeViewModel: FinanceViewModel = viewModel(factory = financeViewModelFactory)
                
                // Observe the current user to pass relevant info to other components
                val currentUser by authViewModel.currentUser.collectAsState()
                
                // Automatically set the userId in financeViewModel when a user logs in
                currentUser?.let { financeViewModel.setUserId(it.id) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Navigation Host defining all the app routes and their respective screens
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onRegisterSuccess = {
                                    navController.navigate("login")
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = authViewModel,
                                financeViewModel = financeViewModel,
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                },
                                onNavigateToExpenses = {
                                    navController.navigate("expense_options")
                                },
                                onNavigateToIncome = {
                                    navController.navigate("income_options")
                                },
                                onNavigateToAllTransactions = {
                                    navController.navigate("all_transactions")
                                },
                                onNavigateToSetGoals = {
                                    navController.navigate("set_goals")
                                }
                            )
                        }
                        composable("expense_options") {
                            ExpenseOptionsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToLogExpense = { navController.navigate("log_transaction/true/General") },
                                onNavigateToTrackExpense = { navController.navigate("track_transaction/true") },
                                onNavigateToCreateCategory = { navController.navigate("create_category/false") }
                            )
                        }
                        composable("income_options") {
                            IncomeOptionsScreen(
                                onBack = { navController.popBackStack() },
                                onNavigateToLogIncome = {
                                    navController.navigate("log_transaction/false/General")
                                },
                                onNavigateToViewIncomeList = { navController.navigate("track_transaction/false") }
                            )
                        }
                        composable(
                            route = "log_transaction/{isExpense}/{accountType}?category={category}",
                            arguments = listOf(
                                navArgument("isExpense") { type = NavType.BoolType },
                                navArgument("accountType") { type = NavType.StringType },
                                navArgument("category") { nullable = true; defaultValue = null }
                            )
                        ) { backStackEntry ->
                            val isExpense = backStackEntry.arguments?.getBoolean("isExpense") ?: true
                            val accountType = backStackEntry.arguments?.getString("accountType") ?: "General"
                            val category = backStackEntry.arguments?.getString("category")
                            
                            LogTransactionScreen(
                                financeViewModel = financeViewModel,
                                isExpense = isExpense,
                                accountType = accountType,
                                preSelectedCategoryName = category,
                                onBack = { navController.popBackStack() },
                                onSuccess = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "track_transaction/{isExpense}",
                            arguments = listOf(navArgument("isExpense") { type = NavType.BoolType })
                        ) { backStackEntry ->
                            val isExpense = backStackEntry.arguments?.getBoolean("isExpense") ?: true
                            TrackTransactionScreen(
                                financeViewModel = financeViewModel,
                                isExpense = isExpense,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("all_transactions") {
                            AllTransactionsScreen(
                                financeViewModel = financeViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "create_category/{isIncome}",
                            arguments = listOf(navArgument("isIncome") { type = NavType.BoolType })
                        ) { backStackEntry ->
                            val isIncome = backStackEntry.arguments?.getBoolean("isIncome") ?: false
                            CreateCategoryScreen(
                                financeViewModel = financeViewModel,
                                isIncome = isIncome,
                                onBack = { navController.popBackStack() },
                                onSuccess = { navController.popBackStack() }
                            )
                        }
                        composable("set_goals") {
                            SetGoalsScreen(
                                viewModel = authViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
