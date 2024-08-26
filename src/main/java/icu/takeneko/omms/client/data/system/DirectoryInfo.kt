package icu.takeneko.omms.client.data.system

import icu.takeneko.omms.client.util.Result

data class DirectoryInfo(
    var folders: List<String>,
    var files: List<String>,
    var result: Result = Result.UNDEFINED
)