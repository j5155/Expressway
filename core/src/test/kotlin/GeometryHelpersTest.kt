import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import page.j5155.actionAdditions.distanceTo
import page.j5155.actionAdditions.rotateBy
import kotlin.test.Test
import kotlin.test.assertEquals

class GeometryHelpersTest {
    @Test
    fun vectorDistanceTo() {
        val vector1 = Vector2d(0.0, 0.0)
        val vector2 = Vector2d(5.0, 0.0)
        assertEquals(vector1.distanceTo(vector2), 5.0)
    }
    @Test
    fun vectorRotateBy() { // relies on rotateBy, times, and distanceTo passing
        val startVector = Vector2d(5.0,0.0)
        val rotationAmount = Math.toRadians(90.0)

        val rotatedVector = startVector.rotateBy(rotationAmount)
        val targetVector = Vector2d(0.0,5.0)
        assert(rotatedVector.distanceTo(targetVector) < 1e-15) // math imprecision
    }
    @Test
    fun poseDistanceTo() {
        val pose1 = Pose2d(0.0, 0.0, 0.0)
        val pose2 = Pose2d(5.0, 0.0, 0.0)

        assertEquals(pose1.distanceTo(pose2), 5.0)
    }
    @Test
    fun poseRotateBy() {
        val startPose = Pose2d(5.0,0.0,0.0)
        val rotationAmount = Math.toRadians(90.0)

        val rotatedPose = startPose.rotateBy(rotationAmount)
        val targetPose = Pose2d(0.0,5.0,Math.toRadians(90.0))
        assert(rotatedPose.distanceTo(targetPose) < 1e-15 && rotatedPose.heading - targetPose.heading < 1e-15)
    }

}