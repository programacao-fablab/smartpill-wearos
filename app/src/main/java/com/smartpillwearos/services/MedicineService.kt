package com.smartpillwearos.services

import com.smartpillwearos.domain.Medicine
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class MedicationDto(
    val id: String,
    val name: String,
    val description: String? = null
)

@Serializable
data class IntakeHistoryDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("medication_id") val medicationId: String,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("taken_at") val takenAt: String? = null,
    val status: String,
    val medications: MedicationDto? = null
)

class MedicineService(private val supabase: SupabaseClient) {

    suspend fun fetchUserMedicines(userId: String): List<Medicine> {
        try {
            // Fetch intake history for the given user, joining medications
            val response = supabase.postgrest["intake_history"]
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<IntakeHistoryDto>()

            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

            return response.map { dto ->
                // Parse scheduled_at which is a timestamp with timezone
                val instant = try {
                    Instant.parse(dto.scheduledAt)
                } catch (e: Exception) {
                    Instant.now() // fallback
                }

                Medicine(
                    id = dto.id,
                    name = dto.medications?.name ?: "Desconhecido",
                    time = timeFormatter.format(instant),
                    isDone = dto.status.equals("TAKEN", ignoreCase = true)
                )
            }.sortedBy { it.time }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to fetch medicines: ${e.message}")
        }
    }
}
