import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.stitchumsdev.fyp.core.model.ObjectModel
import com.stitchumsdev.fyp.feature.search.SearchAction
import com.stitchumsdev.fyp.feature.search.SearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(
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
            TopAppBar(
                title = { Text("Create a route") },
                actions = {
                    TextButton(enabled = canStart, onClick = onClearStops) { Text("Clear") }
                    TextButton(enabled = canStart, onClick = onStart) {
                        Text("Start ($selectedStopsCount)")
                    }
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