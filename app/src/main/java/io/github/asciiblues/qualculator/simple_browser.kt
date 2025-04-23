package io.github.asciiblues.qualculator

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.asciiblues.qualculator.ui.theme.QualculatorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class simple_browser : ComponentActivity() {

    var isClose = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        val url = intent.getStringExtra(EXTRA_URL) ?: "https://google.com"

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setStatusBarColor()
            QualculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    main(modifier = Modifier.padding(innerPadding), url = url)
                }
            }
        }
    }


    companion object {
        const val EXTRA_URL = "https://google.com/"

        fun createIntent(context: Context, url: String): Intent {
            return Intent(context, simple_browser::class.java).apply {
                putExtra(EXTRA_URL, url)
            }
        }
    }

    @Composable
    fun main(modifier: Modifier = Modifier, url: String) {
        val context = LocalContext.current
        val activity = context as? Activity
        var webView: WebView? by remember { mutableStateOf(null) }
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AndroidView(
                    factory = {
                        WebView(it).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.builtInZoomControls = true
                            loadUrl(url)
                            webView = this
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp, bottom = 1.dp, start = 1.dp, end = 1.dp).fillMaxSize()
                )
            }
            if (isClose.value) {
                activity?.finish()
            }
            BackHandler {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()

                }else {
                    isClose.value = true
                }
            }
        }
    }
}