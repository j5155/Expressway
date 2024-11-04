@file:JvmName("ActionHelpers")
package page.j5155.actionAdditions

import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.acmerobotics.roadrunner.SleepAction
import com.acmerobotics.roadrunner.now
import java.util.function.Supplier





// Released under the MIT License and the BSD-3-Clause license by j5155 (you may use it under either one)

/**
 * Takes any number of actions as input and runs them all in parallel
 *
 * Force-stops them all as soon as one ends
 * @param actions actions to run in parallel
 */
open class RaceParallelAction(vararg val actions: Action) : Action {
    override fun run(t: TelemetryPacket): Boolean {
        var finished = true
        for (action in actions) finished = finished && action.run(t)
        return finished
    }

    override fun preview(canvas: Canvas) {
        for (action in actions) action.preview(canvas)
    }
}

/**
 * Alias for RaceParallelAction
 * @see RaceParallelAction
 */
class RaceParallelCommand(vararg actions: Action) : RaceParallelAction(*actions) // pass vararg with spread operator

/**
 * Runs an input action in parallel with an update function
 */
class ActionWithUpdate(val action: Action, val func: Runnable) : Action by action {
    override fun run(t: TelemetryPacket): Boolean {
        func.run()
        return action.run(t)
    }
}

/**
 * Runs an action with a configurable timeout in seconds, defaulting to 5 seconds.
 *
 * After the timeout expires, the action will be FORCIBLY stopped.
 * Ensure that this will not leave your robot in a dangerous state (motors still moving, etc.) that is not resolved by another action.
 */
class TimeoutAction(val action: Action, val timeout: Double = 5.0) : Action by action {
    private var startTime = 0.0

    override fun run(t: TelemetryPacket): Boolean {
        if (startTime == 0.0) startTime = now()
        if (now() - startTime > timeout) return false
        return action.run(t)
    }
}

/** Takes two actions and a condition supplier as inputs.
 * Runs the first action if the condition is true and runs the second action if it is false.
 *
 * Written by j5155, released under the MIT License and the BSD-3-Clause license (you may use it under either one)
 *
 * Example usage:
 * In Java:
 * ```java
 * new ConditionalAction(
 *                  new SleepAction(1), // will be run if the conditional is true
 *                  new SleepAction(2), // will be run if the conditional is false
 *                  () -> Math.random() > 0.5); // lambda conditional function, returning either true or false;
 *          // this example picks which one to run randomly
 * ```
 * Or in Kotlin:
 * ```kotlin
 * fun choose(): Action {
 *     return ConditionalAction(
 *         SleepAction(1.0),  // will be run if the conditional is true
 *         SleepAction(2.0) // will be run if the conditional is false
 *     ) { Math.random() > 0.5 }
 *     // lambda conditional function, returning either true or false;
 *     // this example picks which one to run randomly
 * }
 * ```
 */
class ConditionalAction(val trueAction: Action, val falseAction: Action, val condition: Supplier<Boolean>) :
    InitLoopAction() {
    private var chosenAction: Action? = null

    override fun init() {
        chosenAction =
            if (condition.get()) { // use .get() to check the value of the condition by running the input function
                trueAction // and then save the decision to the chosenAction variable
            } else {
                falseAction
            }
    }

    override fun loop(t: TelemetryPacket): Boolean {
        // every loop, pass through the chosen action
        return chosenAction!!.run(t)
    }

    // ambiguous which one to preview, so preview both
    override fun preview(canvas: Canvas) {
        trueAction.preview(canvas)
        falseAction.preview(canvas)
    }
}



abstract class InitLoopAction : Action {
    private var initialized = false

    /**
     * Run exactly once the first time the action is run.
     */
    abstract fun init()

    /**
     * Run every loop. Init is guaranteed to have been run once before this.
     * @return whether to continue with the action; true to continue looping, false to end
     */
    abstract fun loop(t: TelemetryPacket): Boolean

    final override fun run(t: TelemetryPacket): Boolean { // final to prevent downstream classes from overriding it
        if (!initialized) {
            init()
            initialized = true
        }
        return loop(t)
    }
    // intentionally not overriding preview
}

