import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action

class ActionRunner {
    private val runningActions = mutableListOf<Action>()
    private val dash: FtcDashboard by lazy { FtcDashboard.getInstance() }
    private val canvas: Canvas by lazy { Canvas() }

    fun runAsync(action: Action) {
        runningActions.add(action)
    }

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
}