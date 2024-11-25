import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action

/**
 * ActionRunner allows for running actions asynchronously during an OpMode.
 */
class ActionRunner {
    private val runningActions = mutableListOf<Action>()
    private val dash: FtcDashboard by lazy { FtcDashboard.getInstance() }
    private val canvas: Canvas by lazy { Canvas() }

    /**
     * Adds [action] to the list of running actions.
     */
    fun runAsync(action: Action) {
        runningActions.add(action)
    }

    /**
     * Runs each action that was added using [runAsync] in the order that they were called to [runAsync].
     * Should be called exactly once per loop.
     */
    fun updateAsync() {
        val packet = TelemetryPacket()
        packet.fieldOverlay().operations.addAll(canvas.operations)
        val iter: MutableIterator<Action> = runningActions.iterator()
        while (iter.hasNext() && !Thread.currentThread().isInterrupted) {
            val action: Action = iter.next()
            if (!action.run(packet)) iter.remove()
        }
        dash.sendTelemetryPacket(packet)
    }

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