import android.R.attr.action
import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.RobotLog.a
import java.util.ArrayList
import java.util.concurrent.CompletableFuture.runAsync
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

abstract class ActionLinearOpMode(val runner: ActionRunner = ActionRunner()) : LinearOpMode() {
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


    @Deprecated("legacy wrapper", ReplaceWith("runAsync(action)"))
    protected fun run(action: Action) = runAsync(action)
}
