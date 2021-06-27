import kotlinx.coroutines.*
import java.lang.UnsupportedOperationException
import utils.DevTool.printCurrentThread

@InternalCoroutinesApi
//fun main() {
fun main() = runBlocking {
    val netDispatcher = newSingleThreadContext(name = "ServiceContext")
    printCurrentThread("main")
    runBlocking {
        printCurrentThread("runBlocking")
        val task = GlobalScope.async { // async는 결과가 반환되는 코루틴을 위함.
            printCurrentThread("GlobalScope")
            doSomething()
        }
        task.join() // join은 예외를 전파하지 않고 처리함.
//        task.await() // await는 호출하는 것만으로 예외를 전파함. 실행이 중단됨.
        if (task.isCancelled) {
            val exception = task.getCancellationException()
            println("${exception.cause}")
        } else {
            println("Task Success")
        }
        println("async finished")
    }
    runBlocking {
        printCurrentThread("runBlocking")
//        val task = GlobalScope.launch { // launch는 결과가 반환되지 않는 코루틴을 위함.
        val task = GlobalScope.launch(netDispatcher) { // 지정된 스레드에서 코루틴이 실행.
            printCurrentThread("GlobalScope")
            doSomething()
        }
        task.join() // launch이므로 예외가 발생돼 스택에 출력되지만 실행이 중단되지는 않음.
        if (task.isCancelled) {
            val exception = task.getCancellationException()
            println("${exception.cause}")
        } else {
            println("Task Success")
        }
        println("launch finished")
    }
}

fun doSomething() {
    throw UnsupportedOperationException("Can't do")
}