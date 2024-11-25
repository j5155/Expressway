import com.qualcomm.robotcore.eventloop.opmode.OpMode

abstract class ActionOpMode : OpMode() {
    protected val actionRunner = ActionRunner()
}