package com.ivy.core.domain.action.transaction

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ivy.core.data.common.IvyColor
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.attachment.Attachment
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import com.ivy.data.tag.Tag
import com.ivy.data.tag.TagState
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class WriteTrnsActTest{

    private lateinit var writeTransactionAct: WriteTrnsAct
    private lateinit var transactionDaoFake: TransactionDaoFake
    private lateinit var trnsSignal: TrnsSignal
    private lateinit var timeProviderFake: TimeProviderFake
    private lateinit var invalidateAccCacheAct: InvalidateAccCacheAct
    private lateinit var accountCacheDaoFake: AccountCacheDaoFake

    @BeforeEach
    fun setUp(){
        accountCacheDaoFake = AccountCacheDaoFake()
        timeProviderFake = TimeProviderFake()
        trnsSignal = TrnsSignal()
        invalidateAccCacheAct = InvalidateAccCacheAct(
            accountCacheDaoFake,
            timeProviderFake
        )
        transactionDaoFake = TransactionDaoFake()
        writeTransactionAct = WriteTrnsAct(
            transactionDao = transactionDaoFake,
            trnsSignal = trnsSignal,
            timeProvider = timeProviderFake,
            invalidateAccCacheAct = invalidateAccCacheAct,
            accountCacheDao = accountCacheDaoFake
        )
    }

    @Test
    fun `test creating transaction`() = runBlocking<Unit>{
        val account = account().copy(
            name = "Different name"
        )

        val transactionId = UUID.randomUUID()
        val tag = tag()
        val attachments = attachment(transactionId.toString())

        val transaction = transaction(transactionId, account).copy(
            tags = listOf(tag),
            attachments = listOf(attachments)
        )

        writeTransactionAct(WriteTrnsAct.Input.CreateNew(transaction))

        val cachedTransaction = transactionDaoFake.transactionEntity.find {
            it.id == transactionId.toString()
        }

        val cachedTags = transactionDaoFake.transactionTags.find { it.tagId == tag.id }
        val cachedAttachments = transactionDaoFake.transactionAttachments.find { it.id == attachments.id }

        assertThat(cachedTransaction).isNotNull()
        assertThat(cachedTransaction?.type).isEqualTo(TransactionType.Expense)
        assertThat(cachedTransaction?.amount).isEqualTo(50.0)

        assertThat(cachedTags).isNotNull()
        assertThat(cachedAttachments).isNotNull()
    }
}