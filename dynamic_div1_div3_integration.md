---
tipo: ai-log
projeto: PillGo
plataforma: wear_os_kotlin
fase: DYNAMIC_UI_PROFILE_LOGOUT
data: 2026-04-29
status: implementado_com_sucesso
---

# Log: Dynamic Profile & Logout Integration (Div 1 & Div 3)

## Contexto
Com a máquina de estados de autenticação 100% validada, esta sprint conectou os dados reais do usuário à interface gráfica e implementou o fluxo de logout reativo, mantendo o `VerticalPager` V2 completamente intocado.

---

## Arquitetura das Mudanças

### Service Layer — `SupabaseAuthManager.kt`
Adicionados dois novos métodos ao manager:
- **`logout()`**: executa `supabase.auth.signOut()` dentro de um `try/finally`, garantindo que o `_state` retorne para `AuthState.Idle` mesmo em caso de erro de rede.
- **`getUserProfile()`**: lê `currentUserOrNull()?.userMetadata?.get("full_name")`, trimando aspas do JSON serializado. Retorna `"Usuário"` como fallback seguro.

### ViewModel — `MainViewModel.kt` (NOVO)
Expõe para a UI dois `StateFlow` sem lógica de negócio inline:
- `userName: StateFlow<String>` — carregado no `init {}` via `fetchUserProfile()`.
- `loggedOut: StateFlow<Boolean>` — sinaliza para a UI quando o logout foi concluído.

A função `logout()` executa o manager e seta `_loggedOut.value = true`, sendo a única ação de negócio exposta.

### `HomeScreen.kt` (REFATORADO)
Cola de ligação entre navegação e `MainClockScreen`:
```kotlin
val userName by vm.userName.collectAsState()
val loggedOut by vm.loggedOut.collectAsState()

LaunchedEffect(loggedOut) {
    if (loggedOut) {
        onLoggedOut()  // callback vindo da MainActivity
    }
}

MainClockScreen(
    userName = userName,
    onLogout = { vm.logout() }
)
```

### `MainClockScreen.kt` (MODIFICADO — apenas assinatura)
Os dois parâmetros novos (`userName: String`, `onLogout: () -> Unit`) foram adicionados sem tocar na paleta V2, no `VerticalPager` ou nos indicadores de página.

- **Div 1 (`PageInicio`)**: o `Text("Roager")` hardcoded foi substituído por `Text(userName, modifier = Modifier.testTag("userName"))`.
- **Div 3 (`PageFim`)**: o botão `SAIR` passou de `onClick = { /* Sem lógica */ }` para `onClick = onLogout`.

### Rota de Logout (`MainActivity.kt`)
```kotlin
composable("home") {
    HomeScreen(
        onLoggedOut = {
            navController.navigate("pairing") {
                popUpTo("home") { inclusive = true }  // Destroi Home da pilha
            }
        }
    )
}
```
O usuário é **ejetado de volta para a `PairingScreen`** e a `HomeScreen` é removida da pilha, impedindo que o botão de voltar do Wear OS retorne para uma tela de usuário logado.

---

## Fix de Estabilidade — `supabaseClient` (`lazy`)
O SDK inicializava-se estaticamente ao carregar a classe. No ambiente de testes instrumentados o `local.properties` não está presente, causando um crash de `Process` antes de qualquer teste ser executado ("0 tests ran"). A correção foi converter o `val` para `val supabaseClient by lazy { ... }`, adiando a inicialização para o primeiro acesso real (em runtime, não em tempo de carga de classe). O `testInstrumentationRunner` também foi explicitamente configurado no `build.gradle.kts`.

---

## Testes de Regressão (Obrigatório — GREEN)

### Mock Strategy
O teste instrumentado não pode chamar Supabase. O `MainClockScreen` recebe `userName` como parâmetro simples (`String`), então o mock é trivial: basta passar `userName = "Roger"` diretamente no `setContent {}`, sem precisar de nenhuma biblioteca de mocking.

```kotlin
composeTestRule.setContent {
    MainClockScreen(clock = fixedClock, userName = "Roger")
}
composeTestRule.onNodeWithText("Roger").assertIsDisplayed()  // ✅ GREEN
```

### Resultado Terminal
```
Starting 2 tests on Wear_OS_Small_Round_API_36(AVD) - 16
Finished 2 tests on Wear_OS_Small_Round_API_36(AVD) - 16

> Task :app:connectedAndroidTest

BUILD SUCCESSFUL in 1m 7s
64 actionable tasks: 38 executed, 26 from cache

Exit code: 0
```

---

## Commit Final
`feat(ui): wire dynamic profile and logout to Div1 and Div3`
Branch: `feature/dynamic-profile-and-logout`
