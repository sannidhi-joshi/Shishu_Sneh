package com.example.shishu_sneh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.shishu_sneh.ui.theme.Shishu_SnehTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Shishu_SnehTheme {
                com.example.shishu_sneh.ui.navigation.AppNavigation()
            }
        }
    }
}