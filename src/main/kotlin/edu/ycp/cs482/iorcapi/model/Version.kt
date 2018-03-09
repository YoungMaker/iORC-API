package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo

data class Version(
        val version: String,
        val stats: List<StatQL>,
        val infoList: List<VersionInfo> = listOf()
)