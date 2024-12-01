package page.j5155.expressway.ftc.actions

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

    private var initInternal: Supplier<Unit> = Supplier { }
    private var loopInternal: Consumer<TelemetryPacket> = Consumer { }
    private var cleanupInternal: Supplier<Unit> = Supplier { }

    /**
     * Set the initialization function
     * @param init the initialization function
     */
    fun setInit(init: Supplier<Unit>): ActionBuilder {
        initInternal = init
        return this
    }

    /**
     * Set the loop function
     * @param loop the loop function
     */
    fun setLoop(loop: Consumer<TelemetryPacket>): ActionBuilder {
        loopInternal = loop
        return this
    }

    /**
     * Set the cleanup function
     * @param cleanup the cleanup function
     */
    fun setCleanup(cleanup: Supplier<Unit>): ActionBuilder {
        cleanupInternal = cleanup
        return this
    }

    /**
     * Builds the InitLoopCondAction
     */
    fun build(): InitLoopCondAction {
        return object: InitLoopCondAction(condition) {
            override fun init() = initInternal.get()

            override fun loop(p: TelemetryPacket) = loopInternal.accept(p)

            override fun cleanup() = cleanupInternal.get()
        }
    }
}