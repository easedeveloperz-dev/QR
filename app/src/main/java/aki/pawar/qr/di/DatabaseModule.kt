package aki.pawar.qr.di

import android.content.Context
import androidx.room.Room
import aki.pawar.qr.data.local.QrDatabase
import aki.pawar.qr.data.local.dao.GeneratedQrDao
import aki.pawar.qr.data.local.dao.ScanHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideQrDatabase(
        @ApplicationContext context: Context
    ): QrDatabase {
        return Room.databaseBuilder(
            context,
            QrDatabase::class.java,
            QrDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideScanHistoryDao(database: QrDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }
    
    @Provides
    @Singleton
    fun provideGeneratedQrDao(database: QrDatabase): GeneratedQrDao {
        return database.generatedQrDao()
    }
}









