package com.smartpillwearos.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import java.time.Clock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

val Lavender = Color(0xFFDAE3FA)
val AliceBlue = Color(0xFFEDF3FC)
val BrightSnow = Color(0xFFF6F6F6)
val IvoryMist = Color(0xFFFEFAE9)
val VanillaCustard = Color(0xFFFFE9AF)
val DarkText = Color(0xFF333333)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainClockScreen(
    clock: Clock = Clock.systemDefaultZone(),
    userName: String = "Usuário",
    onLogout: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    var currentTime by remember { mutableStateOf(LocalTime.now(clock)) }

    LaunchedEffect(clock) {
        while (true) {
            currentTime = LocalTime.now(clock)
            delay(1000)
        }
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeString = currentTime.format(timeFormatter)

    Box(modifier = Modifier.fillMaxSize().background(BrightSnow)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PageInicio(timeString, userName)
                1 -> PageMeio()
                2 -> PageFim(onLogout)
            }
        }

        // Indicador de Navegação (3 pontos na parte inferior)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.Gray else Color.LightGray)
                )
            }
        }
    }
}

@Composable
fun PageInicio(timeString: String, userName: String = "Usuário") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Lavender),
                contentAlignment = Alignment.Center
            ) {
                Text("R", color = DarkText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = userName, color = DarkText, fontSize = 14.sp, modifier = Modifier.testTag("userName"))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = timeString,
            color = DarkText,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Próxima Dose: Dipirona",
            color = Color(0xFFC7A23A), // Darker variant of Vanilla Custard for readability
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PageMeio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Agenda",
            color = DarkText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))

        TimelineItem(time = "08:00", dose = "Vitamina C", isDone = true)
        Spacer(modifier = Modifier.height(6.dp))
        TimelineItem(time = "12:30", dose = "Dipirona", isDone = false)
        Spacer(modifier = Modifier.height(6.dp))
        TimelineItem(time = "20:00", dose = "Melatonina", isDone = false)
    }
}

@Composable
fun PageFim(onLogout: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sobre",
            color = DarkText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AliceBlue)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Lavender),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💊", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "PillGo",
                    color = DarkText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Versão v1.0.0",
                    color = DarkText,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Lavender,
                        contentColor = DarkText
                    ),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text(text = "SAIR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TimelineItem(time: String, dose: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDone) Lavender else AliceBlue)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = time, color = DarkText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = dose, color = DarkText, fontSize = 12.sp)
    }
}
