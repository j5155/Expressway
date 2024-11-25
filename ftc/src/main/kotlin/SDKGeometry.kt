import com.acmerobotics.roadrunner.Pose2d
import com.qualcomm.hardware.sparkfun.SparkFunOTOS
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D

// SDK Pose
val Pose2D.rr
    get() = Pose2d(this.xInch, this.yInch, this.hRad)

val Pose2D.xInch: Double
    get() = this.getX(DistanceUnit.INCH)

val Pose2D.yInch: Double
    get() = this.getY(DistanceUnit.INCH)

/** Heading in radians **/
val Pose2D.hRad: Double
    get() = this.getHeading(AngleUnit.RADIANS)


// RR Pose
val Pose2d.sdk
    get() = Pose2D(
        DistanceUnit.INCH,
        this.position.x,
        this.position.y,
        AngleUnit.RADIANS,
        this.heading.toDouble()
    )

/**
 * Convert Road Runner Pose to OTOS Pose
 * Note: returned OTOS Pose will be in inches and radians
 */
val Pose2d.otos: SparkFunOTOS.Pose2D
    get() = SparkFunOTOS.Pose2D(this.position.x, this.position.y, this.heading.toDouble())

// OTOS Pose
/**
 * Convert OTOS Pose to Roadrunner Pose
 * Note: OTOS pose MUST be in inches and radians for this to work
 */
val SparkFunOTOS.Pose2D.rr: Pose2d
    get() {
        assert(this.h < 2 * Math.PI) { "OTOS pose heading greater than 2 pi (it's ${this.h}); is it in radians?" }
        return Pose2d(this.x, this.y, this.h)
    }
