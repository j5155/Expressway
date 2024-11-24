package org.firstinspires.ftc.teamcode.helpers

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.canvas.Canvas
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.helpers.ActionOpMode.UniqueAction
import java.util.ArrayList
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

abstract class ActionOpMode : LinearOpMode() {
    private val dash: FtcDashboard = FtcDashboard.getInstance()
    var runningActions = ArrayList<Action>()
    private val uniqueActionsQueue = ArrayList<UniqueAction>()

    protected fun runBlocking(action: Action) {
        val canvas = Canvas()
        action.preview(canvas)

        var actionStillRunning = true
        while (actionStillRunning && !isStopRequested) {
            val packet = TelemetryPacket()
            packet.fieldOverlay().operations.addAll(canvas.operations)

            actionStillRunning = action.run(packet)

            dash.sendTelemetryPacket(packet)
        }
    }

    protected fun updateAsync(packet: TelemetryPacket = DefaultPacket()) {
        updateUniqueQueue()
        // update running actions
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

    private fun updateUniqueQueue() {
        val oldActions = uniqueActionsQueue
        uniqueActionsQueue.clear()
        // running run on a UniqueAction will automatically re add it to the queue, or start running it
        oldActions.forEach { this.run(it) }
    }

    protected fun run(a: Action) {
        if (duplicated(a)) {
            uniqueActionsQueue.add(a)
        } else {
            runningActions.add(a)
        }
    }

    protected fun runNoQueue(a: Action) {
        if (!duplicated(a)) {
            runningActions.add(a)
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun duplicated(a: Action): Boolean {
        contract {
            // this allows the other function to add it to the uniqueActionsQueue without casting
            returns(true) implies (a is UniqueAction)
        }
        return a is UniqueAction && runningActions.stream().anyMatch {
            it is UniqueAction && it.key == a.key
        }
    }


    class UniqueAction(val action: Action, val key: String = "UniqueAction") : Action by action

    // used to differentiate whether we just made the packet or whether it was just passed
    private class DefaultPacket(drawDefaultField: Boolean = true): TelemetryPacket(drawDefaultField)
}
