//package com.github.orezzero.easyitplugin.ui.project.view
//
//import com.github.orezzero.easyitplugin.index.file.KeyValue
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.vfs.VirtualFile
//
//class Value(project: Project, file: VirtualFile, value: KeyValue) {
//
//    var destination: Destination
//    val anchorAttributes = mutableMapOf<String, String>()
//
//    val name = value.name
//
//    init {
//        value.dest.substringAfter("#").let { parseAnchor(it) }
//        val line = anchorAttributes["L"]?.let { Integer.valueOf(it) - 1 } ?: 0
//        destination = Destination(project, file, line, anchorAttributes)
//    }
//
//    private fun parseAnchor(anchor: String) {
//        val split = anchor.split("&")
//        for (s in split) {
//            val equalPairs = s.split("=")
//            when (equalPairs.size) {
//                1 -> parseSingleAnchor(s, anchorAttributes)
//                2 -> anchorAttributes[equalPairs[0]] = equalPairs[1]
//                else -> {}
//            }
//        }
//    }
//
//    companion object {
//        fun parseSingleAnchor(s: String, anchorAttributes: MutableMap<String, String>) {
//            var lastLetter = 0
//            for ((index, c) in s.withIndex()) {
//                if (c.isLetter()) {
//                    lastLetter = index
//                }
//            }
//            if (lastLetter == s.length - 1) {
//                anchorAttributes[s] = s
//            } else {
//                anchorAttributes[s.substring(0, lastLetter + 1)] = s.substring(lastLetter + 1)
//            }
//        }
//
//    }
//
//
//}
//
////fun main() {
////  var s = "L2d1"
////  val map = mutableMapOf<String, String>()
////  parseSingleAnchor(s, map)
////
////  for (entry in map) {
////    println(entry.key)
////    println(entry.value);
////  }
////}
