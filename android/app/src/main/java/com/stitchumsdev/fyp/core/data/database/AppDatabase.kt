package com.stitchumsdev.fyp.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stitchumsdev.fyp.core.data.database.converters.RouteConverters
import com.stitchumsdev.fyp.core.data.database.dao.ExhibitItemDao
import com.stitchumsdev.fyp.core.data.database.dao.ExhibitRatingDao
import com.stitchumsdev.fyp.core.data.database.dao.LocationItemDao
import com.stitchumsdev.fyp.core.data.database.dao.RouteItemDao
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitRatingEntity
import com.stitchumsdev.fyp.core.data.database.entities.LocationItemEntity
import com.stitchumsdev.fyp.core.data.database.entities.RouteItemEntity

@Database(entities = [
    ExhibitItemEntity :: class,
    LocationItemEntity :: class,
    RouteItemEntity :: class,
    ExhibitRatingEntity :: class], version = 1, exportSchema = false)
@TypeConverters(RouteConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun exhibitItemDao(): ExhibitItemDao
    abstract fun locationItemDao(): LocationItemDao
    abstract fun routeItemDao(): RouteItemDao
    abstract fun exhibitRatingDao(): ExhibitRatingDao
}
