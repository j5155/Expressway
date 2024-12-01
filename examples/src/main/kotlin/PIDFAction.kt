import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.acmerobotics.roadrunner.InstantAction
import com.qualcomm.robotcore.hardware.DcMotor
import page.j5155.expressway.ftc.motion.PIDFController

class PIDFAction(private val motor: DcMotor, target: Int,coefficients: PIDFController.PIDCoefficients) : Action {
    private var initialized = false

    val pidf = PIDFController(coefficients)
    var target = target
        set(value) {
            pidf.targetPosition = value
            field = value
        }

    override fun run(p: TelemetryPacket): Boolean {
        if (!initialized) {
            pidf.targetPosition = target
            initialized = true
        }

        val position = motor.currentPosition
        val power = pidf.update(position.toDouble())

        p.put("Motor Info", "Target: $target; Error ${target-position}; Power: $power")

        motor.power = power

        return position in (target - 50)..(target + 50)
    }

    fun update(target: Int) : Action = InstantAction { this.target = target }
}
