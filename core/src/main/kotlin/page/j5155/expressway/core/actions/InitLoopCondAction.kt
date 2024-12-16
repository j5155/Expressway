package page.j5155.expressway.core.actions

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import java.util.function.Supplier


/**
 * Allows for the creation of an Action using init and loop methods and a boolean-returning continuity condition.
 * Like InitLoopAction, but with the continuity condition seperated out
 * @param condition continues the action while the condition is true; false to stop
 */
abstract class InitLoopCondAction protected constructor(val condition: Supplier<Boolean>) : Action {
    /**
     * Initializes the action.
     * This will always run before [loop].
     */
    open fun init() {}

    /**
     * Contents of the action.
     * This will repeat while [condition] is true.
     */
    abstract fun loop(p: TelemetryPacket)

    /**
     * Method to run once [condition] becomes false.
     * For example, this can be used to stop motors.
     */
    open fun cleanup() {}

    private var hasInit = false
    var stop = false

    val isRunning: Boolean
        get() = condition.get()

    final override fun run(p: TelemetryPacket): Boolean {
        if (!hasInit) {
            init()
            hasInit = true
        }

        loop(p)

        return if (condition.get()) {
            true
        } else {
            cleanup()
            false
        }
    }
}

