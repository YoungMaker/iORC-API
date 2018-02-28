package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.StatQL

data class Version(
        val version: String,
        val stats: List<StatQL>
)