package com.scr.project.srm.entrypoint.messaging.v1

import reactor.core.publisher.Flux
import reactor.kafka.receiver.ReceiverRecord

fun interface KafkaProcessor<T> {

    fun startConsuming(): Flux<ReceiverRecord<String, T>>
}