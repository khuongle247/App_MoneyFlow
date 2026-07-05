package com.example.moneyflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moneyflow.data.local.dao.CycleDao
import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.dao.NotificationDao
import com.example.moneyflow.data.local.entity.*

@Database(
    entities = [
        CategoryEntity::class,
        TransactionEntity::class,
        SavingGoalEntity::class,
        BudgetEntity::class,
        MonthlyBalanceEntity::class,
        FinancialCycleEntity::class,
        NotificationEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FinlyDatabase : RoomDatabase() {
    abstract fun finlyDao(): FinlyDao
    abstract fun cycleDao(): CycleDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create financial_cycles table with explicit NOT NULL and DEFAULT values
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `financial_cycles` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `startDate` INTEGER NOT NULL, 
                        `endDate` INTEGER NOT NULL, 
                        `initialBalance` INTEGER NOT NULL, 
                        `remainingBalance` INTEGER NOT NULL, 
                        `totalExpense` INTEGER NOT NULL, 
                        `isClosed` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                // Create app_notifications table with explicit NOT NULL and DEFAULT values
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `app_notifications` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `content` TEXT NOT NULL, 
                        `createdAt` INTEGER NOT NULL, 
                        `isRead` INTEGER NOT NULL DEFAULT 0,
                        `type` TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `budgets` ADD COLUMN `monthId` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `monthly_balances` (`monthId` TEXT NOT NULL, `balance` REAL NOT NULL, PRIMARY KEY(`monthId`))")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `transactions` ADD COLUMN `icon` TEXT NOT NULL DEFAULT 'wallet'")
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_categoryId ON transactions(categoryId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_date ON transactions(date)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `budgets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `icon` TEXT NOT NULL, `color` INTEGER NOT NULL)")
                db.execSQL("ALTER TABLE `transactions` ADD COLUMN `budgetId` INTEGER")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_budgetId` ON `transactions` (`budgetId`)")
            }
        }
    }
}
