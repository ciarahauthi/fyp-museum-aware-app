import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.feature.search.SearchAction
import com.stitchumsdev.fyp.feature.search.SearchUiState

@Composable
fun SearchScreen(
    navHostController: NavHostController,
    uiState: SearchUiState,
    onAction: (SearchAction) -> Unit,
    onObjectClick: (ObjectModel) -> Unit = {}
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SearchContent(
            uiState = uiState,
            onAction = onAction,
            selectionMode = false,
            onObjectClick = onObjectClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}