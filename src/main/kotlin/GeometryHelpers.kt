package page.j5155.actionAdditions

import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Rotation2d
import com.acmerobotics.roadrunner.Vector2d

fun Vector2d.distanceTo(other: Vector2d): Double = (this - other).norm()

operator fun Vector2d.times(rotation: Rotation2d) = rotation * this

fun Vector2d.rotateBy(rotation: Rotation2d) = this * rotation
fun Vector2d.rotateBy(rotationRadians: Double) = this * Rotation2d.exp(rotationRadians)


fun Pose2d.distanceTo(other: Pose2d) = this.position.distanceTo(other.position)

operator fun Pose2d.times(rotation: Rotation2d) = Pose2d(this.position * rotation, this.heading * rotation)

fun Pose2d.rotateBy(rotation: Rotation2d) = this * rotation
fun Pose2d.rotateBy(rotationRadians: Double) = this * Rotation2d.exp(rotationRadians)


