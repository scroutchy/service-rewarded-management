package com.scr.project.srm.domains.rewarded.dao

import com.scr.project.commons.cinema.test.dao.GenericDao
import com.scr.project.srm.domains.rewarded.model.entity.Rewarded

class RewardedDao(mongoUri: String) : GenericDao<Rewarded>(mongoUri, Rewarded::class.java, "rewarded") {
}