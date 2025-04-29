package com.scr.project.srm.domains.rewarded.component

import com.scr.project.srm.AbstractIntegrationTest
import com.scr.project.srm.domains.rewarded.dao.RewardedDao
import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import com.scr.project.srm.domains.rewarded.model.entity.RewardedType.ACTOR
import com.scr.project.srm.domains.rewarded.repository.RewardedRepository
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test

@SpringBootTest
class RewardedRepositoryTest(
    @Autowired private val rewardedRepository: RewardedRepository,
    @Autowired private val rewardedDao: RewardedDao
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setUp() {
        rewardedDao.initTestData()
    }

    @Test
    fun `insert should succeed and create a rewarded in database`() {
        val rewarded = Rewarded(ObjectId.get().toHexString(), ACTOR)
        rewardedRepository.insert(rewarded)
            .test()
            .expectSubscription()
            .consumeNextWith {
                assertThat(it.id).isNotNull
                assertThat(it.type).isEqualTo(ACTOR)
                assertThat(it.rewardedId).isEqualTo(rewarded.rewardedId)
                assertThat(it.rewards).isEqualTo(rewarded.rewards)
            }.verifyComplete()
        val rewardedInDB = rewardedDao.findAllBy { it.rewardedId == rewarded.rewardedId }.single()
        assertThat(rewardedInDB).isNotNull
        assertThat(rewardedInDB.id).isNotNull
        assertThat(rewardedInDB.type).isEqualTo(ACTOR)
        assertThat(rewardedInDB.rewardedId).isEqualTo(rewarded.rewardedId)
        assertThat(rewardedInDB.rewards).isEqualTo(rewarded.rewards)
    }
}