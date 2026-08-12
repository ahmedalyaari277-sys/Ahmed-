package com.example.ledgerapp

import androidx.room.*
import java.math.BigDecimal

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val type: String
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val amountStr: String,
    val type: String,
    val details: String?,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface LedgerDao {
    @Insert
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    suspend fun getTransactionsForCustomer(customerId: Long): List<TransactionEntity>
}

@Database(entities = [CustomerEntity::class, TransactionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ledgerDao(): LedgerDao
}

object FinancialCalculator {
    fun calculateCustomerBalance(transactions: List<TransactionEntity>): BigDecimal {
        var totalGave = BigDecimal.ZERO
        var totalGot = BigDecimal.ZERO

        for (tx in transactions) {
            val amount = BigDecimal(tx.amountStr)
            if (tx.type == "GAVE") {
                totalGave = totalGave.add(amount)
            } else if (tx.type == "GOT") {
                totalGot = totalGot.add(amount)
            }
        }
        return totalGave.subtract(totalGot)
    }
}
