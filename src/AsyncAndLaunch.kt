import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.lang.UnsupportedOperationException

fun main() {
    val netDispatcher = newSingleThreadContext(name = "ServiceContext")
    runBlocking {
        val task = GlobalScope.async {
            doSomething()
        }
        task.join()
        if (task.isCancelled) {
            val exception = task.getCancellationException()
            println("${exception.cause}")
        } else {
            println("Task Success")
        }
    }
}

fun doSomething() {
    throw UnsupportedOperationException("Can't do")
}