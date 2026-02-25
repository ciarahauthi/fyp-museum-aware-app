import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ObjectModel
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
    onToggleSelect: (ObjectModel) -> Unit = {},
    onObjectClick: (ObjectModel) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(fypColours.mainBackground)
            .padding(dimensionResource(R.dimen.padding_16))
    ) {
        when (uiState) {
            SearchUiState.Loading -> { //ToDo Update
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Text("Loading exhibits…")
            }

            SearchUiState.Error -> Text("Couldn’t load exhibits.") // ToDo error screen

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
                        text = "Search exhibits…",
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
                    singleLine = true
                )

                Text(
                    text = "${filtered.size} result(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = fypColours.secondaryText,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_8))
                )

                // List of exhibits
                if (filtered.isEmpty()) {
                    Text("No exhibits match “$query”.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered, key = { it.id }) { obj ->
                            val isSelected = selectedIds.contains(obj.id)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (selectionMode) onToggleSelect(obj) else onObjectClick(obj)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) { ExhibitRow(obj = obj) }

                                if (selectionMode) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { onToggleSelect(obj) }
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_chevron),
                                        contentDescription = null,
                                        modifier = Modifier.size(dimensionResource(R.dimen.icon_medium))
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExhibitRow(
    obj: ObjectModel
) {
    val title = obj.title.ifBlank { "Untitled" }
    val description = obj.description

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_4))
    ) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = fypColours.mainText
        )
        // Description
        Text(
            text = description,
            maxLines = 2,
            style = MaterialTheme.typography.bodyMedium,
            color = fypColours.secondaryText
        )
    }
}