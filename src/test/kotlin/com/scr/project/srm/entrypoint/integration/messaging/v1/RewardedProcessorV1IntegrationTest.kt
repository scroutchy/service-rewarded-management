package com.scr.project.srm.entrypoint.integration.messaging.v1

import com.scr.project.commons.cinema.test.awaitUntil
import com.scr.project.srm.AbstractIntegrationTest
import com.scr.project.srm.KafkaTestProducer
import com.scr.project.srm.RewardedEntityTypeKafkaDto
import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.TestKafkaConfig
import com.scr.project.srm.config.TopicProperties
import com.scr.project.srm.domains.rewarded.dao.RewardedDao
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import reactor.kotlin.test.test

@SpringBootTest
@Import(TestKafkaConfig::class)
class RewardedProcessorV1IntegrationTest(
    @Autowired private val testRewardedProducer: KafkaTestProducer<RewardedKafkaDto>,
    @Autowired private val rewardedDao: RewardedDao,
    @Autowired private val topicProperties: TopicProperties,
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setUp() {
        rewardedDao.initTestData()
    }

    @Test
    fun `processor should succeed and persist Rewarded in database`() {
        val rewardedKafkaDto = RewardedKafkaDto(ObjectId.get().toHexString(), RewardedEntityTypeKafkaDto.ACTOR)
        testRewardedProducer.sendMessage(topicProperties.rewardedCreationNotification, rewardedKafkaDto.id, rewardedKafkaDto)
            .test()
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete()
        awaitUntil {
            val rewarded = rewardedDao.findAllBy { it.rewardedId == rewardedKafkaDto.id }
            assertThat(rewarded).hasSize(1)
        }
    }
}