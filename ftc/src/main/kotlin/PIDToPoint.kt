import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.PoseVelocity2d
import com.acmerobotics.roadrunner.Vector2d
import page.j5155.expressway.geometry.distanceTo
import page.j5155.expressway.geometry.times
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.math.PI

class PIDToPoint (
    private val pose: Supplier<Pose2d>,
    private val vel: Supplier<Vector2d>,
    private val target: Pose2d,
    private val powerUpdater: Consumer<PoseVelocity2d>,
    axialCoefs: PIDFController.PIDCoefficients,
    lateralCoefs: PIDFController.PIDCoefficients,
    headingCoefs: PIDFController.PIDCoefficients,
) : Action {

    private val xController = SquidController(axialCoefs)
    private val yController = SquidController(lateralCoefs)
    private val headingController = SquidController(headingCoefs)

    init {
        xController.targetPosition = target.position.x.toInt()
        yController.targetPosition = target.position.y.toInt()

        headingController.targetPosition = target.heading.toDouble().toInt()
        headingController.setOutputBounds(-PI, PI)
    }

    override fun run(p: TelemetryPacket): Boolean {
        val vel = vel.get()
        val pose = pose.get()

        if (pose.position.distanceTo(target.position) < 1) {
            powerUpdater.accept(PoseVelocity2d(Vector2d(0.0, 0.0), 0.0))
            return false
        }

        var inputVector =
            Vector2d(xController.update(pose.position.x), yController.update(pose.position.y))
        inputVector *= pose.heading.inverse()

        val inputVels =
            PoseVelocity2d(inputVector, headingController.update(pose.heading.toDouble()))

        powerUpdater.accept(inputVels)
        return true
    }
}