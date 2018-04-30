package edu.ycp.cs482.iorcapi.model

import edu.ycp.cs482.iorcapi.model.attributes.StatQL
import edu.ycp.cs482.iorcapi.model.attributes.VersionInfo
import edu.ycp.cs482.iorcapi.model.authentication.AccessData
import org.springframework.data.annotation.Id

data class Version(
        @Id val version: String,
        val access: List<AccessData> // list of owners for r/w
)

data class VersionQL(
        val version: String,
        val stats: List<StatQL>,
        val infoList: List<VersionInfo> = listOf()
)