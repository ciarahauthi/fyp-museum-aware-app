import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.core.ui.components.CommonTextButton
import com.stitchumsdev.fyp.core.ui.components.TopBar
import com.stitchumsdev.fyp.feature.search.SearchAction
import com.stitchumsdev.fyp.feature.search.SearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
    navHostController: NavHostController,
    uiState: SearchUiState,
    onSearchAction: (SearchAction) -> Unit,
    selectedStopsCount: Int,
    canStart: Boolean,
    selectedIds: Set<Int>,
    onClearStops: () -> Unit,
    onStart: () -> Unit,
    onToggleSelect: (ObjectModel) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                navHostController = navHostController ,
                title = { Text("Create a route") },
                actions = {
                    CommonTextButton(
                        text = stringResource(R.string.clear),
                        enabled = canStart,
                        onClick = onClearStops
                    )
                    CommonTextButton(
                        text = stringResource(R.string.start) + " ($selectedStopsCount)",
                        enabled = canStart,
                        onClick = onStart
                    )
                }
            )
        }
    ) { innerPadding ->
        SearchContent(
            uiState = uiState,
            onAction = onSearchAction,
            selectionMode = true,
            selectedIds = selectedIds,
            onToggleSelect = onToggleSelect,
            onObjectClick = {},
            modifier = Modifier.padding(innerPadding)
        )
    }
}
