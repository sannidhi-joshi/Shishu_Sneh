package com.example.shishu_sneh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shishu_sneh.data.local.feeding.FeedingGuideEngine
import com.example.shishu_sneh.data.local.entity.CustomMilestone
import com.example.shishu_sneh.ui.viewmodel.CustomMilestoneViewModel
import com.example.shishu_sneh.ui.viewmodel.GrowthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Milestone(val title: String, val description: String)

fun getMilestones(age: Int): List<Milestone> {
    return when (age) {
        in 0..3 -> listOf(
            Milestone("Smiles responsively", "Responds to familiar faces"),
            Milestone("Follows objects with eyes", "Tracks movement with eyes"),
            Milestone("Lifts head slightly", "During tummy time")
        )
        in 4..6 -> listOf(
            Milestone("Rolls over", "From tummy to back or vice versa"),
            Milestone("Holds toys", "Reaches and grasps objects"),
            Milestone("Laughs", "Makes joyful sounds")
        )
        in 7..9 -> listOf(
            Milestone("Sits without support", "Can sit steady"),
            Milestone("Responds to name", "Turns when called"),
            Milestone("Babbles", "Makes repetitive consonant sounds")
        )
        in 10..12 -> listOf(
            Milestone("Crawls or stands", "Begins moving independently"),
            Milestone("Says simple words", "Mama, Dada, etc."),
            Milestone("Picks objects with fingers", "Pincer grasp developing")
        )
        in 13..24 -> listOf(
            Milestone("Walks independently", "Starts moving around"),
            Milestone("Points to objects", "Uses gestures to communicate"),
            Milestone("Understands simple instructions", "Responds to basic requests")
        )
        else -> listOf(
            Milestone("Continue growth", "Keep supporting development"),
            Milestone("Encourage play", "Interactive play aids learning")
        )
    }
}

@Composable
fun DelayStatusCard(status: String, modifier: Modifier = Modifier) {
    val (emoji, bgColor) = when {
        status.startsWith("On track") -> Pair("✔", Color(0xFFE8F6EA))
        status.startsWith("Slight delay") -> Pair("⚠", Color(0xFFFFF8E1))
        else -> Pair("🚨", Color(0xFFFFEBEE))
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Development Status", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(10.dp))
                Text(status, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This is not a medical diagnosis. Consult a pediatrician if concerned.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun getDelayStatus(
    age: Int,
    milestones: List<Milestone>,
    achievedMap: Map<String, Boolean>
): String {
    val total = milestones.size
    if (total == 0) return "On track"
    val achieved = milestones.count { achievedMap[it.title] == true }
    val pct = (achieved * 100) / total

    return when {
        pct >= 70 -> "On track"
        pct in 40..69 -> "Slight delay - monitor progress"
        else -> "Possible delay - consult pediatrician"
    }
}

@Composable
fun MilestonesScreen(
    navController: NavController,
    growthViewModel: GrowthViewModel,
    customMilestoneViewModel: CustomMilestoneViewModel
) {
    val userProfile by growthViewModel.userProfile.collectAsState()
    val age = userProfile?.dob?.let { FeedingGuideEngine.getAgeInMonths(it) } ?: 0
    val customMilestones by customMilestoneViewModel.milestones.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var customTitle by remember { mutableStateOf("") }
    var customDescription by remember { mutableStateOf("") }

    val milestones = getMilestones(age)

    val checkedStates = remember { mutableStateMapOf<String, Boolean>() }

    val total = milestones.size
    val achieved = milestones.count { checkedStates[it.title] == true }
    val progress = if (total > 0) achieved.toFloat() / total else 0f

    val status = getDelayStatus(age, milestones, checkedStates)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🧠 Milestones", style = MaterialTheme.typography.headlineSmall)

        Text("Age: $age months", style = MaterialTheme.typography.titleMedium)

        DelayStatusCard(status = status, modifier = Modifier.fillMaxWidth())

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("$achieved / $total achieved", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            }
        }

        milestones.forEach { milestone ->
            val key = milestone.title
            val checked = checkedStates[key] ?: false

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (checked) Color(0xFFE8F6EA) else Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(milestone.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(milestone.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    Checkbox(
                        checked = checked,
                        onCheckedChange = { value ->
                            checkedStates[key] = value
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("✨ Special Moments", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = { showAddDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("➕ Add Special Moment")
        }

        customMilestones.forEachIndexed { index, item ->
            CustomMilestoneCard(
                milestone = item,
                highlight = index == 0
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Special Moment") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customTitle,
                            onValueChange = { customTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = customDescription,
                            onValueChange = { customDescription = it },
                            label = { Text("Description (optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        customMilestoneViewModel.addMilestone(customTitle, customDescription)
                        customTitle = ""
                        customDescription = ""
                        showAddDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun CustomMilestoneCard(milestone: CustomMilestone, highlight: Boolean) {
    val bgColor = if (highlight) Color(0xFFFFF6DD) else MaterialTheme.colorScheme.surface
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (highlight) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${emojiForTitle(milestone.title)} ${milestone.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (milestone.description.isNotBlank()) {
                Text(text = milestone.description, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(milestone.date)),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

private fun emojiForTitle(title: String): String {
    val lower = title.lowercase(Locale.getDefault())
    return when {
        "smile" in lower -> "😊"
        "walk" in lower || "step" in lower -> "🚶"
        "talk" in lower || "word" in lower || "speak" in lower -> "🗣"
        else -> "🌟"
    }
}
