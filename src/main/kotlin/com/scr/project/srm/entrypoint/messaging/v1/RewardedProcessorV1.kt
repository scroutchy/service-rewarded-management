package com.scr.project.srm.entrypoint.messaging.v1

import com.scr.project.commons.cinema.kafka.processor.KafkaProcessor
import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.domains.rewarded.service.RewardedService
import com.scr.project.srm.entrypoint.mapper.toEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverRecord

@Component
class RewardedProcessorV1(
    private val rewardedReceiver: KafkaReceiver<String, RewardedKafkaDto>,
    private val rewardedService: RewardedService
) : KafkaProcessor<RewardedKafkaDto> {

    private val logger: Logger = LoggerFactory.getLogger(RewardedProcessorV1::class.java)

    override fun startConsuming(): Flux<ReceiverRecord<String, RewardedKafkaDto>> {
        return rewardedReceiver.receive()
            .flatMap { r ->
                rewardedService.create(r.value().toEntity())
                    .doOnSubscribe { logger.debug("Receiving message: ${r.value()}") }
                    .doOnSuccess {
                        logger.info("Message processed: ${r.value()}")
                        r.receiverOffset().acknowledge()
                    }
                    .doOnError { logger.error("Error processing message: ${r.value()}", it) }
                    .thenReturn(r)
            }
    }
}