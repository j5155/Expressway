import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action

open class ActionRunner {
    var runningActions = mutableListOf<Action>()
    private val dash = FtcDashboard.getInstance()

    open fun runAsync(action: Action) {
        runningActions.add(action)
    }

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
}