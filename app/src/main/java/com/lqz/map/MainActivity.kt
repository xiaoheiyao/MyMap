package com.lqz.map

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.lqz.imap.core.MapView
import com.lqz.imap.core.internal.IMapDelegate
import com.lqz.imap.core.internal.IMapViewDelegate
import com.lqz.imap.core.listener.OnMapLoadedListener
import com.lqz.imap.model.MapType
import com.lqz.imap_arcgis10.Arcgis10MapView
import com.lqz.map.ui.page.common.AppScaffold
import com.lqz.map.ui.theme.MapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScaffold(savedInstanceState)

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MapTheme {
        Greeting("Android")
    }
}