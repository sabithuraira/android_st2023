package bps.sumsel.st2023.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import bps.sumsel.st2023.room.entity.SlsEntity

@Dao
interface SlsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: List<SlsEntity>)

    @Update
    fun update(data: SlsEntity)

    @Delete
    fun delete(data: SlsEntity)

    @Query("SELECT * from sls ORDER BY id ASC")
    fun findAll(): LiveData<List<SlsEntity>>

    @Query("SELECT * from sls WHERE kode_pcl= :kode OR kode_pml= :kode OR kode_koseka= :kode ORDER BY id ASC")
    fun findByPetugas(kode: String): LiveData<List<SlsEntity>>

    @Query("DELETE FROM sls")
    fun deleteAll()
}