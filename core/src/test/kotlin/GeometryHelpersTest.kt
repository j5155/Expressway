import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Vector2d
import page.j5155.expressway.core.geometry.approxEqual
import page.j5155.expressway.core.geometry.distanceTo
import page.j5155.expressway.core.geometry.rotateBy
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
    fun vectorApproxEqual() {
        val vector1 = Vector2d(0.0, 0.0)
        val vector2 = Vector2d(1e-20, 0.0)
        assert(vector1 != vector2)
        assert(vector1.approxEqual(vector2))
    }
    @Test
    fun vectorRotateBy() { // relies on rotateBy, times, and distanceTo passing
        val startVector = Vector2d(5.0,0.0)
        val rotationAmount = Math.toRadians(90.0)

        val rotatedVector = startVector.rotateBy(rotationAmount)
        val targetVector = Vector2d(0.0,5.0)
        assert(rotatedVector.approxEqual(targetVector))
    }
    @Test
    fun poseDistanceTo() {
        val pose1 = Pose2d(0.0, 0.0, 0.0)
        val pose2 = Pose2d(5.0, 0.0, 0.0)

        assertEquals(pose1.distanceTo(pose2), 5.0)
    }
    @Test
    fun poseApproxEqual() {
        val pose1 = Pose2d(0.0,0.0,0.0)
        val pose2 = Pose2d(1e-20,0.0,1e-20)
        assert(pose1 != pose2)
        assert(pose1.approxEqual(pose2))
    }
    @Test
    fun poseRotateBy() {
        val startPose = Pose2d(5.0,0.0,0.0)
        val rotationAmount = Math.toRadians(90.0)

        val rotatedPose = startPose.rotateBy(rotationAmount)
        val targetPose = Pose2d(0.0,5.0,Math.toRadians(90.0))
        assert(rotatedPose.approxEqual(targetPose))
    }

}