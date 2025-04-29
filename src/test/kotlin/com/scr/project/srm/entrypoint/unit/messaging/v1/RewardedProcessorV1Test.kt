package com.scr.project.srm.entrypoint.unit.messaging.v1

import com.scr.project.srm.RewardedEntityTypeKafkaDto.ACTOR
import com.scr.project.srm.RewardedEntityTypeKafkaDto.MOVIE
import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import com.scr.project.srm.domains.rewarded.service.RewardedService
import com.scr.project.srm.entrypoint.mapper.toEntity
import com.scr.project.srm.entrypoint.messaging.v1.RewardedProcessorV1
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOffset
import reactor.kafka.receiver.ReceiverRecord
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class RewardedProcessorV1Test {

    private val rewardedReceiver = mockk<KafkaReceiver<String, RewardedKafkaDto>>()
    private val rewardedService = mockk<RewardedService>()
    private val receiverOffset = mockk<ReceiverOffset>()
    private val rewardedProcessor = RewardedProcessorV1(rewardedReceiver, rewardedService)

    @BeforeEach
    fun setUp() {
        clearMocks(rewardedReceiver, rewardedService, receiverOffset)
    }

    @Test
    fun `startConsuming should succeed with on message`() {
        val rewardedKafkaDto = RewardedKafkaDto(ObjectId.get().toHexString(), ACTOR)
        every { rewardedReceiver.receive() } answers {
            listOf(
                ReceiverRecord(
                    ConsumerRecord("topic", 0, 0, "key", rewardedKafkaDto),
                    receiverOffset
                )
            ).toFlux()
        }
        every { rewardedService.create(any<Rewarded>()) } answers { firstArg<Rewarded>().copy(id = ObjectId.get()).toMono() }
        every { receiverOffset.acknowledge() } just Runs
        rewardedProcessor.startConsuming()
            .test()
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete()
        verify(exactly = 1) { rewardedReceiver.receive() }
        verify(exactly = 1) { rewardedService.create(any<Rewarded>()) }
        verify(exactly = 1) { receiverOffset.acknowledge() }
        confirmVerified(rewardedReceiver, rewardedService, receiverOffset)
    }

    @Test
    fun `startConsuming should succeed when no message`() {
        every { rewardedReceiver.receive() } answers { Flux.empty() }
        rewardedProcessor.startConsuming()
            .test()
            .expectSubscription()
            .expectNextCount(0)
            .verifyComplete()
        verify(exactly = 1) { rewardedReceiver.receive() }
        verify(inverse = true) { rewardedService.create(any<Rewarded>()) }
        verify(inverse = true) { receiverOffset.acknowledge() }
        confirmVerified(rewardedReceiver, rewardedService, receiverOffset)
    }

    @Test
    fun `startConsuming should succeed with several messages`() {
        val rewardedKafkaDto1 = RewardedKafkaDto(ObjectId.get().toHexString(), ACTOR)
        val rewardedKafkaDto2 = RewardedKafkaDto(ObjectId.get().toHexString(), MOVIE)
        every { rewardedReceiver.receive() } answers
                {
                    listOf(
                        ReceiverRecord(ConsumerRecord("topic", 0, 0, "key", rewardedKafkaDto1), receiverOffset),
                        ReceiverRecord(ConsumerRecord("topic", 0, 0, "key", rewardedKafkaDto2), receiverOffset),
                    ).toFlux()
                }
        every { rewardedService.create(any<Rewarded>()) } answers { firstArg<Rewarded>().copy(id = ObjectId.get()).toMono() }
        every { receiverOffset.acknowledge() } just Runs
        rewardedProcessor.startConsuming()
            .test()
            .expectSubscription()
            .expectNextCount(2)
            .verifyComplete()
        verify(exactly = 1) { rewardedReceiver.receive() }
        verify(exactly = 1) { rewardedService.create(rewardedKafkaDto1.toEntity()) }
        verify(exactly = 1) { rewardedService.create(rewardedKafkaDto2.toEntity()) }
        verify(exactly = 2) { receiverOffset.acknowledge() }
        confirmVerified(rewardedReceiver, rewardedService, receiverOffset)
    }
}