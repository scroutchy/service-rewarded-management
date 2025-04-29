package com.scr.project.srm.domains.rewarded.repository

import com.scr.project.srm.domains.rewarded.model.entity.Rewarded
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface RewardedRepository : ReactiveMongoRepository<Rewarded, String>