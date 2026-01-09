package aki.pawar.qr.data.repository

import aki.pawar.qr.data.local.dao.GeneratedQrDao
import aki.pawar.qr.data.local.entity.GeneratedQrEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing generated QR code history
 */
@Singleton
class GeneratedQrRepository @Inject constructor(
    private val generatedQrDao: GeneratedQrDao
) {
    
    fun getAllGenerated(): Flow<List<GeneratedQrEntity>> = generatedQrDao.getAllGenerated()
    
    fun getFavoriteGenerated(): Flow<List<GeneratedQrEntity>> = generatedQrDao.getFavoriteGenerated()
    
    fun getRecentGenerated(limit: Int = 10): Flow<List<GeneratedQrEntity>> = 
        generatedQrDao.getRecentGenerated(limit)
    
    fun getGeneratedByType(type: String): Flow<List<GeneratedQrEntity>> = 
        generatedQrDao.getGeneratedByType(type)
    
    fun searchGenerated(query: String): Flow<List<GeneratedQrEntity>> = 
        generatedQrDao.searchGenerated(query)
    
    fun getGeneratedCount(): Flow<Int> = generatedQrDao.getGeneratedCount()
    
    suspend fun getGeneratedById(id: Long): GeneratedQrEntity? = 
        generatedQrDao.getGeneratedById(id)
    
    suspend fun saveGenerated(
        qrType: String,
        qrContent: String,
        displayLabel: String,
        metadata: String = ""
    ): Long {
        val entity = GeneratedQrEntity(
            qrType = qrType,
            qrContent = qrContent,
            displayLabel = displayLabel,
            metadata = metadata
        )
        return generatedQrDao.insertGenerated(entity)
    }
    
    suspend fun deleteGenerated(qr: GeneratedQrEntity) = generatedQrDao.deleteGenerated(qr)
    
    suspend fun deleteGeneratedById(id: Long) = generatedQrDao.deleteGeneratedById(id)
    
    suspend fun deleteAllGenerated() = generatedQrDao.deleteAllGenerated()
    
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = 
        generatedQrDao.updateFavoriteStatus(id, isFavorite)
}









