@file:JvmName("HardwareDependencies")
package page.j5155.expressway.ftc.actions

import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.Servo
import page.j5155.expressway.core.actions.Dependency

class MotorDependency(private val motor: DcMotorEx) : Dependency, DcMotorEx by motor {
    override var inUse: Boolean = false
}

class ServoDependency(private val servo: Servo) : Dependency, Servo by servo {
    override var inUse: Boolean = false
}

class CRServoDependency(private val crServo: CRServo) : Dependency, CRServo by crServo {
    override var inUse: Boolean = false
}