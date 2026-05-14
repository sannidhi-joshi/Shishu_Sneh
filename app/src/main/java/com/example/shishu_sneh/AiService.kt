package com.example.shishu_sneh

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiService {
    private val generativeModel: Any? by lazy {
        try {
            // attempt to construct model; library may be absent in some builds
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "REDACTED_OR_ENV_VAR",
                generationConfig = generationConfig {
                    temperature = 0.7f
                    topK = 40
                    topP = 0.95f
                }
            )
        } catch (e: Throwable) {
            null
        }
    }

    suspend fun fetchAdvice(ageInMonths: Int): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "You are a 'Digital Elder' for an Indian mother. " +
                        "Give a short, traditional, and healthy feeding tip for a " +
                        "$ageInMonths month old baby. Keep it simple and warm."

                val model = generativeModel
                if (model == null) {
                    return@withContext "AI service unavailable"
                }

                // Use reflection to call generateContent if the typed API is not available at compile time
                val method = model::class.java.methods.firstOrNull { it.name == "generateContent" }
                val response = method?.invoke(model, prompt)

                val textField = response?.javaClass?.getDeclaredField("text")
                val textValue = textField?.let {
                    it.isAccessible = true
                    it.get(response) as? String
                }

                textValue ?: "The Digital Elder is resting. Please try again in a moment."

            } catch (e: Exception) {
                "Error Details: ${e.message}"
            }
        }
    }
}