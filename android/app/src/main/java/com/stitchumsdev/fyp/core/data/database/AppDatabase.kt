package com.stitchumsdev.fyp.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stitchumsdev.fyp.core.data.database.dao.ExhibitItemDao
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity

@Database(entities = [ExhibitItemEntity :: class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun exhibitItemDao(): ExhibitItemDao
}
