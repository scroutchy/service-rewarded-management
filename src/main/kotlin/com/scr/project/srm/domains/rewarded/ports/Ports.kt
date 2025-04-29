package com.scr.project.srm.domains.rewarded.ports

import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import reactor.core.publisher.Mono

fun interface RewardedPort {

    fun create(rewarded: Rewarded): Mono<Rewarded>
}