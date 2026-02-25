package com.stitchumsdev.fyp.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.model.ObjectModel

@Composable
fun ExhibitInformationScreen(
    navHostController: NavHostController,
    exhibit: ObjectModel
) {
    InformationScreen(
        navHostController = navHostController,
        title = exhibit.title,
        image = R.drawable.img_rusty //ToDo Change
    ) {
        Text(text = "Child Friendly: ${exhibit.childFriendly}")
        Text(exhibit.description)
    }
}