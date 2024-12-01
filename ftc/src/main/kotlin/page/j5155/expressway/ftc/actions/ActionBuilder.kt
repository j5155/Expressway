package page.j5155.expressway.ftc.actions

import android.app.Notification.Action
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import page.j5155.expressway.core.actions.Condition
import page.j5155.expressway.core.actions.InitLoopCondAction
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * This class builds an InitLoopCondAction
 * @param condition the condition for the action to complete
 */
class ActionBuilder(val condition: Condition) {

    private var initLambda: () -> Unit = { }
    private var loopLambda: (TelemetryPacket) -> Unit = { }
    private var cleanupLambda: () -> Unit = { }

    /**
     * Set the initialization function
     * @param initialization the initialization function
     */
    fun setInit(initialization: () -> Unit): ActionBuilder {
        initLambda = initialization
        return this
    }

    /**
     * Set the initialization function
     * @param initialization the initialization function
     */
    fun setInit(initialization: Supplier<Unit>): ActionBuilder {
        initLambda = { initialization.get() }
        return this
    }

    /**
     * Set the loop function
     * @param loop the loop function
     */
    fun setLoop(loop: (TelemetryPacket) -> Unit): ActionBuilder {
        loopLambda = loop
        return this
    }

    /**
     * Set the loop function
     * @param loop the loop function
     */
    fun setLoop(loop: Consumer<TelemetryPacket>): ActionBuilder {
        loopLambda = { loop.accept(it) }
        return this
    }

    /**
     * Set the cleanup function
     * @param cleanup the cleanup function
     */
    fun setCleanup(cleanup: () -> Unit): ActionBuilder {
        cleanupLambda = cleanup
        return this
    }

    /**
     * Set the cleanup function
     * @param cleanup the cleanup function
     */
    fun setCleanup(cleanup: Supplier<Unit>): ActionBuilder {
        cleanupLambda = { cleanup.get() }
        return this
    }

    /**
     * Builds the InitLoopCondAction
     */
    fun build(): InitLoopCondAction {
        return object: InitLoopCondAction(condition) {
            override fun loop(p: TelemetryPacket) {
                loopLambda(p)
            }

            override fun init() {
                initLambda()
            }

            override fun cleanup() {
                cleanupLambda()
            }
        }
    }
}