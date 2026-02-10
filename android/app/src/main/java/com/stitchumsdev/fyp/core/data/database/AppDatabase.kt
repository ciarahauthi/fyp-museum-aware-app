package com.stitchumsdev.fyp.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stitchumsdev.fyp.core.data.database.dao.ExhibitItemDao
import com.stitchumsdev.fyp.core.data.database.dao.LocationItemDao
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity

@Database(entities = [ExhibitItemEntity :: class, LocationItemEntity :: class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun exhibitItemDao(): ExhibitItemDao
    abstract fun locationItemDao(): LocationItemDao
}
