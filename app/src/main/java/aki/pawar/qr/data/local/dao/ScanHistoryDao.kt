package aki.pawar.qr.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import aki.pawar.qr.data.local.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for scan history operations
 */
@Dao
interface ScanHistoryDao {
    
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT * FROM scan_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteScans(): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT * FROM scan_history WHERE id = :id")
    suspend fun getScanById(id: Long): ScanHistoryEntity?
    
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentScans(limit: Int): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT * FROM scan_history WHERE rawValue LIKE '%' || :query || '%' OR displayValue LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity): Long
    
    @Update
    suspend fun updateScan(scan: ScanHistoryEntity)
    
    @Delete
    suspend fun deleteScan(scan: ScanHistoryEntity)
    
    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteScanById(id: Long)
    
    @Query("DELETE FROM scan_history")
    suspend fun deleteAllScans()
    
    @Query("UPDATE scan_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    
    @Query("SELECT COUNT(*) FROM scan_history")
    fun getScanCount(): Flow<Int>
}


