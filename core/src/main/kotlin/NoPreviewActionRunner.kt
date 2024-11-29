package page.j5155.expressway.actions

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action

/**
 * ActionRunner allows for running actions asynchronously during an OpMode.
 * This version has no preview, allowing it to be used without depending on FTC Dashboard
 */
open class NoPreviewActionRunner(val sendTelemetryPacket: (TelemetryPacket) -> Unit = {}) {
    var runningActions = mutableListOf<Action>()

    /**
     * Adds [action] to the list of running actions.
     */
    open fun runAsync(action: Action) {
        try {
            runningActions.add(action)
        } catch (_: ConcurrentModificationException) {
            throw RuntimeException("An action attempted to queue another action with runAsync. Actions should instead wrap other actions to begin running them.")
        }
    }

    /**
     * Runs each action that was added using [runAsync] in the order that they were called to [runAsync].
     * Should be called exactly once per loop.
     */
    @JvmOverloads
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
            sendTelemetryPacket(packet)
        }
    }


    // used to differentiate whether we just made the packet or whether it was just passed
    private class DefaultPacket(drawDefaultField: Boolean = true) : TelemetryPacket(drawDefaultField)
}