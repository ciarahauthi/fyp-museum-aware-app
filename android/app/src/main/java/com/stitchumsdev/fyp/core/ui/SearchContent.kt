import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ExhibitModel
import com.stitchumsdev.fyp.core.ui.GenericErrorScreen
import com.stitchumsdev.fyp.core.ui.LoadingScreen
import com.stitchumsdev.fyp.core.ui.components.ExhibitRow
import com.stitchumsdev.fyp.core.ui.theme.Typography
import com.stitchumsdev.fyp.core.ui.theme.fypColours
import com.stitchumsdev.fyp.feature.search.SearchAction
import com.stitchumsdev.fyp.feature.search.SearchUiState

@Composable
fun SearchContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    onAction: (SearchAction) -> Unit,
    selectionMode: Boolean = false,
    selectedIds: Set<Int> = emptySet(),
    onToggleSelect: (ExhibitModel) -> Unit = {},
    onObjectClick: (ExhibitModel) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fypColours.mainBackground)
            .padding(dimensionResource(R.dimen.padding_8))
    ) {
        when (uiState) {
            SearchUiState.Loading -> LoadingScreen()

            SearchUiState.Error -> GenericErrorScreen { onAction(SearchAction.Retry) }

            is SearchUiState.Default -> {
                val query = uiState.searchText.trim()

                val filtered = remember(uiState.objects, query) {
                    if (query.isEmpty()) uiState.objects
                    else {
                        val q = query.lowercase()
                        uiState.objects.filter { obj ->
                            obj.title.lowercase().contains(q) ||
                                    obj.description.lowercase().contains(q)
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = { onAction(SearchAction.OnTextChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(
                        text = "Search...",
                        style = Typography.bodyMedium,
                        color = fypColours.secondaryText) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            tint = fypColours.mainText,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                        ) },
                    trailingIcon = {
                        if (uiState.searchText.isNotEmpty()) {
                            IconButton(
                                onClick = { onAction(SearchAction.ClearSearch) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = null,
                                    tint = fypColours.mainText,
                                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = fypColours.mainText,
                        unfocusedTextColor = fypColours.mainText,
                    )
                )

                Text(
                    text = "${filtered.size} result(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = fypColours.secondaryText,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_8))
                )

                // List of exhibits
                if (filtered.isEmpty()) {
                    Text(
                        text = "No exhibits match '$query'.",
                        style = Typography.titleMedium,
                        color = fypColours.mainText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                        ) {
                        items(filtered, key = { it.id }) { obj ->
                            val isSelected = selectedIds.contains(obj.id)

                            ExhibitRow(
                                obj = obj,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = dimensionResource(R.dimen.padding_8))
                                    .clickable {
                                        if (selectionMode) onToggleSelect(obj) else onObjectClick(obj)
                                    },
                                selectionMode = selectionMode,
                                isSelected = isSelected,
                                onToggleSelect = { onToggleSelect(obj) },
                                )
                        }
                    }
                }
            }
        }
    }
}