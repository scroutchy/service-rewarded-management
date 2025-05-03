package com.scr.project.srm.domains.rewarded.dao

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class DaoTestConfiguration {

    @Bean
    fun rewardedDao(@Value("\${spring.data.mongodb.uri}") mongoUri: String) = RewardedDao(mongoUri)
}