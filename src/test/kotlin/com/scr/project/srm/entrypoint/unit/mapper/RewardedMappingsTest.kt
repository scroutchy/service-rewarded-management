package com.scr.project.srm.entrypoint.unit.mapper

import com.scr.project.srm.RewardedEntityTypeKafkaDto.ACTOR
import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.entrypoint.mapper.toEntity
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

class RewardedMappingsTest {

    @Test
    fun `toEntity should succeed`() {
        val kafkaDto = RewardedKafkaDto(ObjectId.get().toHexString(), ACTOR)
        val entity = kafkaDto.toEntity()
        assertThat(entity).isNotNull
        assertThat(entity.id).isNull()
        assertThat(entity.rewardedId).isEqualTo(kafkaDto.id)
        assertThat(entity.type.name).isEqualTo(kafkaDto.type.name)
        assertThat(entity.rewards).hasSize(0)
    }
}