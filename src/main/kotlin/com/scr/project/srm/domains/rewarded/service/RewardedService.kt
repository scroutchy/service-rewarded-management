package com.scr.project.srm.domains.rewarded.service

import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import com.scr.project.srm.domains.rewarded.ports.RewardedPort
import com.scr.project.srm.domains.rewarded.repository.RewardedRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RewardedService(val rewardedRepository: RewardedRepository) : RewardedPort {

    private val logger: Logger = LoggerFactory.getLogger(RewardedService::class.java)

    override fun create(rewarded: Rewarded): Mono<Rewarded> {
        return rewardedRepository.insert(rewarded)
            .doOnSubscribe { logger.debug("Creating rewarded") }
            .doOnSuccess { logger.info("Creation of rewarded with id ${it.id} and rewardedId ${it.rewardedId} was successful.") }
            .doOnError { logger.warn("Error when creating rewarded with rewardedId ${rewarded.rewardedId}.") }
    }
}