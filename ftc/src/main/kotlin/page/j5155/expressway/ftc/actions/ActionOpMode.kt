package page.j5155.expressway.ftc.actions

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.eventloop.opmode.OpMode

abstract class ActionOpMode(val runner: ActionRunner = ActionRunner()) : OpMode() {
    /**
     * Run an action asynchronously.
     * Note: you MUST run updateActions every loop so that the actions queued by runAsync continue.
     */
    protected fun runAsync(action: Action) {
        runner.runAsync(action)
    }


    protected fun updateActions(packet: TelemetryPacket? = null) {
        if (packet == null) {
            runner.updateAsync()
        } else {
            runner.updateAsync(packet)
        }
    }


    @Deprecated("legacy wrapper",ReplaceWith("runAsync(action)"))
    protected fun run(action: Action) = runAsync(action)
}