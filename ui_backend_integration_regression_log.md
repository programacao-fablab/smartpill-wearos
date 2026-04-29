---
tipo: ai-log
projeto: PillGo
plataforma: wear_os_kotlin
fase: UI_BACKEND_WIRING
branch_criada: feature/auth-ui-wiring
data: 2026-04-29
status: integracao_com_regressao_passou
---

# Log: UI Backend Wiring (Auth Machine → PairingScreen)

## Objetivo
Acoplar a `SupabaseAuthManager` (camada headless, validada na branch anterior) à interface gráfica do Wear OS via reatividade pura. Nenhuma lógica de negócio pode residir em `PairingScreen.kt`.

---

## Arquitetura do Wiring

### `PairingViewModel.kt` (NOVO)
Classe responsável por ser a ponte entre a UI e a máquina de estados. Instancia `SupabaseAuthManager` com o `supabaseClient` real e expõe:
- `authState: StateFlow<AuthState>` — única fonte de verdade para a UI
- `startPairing(context)` — engatilha `manager.startPairingFlow(deviceId)` dentro de `viewModelScope.launch`
- `retry(context)` — re-chama `startPairing(context)` em caso de erro

### `PairingScreen.kt` (REFATORADO)
**Regra violada antes:** A tela continha `LaunchedEffect` com chamadas diretas ao Supabase SDK (`functions.invoke`, `realtime.channel`, `auth.refreshSession`), acoplando a lógica de negócio à camada de UI.

**Solução aplicada:** A tela agora é um puro observador de estado:
```kotlin
val authState by vm.authState.collectAsState()

LaunchedEffect(Unit) {
    vm.startPairing(context)  // Disparo único
}

LaunchedEffect(authState) {
    if (authState is AuthState.Success) {
        onAuthenticationSuccess()  // Navegação via callback
    }
}

when (val state = authState) {
    is AuthState.Idle, is AuthState.GeneratingQR, is AuthState.Authenticating -> /* Loader */
    is AuthState.WaitingForMobileScan -> /* QR Code baseado em state.qrToken */
    is AuthState.Success -> { /* LaunchedEffect cuida da navegação */ }
    is AuthState.Error -> /* Mensagem + Botão Retry */
}
```

**Por que não há loop de recomposição:** O `LaunchedEffect(Unit)` é disparado exatamente uma vez (pela natureza do `Unit` como key). O `LaunchedEffect(authState)` só reage quando o estado muda para `Success`, e nesse momento navega e destrói a tela — portanto não há re-renderização que possa re-disparar a geração do QR.

### Navegação (`MainActivity.kt`)
O callback de sucesso usa `popUpTo("pairing") { inclusive = true }` para destruir a PairingScreen da pilha após navegar para a Home:
```kotlin
onAuthenticationSuccess = {
    navController.navigate("home") {
        popUpTo("pairing") { inclusive = true }
    }
}
```

---

## Regressão: Testes Instrumentados

### Objetivo
Verificar que o `VerticalPager` (UI V2 — Divisão 1 com texto "Roger") não sofreu dano colateral após a refatoração da camada de serviços.

### Resultado do Terminal (connectedAndroidTest)
```
> Task :app:connectedDebugAndroidTest
> Task :app:connectedAndroidTest

BUILD SUCCESSFUL in 17s
64 actionable tasks: 29 executed, 16 from cache, 19 up-to-date
Configuration cache entry stored.

Exit code: 0
```

Todos os testes de instrumentação passaram sem regressão. O `VerticalPager` e seus componentes V2 estão intactos.

### Resultado dos Testes Unitários (Behavioral StateFlow)
```
SupabaseAuthManagerTest > startPairingFlow with mockedTokens should transition to Success STANDARD_OUT
    ⏳ [TRANSITION]: Idle
    ⏳ [TRANSITION]: GeneratingQR
    ⏳ [TRANSITION]: WaitingForMobileScan(qrToken=mock_qr_token_123)
    ⏳ [TRANSITION]: Authenticating
    ⏳ [TRANSITION]: Success(userId=mocked_user_id_999)

SupabaseAuthManagerTest > startPairingFlow with mockedTokens should transition to Success PASSED

BUILD SUCCESSFUL in 51s
```

---

## Commit Final
`feat(ui): wire PairingScreen to headless SupabaseAuthManager via StateFlow`

Branch: `feature/auth-ui-wiring`
