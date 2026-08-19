package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VmInstance
import com.example.data.model.VmSnapshot
import com.example.data.model.VmStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VmDao {
    @Query("SELECT * FROM vm_instances ORDER BY lastBootedAt DESC, id DESC")
    fun getAllVms(): Flow<List<VmInstance>>

    @Query("SELECT * FROM vm_instances WHERE id = :id")
    fun getVmById(id: Long): Flow<VmInstance?>

    @Query("SELECT * FROM vm_instances WHERE id = :id")
    suspend fun getVmByIdDirect(id: Long): VmInstance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVm(vm: VmInstance): Long

    @Update
    suspend fun updateVm(vm: VmInstance)

    @Delete
    suspend fun deleteVm(vm: VmInstance)

    @Query("UPDATE vm_instances SET status = :status WHERE id = :id")
    suspend fun updateVmStatus(id: Long, status: VmStatus)

    @Query("UPDATE vm_instances SET status = 'STOPPED' WHERE status != 'STOPPED'")
    suspend fun resetAllStatusesToStopped()

    @Query("SELECT COUNT(*) FROM vm_instances")
    suspend fun getVmCount(): Int
}

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM vm_snapshots WHERE vmId = :vmId ORDER BY createdAt DESC")
    fun getSnapshotsForVm(vmId: Long): Flow<List<VmSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: VmSnapshot): Long

    @Delete
    suspend fun deleteSnapshot(snapshot: VmSnapshot)
}
