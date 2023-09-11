package com.ivy.core.domain.action.transaction

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.dao.trn.AccountIdAndTrnTime
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState

class TransactionDaoFake: TransactionDao() {

    val transactionEntity = mutableListOf<TransactionEntity>()
    val transactionTags = mutableListOf<TrnTagEntity>()
    val transactionAttachments = mutableListOf<AttachmentEntity>()
    val transactionMetadata = mutableListOf<TrnMetadataEntity>()


    override suspend fun saveTrnEntity(entity: TransactionEntity) {
        transactionEntity.add(entity)
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        val tag = transactionTags.find { it.trnId == trnId } ?: return

        val index = transactionTags.indexOf(tag)
        transactionTags[index] = tag.copy(sync = sync)
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {
        transactionTags.addAll(entity)
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        val attachment = transactionAttachments.find { it.associatedId == associatedId } ?: return

        val index = transactionAttachments.indexOf(attachment)
        transactionAttachments[index] = attachment.copy(sync = sync)
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        transactionAttachments.addAll(entity)
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        val metadata = transactionMetadata.find { it.trnId == trnId } ?: return

        val index = transactionMetadata.indexOf(metadata)
        transactionMetadata[index] = metadata.copy(sync = sync)
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        transactionMetadata.addAll(entity)
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        return transactionEntity.filter { it.sync == SyncState.Deleting }
    }

    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        return transactionEntity
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        val transaction = transactionEntity.find { it.id == trnId && it.sync == SyncState.Deleting } ?: return null

        return AccountIdAndTrnTime(
            accountId = transaction.accountId,
            time = transaction.time,
            timeType = transaction.timeType
        )
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        val transaction = transactionEntity.find { it.id == trnId } ?: return

        val index = transactionEntity.indexOf(transaction)
        transactionEntity[index] = transaction.copy(sync = sync)
    }
}