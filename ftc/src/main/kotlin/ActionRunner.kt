import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action

/**
 * ActionRunner allows for running actions asynchronously during an OpMode.
 */
class ActionRunner {
    private val runningActions = mutableListOf<Action>()
    private val dash: FtcDashboard by lazy { FtcDashboard.getInstance() }
    private val canvas: Canvas by lazy { Canvas() }
open class ActionRunner {
    var runningActions = mutableListOf<Action>()
    private val dash = FtcDashboard.getInstance()

    /**
     * Adds [action] to the list of running actions.
     */
    open fun runAsync(action: Action) {
        runningActions.add(action)
    }

    /**
     * Runs each action that was added using [runAsync] in the order that they were called to [runAsync].
     * Should be called exactly once per loop.
     */

    open fun updateAsync(packet: TelemetryPacket = DefaultPacket()) {
        val newActions = ArrayList<Action>()

        for (action in runningActions) {
            action.preview(packet.fieldOverlay())
            if (action.run(packet)) {
                newActions.add(action)
            }
        }

        runningActions = newActions

        // if no packet was specified, we have to create and send one ourselves
        // this feels kinda jank tbh
        if (packet is DefaultPacket) {
            dash.sendTelemetryPacket(packet)
        }
    }


    // used to differentiate whether we just made the packet or whether it was just passed
    private class DefaultPacket(drawDefaultField: Boolean = true) : TelemetryPacket(drawDefaultField)

    companion object {
        /**
         * Runs [action] until it returns false.
         * Equivalent to calling `Actions.runBlocking(action)`.
         */
        @JvmStatic
        fun runBlocking(action: Action) {
            val canvas = Canvas()
            action.preview(canvas)

            var actionStillRunning = true
            while (actionStillRunning && !Thread.currentThread().isInterrupted) {
                val packet = TelemetryPacket()
                packet.fieldOverlay().operations.addAll(canvas.operations)

                actionStillRunning = action.run(packet)

                FtcDashboard.getInstance().sendTelemetryPacket(packet)
            }
        }
    }
}