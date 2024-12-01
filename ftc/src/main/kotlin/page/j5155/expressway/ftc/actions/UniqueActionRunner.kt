package page.j5155.expressway.ftc.actions

import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class UniqueActionRunner: ActionRunner() {
    private val uniqueActionsQueue = ArrayList<UniqueAction>()


    override fun updateAsync(packet: TelemetryPacket) {
        updateUniqueQueue()
        super.updateAsync(packet)
    }

    private fun updateUniqueQueue() {
        val oldActions = uniqueActionsQueue
        uniqueActionsQueue.clear()
        // running run on a UniqueAction will automatically re add it to the queue, or start running it
        oldActions.forEach { this.runAsync(it) }
    }

    override fun runAsync(action: Action) {
        if (duplicated(action)) {
            uniqueActionsQueue.add(action)
        } else {
            super.runAsync(action)
        }
    }

    fun runNoQueue(action: Action) {
        if (!duplicated(action)) {
            runningActions.add(action)
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
}