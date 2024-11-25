import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.math.withSign

/**
 * PID controller with various feedforward components.
 * Originally from Roadrunner 0.5
 * Ported to Kotlin by Zach.Waffle and j5155
 */
class PIDFController
/**
 * Feedforward parameters `kV`, `kA`, and `kStatic` correspond with a basic
 * kinematic model of DC motors. The general function `kF` computes a custom feedforward
 * term for other plants.
 *
 * @param pid     traditional PID coefficients
 * @param kV      feedforward velocity gain
 * @param kA      feedforward acceleration gain
 * @param kStatic additive feedforward constant
 * @param kF      custom feedforward that depends on position and velocity
 */ @JvmOverloads constructor(
    private val pid: PIDCoefficients,
    private val kV: Double = 0.0,
    private val kA: Double = 0.0,
    private val kStatic: Double = 0.0,
    private val kF: FeedforwardFun = FeedforwardFun { x: Double, v: Double? -> 0.0 }
) {
    /**
     * Target position (that is, the controller setpoint).
     */
    var targetPosition: Int = 0

    /**
     * Target velocity.
     */
    var targetVelocity: Double = 0.0

    /**
     * Target acceleration.
     */
    var targetAcceleration: Double = 0.0

    /**
     * Error computed in the last call to [.update]
     */
    var lastError: Double = 0.0
    private var errorSum = 0.0
    private var lastUpdateTs: Long = 0
    private var inputBounded = false
    private var minInput = 0.0
    private var maxInput = 0.0
    private var outputBounded = false
    private var minOutput = 0.0
    private var maxOutput = 0.0

    constructor(
        pid: PIDCoefficients,
        kF: FeedforwardFun
    ) : this(pid, 0.0, 0.0, 0.0, kF)

    /**
     * Sets bound on the input of the controller. When computing the error, the min and max are
     * treated as the same value. (Imagine taking the segment of the real line between min and max
     * and attaching the endpoints.)
     *
     * @param min minimum input
     * @param max maximum input
     */
    fun setInputBounds(min: Double, max: Double) {
        assert(min < max) { "Min output must be less than max output!" }
            inputBounded = true
            minInput = min
            maxInput = max
    }

    /**
     * Sets bounds on the output of the controller.
     *
     * @param min minimum output
     * @param max maximum output
     */
    fun setOutputBounds(min: Double, max: Double) {
        assert(min < max) { "Min output must be less than max output!" }
            outputBounded = true
            minOutput = min
            maxOutput = max
    }


    fun getPositionError(measuredPosition: Double): Double {
        var error = targetPosition - measuredPosition
        if (inputBounded) {
            val inputRange = maxInput - minInput
            while (abs(error) > inputRange / 2.0) {
                error -= inputRange.withSign(error)
            }
        }
        return error
    }

    /**
     * Run a single iteration of the controller.
     *
     * @param timestamp        measurement timestamp as given by [System.nanoTime]
     * @param measuredPosition measured position (feedback)
     * @param measuredVelocity measured velocity
     */
    @JvmOverloads
    fun update(
        timestamp: Long,
        measuredPosition: Double,
        measuredVelocity: Double? = null
    ): Double {
        val error = getPositionError(measuredPosition)

        if (lastUpdateTs == 0L) {
            lastError = error
            lastUpdateTs = timestamp
            return 0.0
        }

        val dt = (timestamp - lastUpdateTs).toDouble()
        errorSum += 0.5 * (error + lastError) * dt
        val errorDeriv = (error - lastError) / dt

        lastError = error
        lastUpdateTs = timestamp
        val velError = if (measuredVelocity == null) {
            errorDeriv
        } else {
            targetVelocity - measuredVelocity
        }

        val baseOutput =
            pid.kP * error + pid.kI * errorSum + pid.kD * velError + kV * targetVelocity + kA * targetAcceleration +
                    kF.compute(measuredPosition, measuredVelocity)

        var output = 0.0
        if (abs(baseOutput) > 1e-6) {
            output = baseOutput + kStatic.withSign(baseOutput)
        }

        if (outputBounded) {
            return max(minOutput, min(output, maxOutput))
        }

        return output
    }

    fun update(
        measuredPosition: Double
    ): Double {
        return update(measuredPosition=measuredPosition)
    }

    @JvmOverloads
    fun updateSquid( // TODO this should be it's own class (extending this one)
        timestamp: Long,
        measuredPosition: Double,
        measuredVelocity: Double? = null
    ): Double {
        val result = update(timestamp, measuredPosition, measuredVelocity)

        return sqrt(abs(result)) * sign(result)
    }

    fun updateSquid(
        measuredPosition: Double
    ): Double {
        return updateSquid(measuredPosition=measuredPosition)
    }

    /**
     * Reset the controller's integral sum.
     */
    fun reset() {
        errorSum = 0.0
        lastError = 0.0
        lastUpdateTs = 0
    }

    fun interface FeedforwardFun {
        fun compute(position: Double, velocity: Double?): Double
    }

    data class PIDCoefficients(var kP: Double, var kI: Double, var kD: Double) // TODO do these need to be vars?
}