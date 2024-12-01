package page.j5155.expressway.ftc

import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sqrt

class SquidController: PIDFController {
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
     */
    @JvmOverloads
    constructor(
        pid: PIDCoefficients,
        kV: Double = 0.0,
        kA: Double = 0.0,
        kStatic: Double = 0.0,
        kF: FeedforwardFun = FeedforwardFun { x: Double, v: Double? -> 0.0 }
    ) : super(pid,kV,kA,kStatic,kF)

    constructor(
            pid: PIDCoefficients,
            kF: FeedforwardFun
    ) : this(pid, 0.0, 0.0, 0.0, kF)

    override fun update(
        timestamp: Long,
        measuredPosition: Double,
        measuredVelocity: Double?
    ): Double {
        val result = update(timestamp, measuredPosition, measuredVelocity)

        return sqrt(abs(result)) * sign(result)
    }
}