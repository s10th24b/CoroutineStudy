import kotlinx.coroutines.*
import java.lang.UnsupportedOperationException
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

@OptIn(InternalCoroutinesApi::class)
fun main() {
    runBlocking {
        val job = GlobalScope.launch {
            // 기본적으로 잡 내부에서 발생한 예외는 잡을 생성한 곳까지 전파된다. 잡이 완료되기까지 기다리지 않아도 발생한다.
            throw UnsupportedOperationException("Can't do!")
        }
        delay(500) // 충분한 시간동안 앱을 실행해 앱이 끝나기 전에 예외가 발생할 수 있도록 함.
        // job.join()을 하지 않더라도 발생한다.
    }
    runBlocking {
        val job = Job()
    }

    // 실행
    runBlocking {
        val job = GlobalScope.launch(start = CoroutineStart.LAZY) { // 잡을 자동으로 시작되지 않게.
            throw UnsupportedOperationException("can't do!")
        }
        job.start() // 이렇게 원할 때 명시적으로 시작할 수 있음. 실행을 일시중단하지 않으므로 suspend나 코루틴에서 호출할 필요 없음.
        job.join() // join을 사용하면 애플리케이션이 job을 완료할 때까지 대기함. suspend나 코루틴에서 호출해야함. (runblocking)
    }

    // 취소
    runBlocking {
        val job = GlobalScope.launch(start = CoroutineStart.LAZY) { // 잡을 자동으로 시작되지 않게.
            throw UnsupportedOperationException("can't do!")
        }
        job.cancel() // 실행 취소 요청. 잡 실행은 2초후에 취소됨.
        job.cancelAndJoin() // 취소 요청하고 끝날 때까지 대기.
        val cancellation = job.getCancellationException()
        println("${cancellation.message}, ${cancellation.cause}")
        delay(500)
    }
    runBlocking {
        // 내가 취소요청한 잡과 예외로 인해 실패한 잡을 구별하기 위해 예외핸들러를 설정. CoroutineExceptionHandler
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Job cancelled due to ${throwable.message}")
        }
        GlobalScope.launch(exceptionHandler) {
            throw UnsupportedOperationException("can't do")
        }
        delay(2000)
    }
    // 예외 핸들러를 만들지 않고, invokeOnCompletion() 을 사용할 수도 있다.
    runBlocking {
        val job = GlobalScope.launch {
            throw UnsupportedOperationException("can't do")
        }
        job.invokeOnCompletion { cause ->
            cause?.let {
                println("Job cancelled due to ${it.message}")
            }
        }
        when {
            job.isActive -> {
            }
            job.isCancelled -> {
            }
            job.isCompleted -> {
            }
        }
    }

    //완료. 실행이 중지된 Job은 완료됨으로 간주된다. 이는 실행이 정상적으로 종료됐거나 취소됐는지 또는 예외때문에 종료됐는지 여부에 관계없이 적용.
    // 이러한 이유로 취소된 항목은 완료된 항목의 하위 항목으로 간주되기도 한다.
    // RxJAVA랑 많이 비슷하네..


    // Deferred
    runBlocking {
        // Deferred를 만들려면 async를 사용할 수 있다.
        val deferred = GlobalScope.async {
            getHeadlines()
            throw UnsupportedOperationException("can't do")
        }
        deferred.join() // 예외를 전파하지 않고 처리.
//        deferred.await() //  예외 전파

        // 순수한 잡과 달리 디퍼드는 처리되지않은 예외를 자동으로 전파하지 않는다.

//         또는 CompletableDeferred 도 사용할 수 있다.
//        val articleTask = CompletableDeferred<List<Article>>()
    }
    runBlocking {
        val deferred = GlobalScope.async {
            TODO ("todo")
//            throw UnsupportedOperationException("can't do")
        }
        delay(1000) // 디퍼드의 실행을 모니터링하지않는 시나리오를 재현하도록 delay 사용.
        // 디퍼드는 모니터링하도록 되어있으므로 이렇게 하면 안된다. 예외를 쉽게 전파하려면 await를 쓴다.

        // 디퍼드의 실행이 코드 흐름의 필수적인 부분임을 나타내는 것이기 때문에 await()를 호출하는 이런 방식으로 설계됐다.
        // 이 방식을 사용하면 명령형으로 보이는 비동기코드를 더 쉽게 작성할 수 있고, try-catch 문으로 예외 처리 가능.
        try {
            deferred.await()
        } catch (throwable: Throwable) {
            println("Deferred cancelled due to ${throwable.message}")
        }
    }
    runBlocking {
        // 잡은 특정 상태에 도달하면 이전 상태로 되돌아가지 않는다. 즉, 상태는 한 방향으로만 이동한다.
        val time = measureTimeMillis {
            val job = GlobalScope.launch {
                delay(2000)
            }
            // Wait for it to complete once
            job.join()

            // Restart the Job
            job.start()
            job.join()
        }
        println("Took $time ms")
    }
}

fun getHeadlines() {

}