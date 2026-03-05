import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.core.model.ExhibitModel
import com.stitchumsdev.fyp.core.ui.GenericErrorScreen
import com.stitchumsdev.fyp.core.ui.LoadingScreen
import com.stitchumsdev.fyp.core.ui.components.BottomNavigationBar
import com.stitchumsdev.fyp.feature.search.SearchAction
import com.stitchumsdev.fyp.feature.search.SearchUiState

@Composable
fun SearchScreen(
    navHostController: NavHostController,
    uiState: SearchUiState,
    onAction: (SearchAction) -> Unit,
    onObjectClick: (ExhibitModel) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navHostController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            SearchUiState.Error -> GenericErrorScreen(onRetry = onRetry)
            SearchUiState.Loading -> LoadingScreen()
            is SearchUiState.Default -> {
                SearchContent(
                    uiState = uiState,
                    onAction = onAction,
                    selectionMode = false,
                    onObjectClick = onObjectClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}