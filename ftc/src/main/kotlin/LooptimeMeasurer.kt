import com.qualcomm.robotcore.util.ElapsedTime
import java.util.LinkedList

private const val loopsToStore = 1000
class LooptimeMeasurer {
    val loopsList = LinkedList<Double>() // ms
    val timer = ElapsedTime()

    fun update() {
        loopsList.add(timer.milliseconds())
        if (loopsList.size > loopsToStore) {
            loopsList.removeFirst()
        }
        // this isn't necessarily ideal
        // but it seems to generally be fine?
        // on my laptop sorting a 1000 sized list took 0.0014ms on average
        // and less then 0.45464ms 99.9992% of the time
        loopsList.sort()
        timer.reset()
    }

    fun getAverage(): LoopLength {
        return LoopLength(loopsList.average())
    }



}

data class LoopLength(val ms: Double) {
    val hz: Double
        get() {
            return 1000.0 / ms
        }
}