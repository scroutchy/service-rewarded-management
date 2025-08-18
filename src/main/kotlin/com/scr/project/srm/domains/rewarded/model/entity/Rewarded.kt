package com.scr.project.srm.domains.rewarded.model.entity

import com.scr.project.commons.cinema.model.entity.Auditable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Rewarded(
    val rewardedId: String,
    val type: RewardedType,
    val rewards: List<Reward> = listOf(),
    @field:Id @BsonId var id: ObjectId? = null
) : Auditable()