package com.example.project_uts_rentaloan.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project_uts_rentaloan.data.local.dao.LoanDao
import com.example.project_uts_rentaloan.data.local.dao.UserDao
import com.example.project_uts_rentaloan.data.local.entity.BorrowerEntity
import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanTransactionEntity
import com.example.project_uts_rentaloan.data.local.entity.UserEntity

@Database(
    entities = [
        ItemEntity::class, 
        BorrowerEntity::class, 
        LoanTransactionEntity::class,
        UserEntity::class
    ],
    version = 9, // Incremented to apply ForeignKey.CASCADE and fix deletion crash
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loanDao(): LoanDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rental_loan_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
