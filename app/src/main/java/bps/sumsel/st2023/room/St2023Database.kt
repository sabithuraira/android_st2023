package bps.sumsel.st2023.room

import androidx.room.Database
import androidx.room.RoomDatabase
import bps.sumsel.st2023.room.dao.SlsDao
import bps.sumsel.st2023.room.entity.SlsEntity

@Database(entities = [SlsEntity::class], version = 1)
abstract class St2023Database: RoomDatabase() {
    abstract fun slsDao(): SlsDao
}