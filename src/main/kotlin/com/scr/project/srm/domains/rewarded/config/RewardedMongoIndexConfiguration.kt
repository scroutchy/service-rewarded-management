package com.scr.project.srm.domains.rewarded.config

import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.Index

@Configuration(proxyBeanMethods = false)
class RewardedMongoIndexConfiguration(mongoTemplate: ReactiveMongoTemplate) {

    init {
        rewardedIndexes(mongoTemplate)
    }

    private fun rewardedIndexes(mongoTemplate: ReactiveMongoTemplate) {
        mongoTemplate.indexOps(Rewarded::class.java)
            .createIndex(
                Index().on(Rewarded::type.name, ASC)
                    .on(Rewarded::rewardedId.name, ASC)
                    .unique()
            ).subscribe()
    }
}
