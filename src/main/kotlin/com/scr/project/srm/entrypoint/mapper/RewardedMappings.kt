package com.scr.project.srm.entrypoint.mapper

import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import com.scr.project.srm.domains.rewarded.model.entity.RewardedType

fun RewardedKafkaDto.toEntity() = Rewarded(id, RewardedType.valueOf(type.name))