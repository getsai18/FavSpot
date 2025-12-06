package com.ejemplo.favspot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { userId ->
                    navController.navigate("home/$userId")
                },
                onRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("login")
            })
        }

        composable(
            route = "home/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            HomeScreen(
                userId = userId,
                onAddPlace = {
                    navController.navigate("addPlace/$userId")
                },
                onEditPlace = { placeId ->
                    navController.navigate("editPlace/$placeId")
                },
                onViewMap = {
                    navController.navigate("map/$userId")
                }
            )
        }

        composable(
            route = "addPlace/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0

            AddPlaceScreen(
                userId = userId,
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "editPlace/{placeId}",
            arguments = listOf(navArgument("placeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getInt("placeId") ?: 0

            EditPlaceScreen(
                placeId = placeId,
                onUpdated = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "map/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStack ->
            val userId = backStack.arguments?.getInt("userId") ?: 0

            var places by remember { mutableStateOf(listOf<Place>()) }

            LaunchedEffect(userId) {
                places = RetrofitClient.instance.getPlaces(userId)
            }

            MapScreen(places)
        }
    }
}