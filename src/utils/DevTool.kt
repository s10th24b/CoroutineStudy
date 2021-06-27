package utils

object DevTool {
    fun printCurrentThread(tag: String = "default") {
        println("Running in (TAG: $tag) Thread[${Thread.currentThread().name}]")
    }
}