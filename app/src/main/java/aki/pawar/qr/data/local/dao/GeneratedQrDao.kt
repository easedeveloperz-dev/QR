package aki.pawar.qr.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import aki.pawar.qr.data.local.entity.GeneratedQrEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for generated QR code history operations
 */
@Dao
interface GeneratedQrDao {
    
    @Query("SELECT * FROM generated_qr ORDER BY timestamp DESC")
    fun getAllGenerated(): Flow<List<GeneratedQrEntity>>
    
    @Query("SELECT * FROM generated_qr WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteGenerated(): Flow<List<GeneratedQrEntity>>
    
    @Query("SELECT * FROM generated_qr WHERE id = :id")
    suspend fun getGeneratedById(id: Long): GeneratedQrEntity?
    
    @Query("SELECT * FROM generated_qr ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentGenerated(limit: Int): Flow<List<GeneratedQrEntity>>
    
    @Query("SELECT * FROM generated_qr WHERE qrType = :type ORDER BY timestamp DESC")
    fun getGeneratedByType(type: String): Flow<List<GeneratedQrEntity>>
    
    @Query("SELECT * FROM generated_qr WHERE displayLabel LIKE '%' || :query || '%' OR qrContent LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchGenerated(query: String): Flow<List<GeneratedQrEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenerated(qr: GeneratedQrEntity): Long
    
    @Update
    suspend fun updateGenerated(qr: GeneratedQrEntity)
    
    @Delete
    suspend fun deleteGenerated(qr: GeneratedQrEntity)
    
    @Query("DELETE FROM generated_qr WHERE id = :id")
    suspend fun deleteGeneratedById(id: Long)
    
    @Query("DELETE FROM generated_qr")
    suspend fun deleteAllGenerated()
    
    @Query("UPDATE generated_qr SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    
    @Query("SELECT COUNT(*) FROM generated_qr")
    fun getGeneratedCount(): Flow<Int>
}



