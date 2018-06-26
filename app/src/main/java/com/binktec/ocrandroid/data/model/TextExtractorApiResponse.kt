package com.binktec.ocrandroid.data.model

import android.arch.persistence.room.Entity

data class TextExtractorApiResponse(val response: Response?, val time: Number?, val ok: Boolean?)

data class Entities(val id: Number?, val type: List<String>?, val matchingTokens: List<Number>?, val entityId: String?, val freebaseTypes: List<String>?, val confidenceScore: Number?, val wikiLink: String?, val matchedText: String?, val freebaseId: String?, val relevanceScore: Number?, val entityEnglishId: String?, val startingPos: Number?, val endingPos: Number?, val wikidataId: String?)

data class Response(val language: String?, val languageIsReliable: Boolean?, val entities: List<Entities>?)