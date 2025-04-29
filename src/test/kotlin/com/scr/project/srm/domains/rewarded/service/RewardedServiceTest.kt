package com.scr.project.srm.domains.rewarded.service

import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import com.scr.project.srm.domains.rewarded.model.entity.RewardedType
import com.scr.project.srm.domains.rewarded.repository.RewardedRepository
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class RewardedServiceTest {

    private val rewardedRepository = mockk<RewardedRepository>()
    private val rewardedService = RewardedService(rewardedRepository)

    @BeforeEach
    fun setUp() {
        clearMocks(rewardedRepository)
    }

    @Test
    fun `create should succeed`() {
        val rewarded = Rewarded(ObjectId.get().toHexString(), RewardedType.ACTOR)
        every { rewardedRepository.insert(rewarded) } answers { rewarded.copy(id = ObjectId.get()).toMono() }
        rewardedService.create(rewarded)
            .test()
            .expectSubscription()
            .consumeNextWith {
                assertThat(it.id).isNotNull
                assertThat(it.rewardedId).isEqualTo(rewarded.rewardedId)
                assertThat(it.type).isEqualTo(rewarded.type)
                assertThat(it.rewards).isEqualTo(rewarded.rewards)
            }.verifyComplete()
        verify(exactly = 1) { rewardedRepository.insert(rewarded) }
        confirmVerified(rewardedRepository)
    }

    @Test
    fun `create should fail when insert fails`() {
        val rewarded = Rewarded(ObjectId.get().toHexString(), RewardedType.ACTOR)
        val exception = RuntimeException("Insert failed")
        every { rewardedRepository.insert(any<Rewarded>()) } returns Mono.error(exception)

        rewardedService.create(rewarded)
            .test()
            .expectSubscription()
            .expectError(RuntimeException::class.java)
            .verify()

        verify(exactly = 1) { rewardedRepository.insert(rewarded) }
        confirmVerified(rewardedRepository)
    }
}