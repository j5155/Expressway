@file:JvmName("GeometryHelpers")
package page.j5155.expressway.core.geometry

import com.acmerobotics.roadrunner.Pose2d
import com.acmerobotics.roadrunner.Rotation2d
import com.acmerobotics.roadrunner.Vector2d

fun Rotation2d.approxEqual(other: Rotation2d, epsilon: Double = 1e-15) = this.toDouble() - other.toDouble() < epsilon

fun Vector2d.distanceTo(other: Vector2d): Double = (this - other).norm()

fun Vector2d.approxEqual(other: Vector2d, epsilon: Double = 1e-15) =
    this.distanceTo(other) < epsilon

operator fun Vector2d.times(rotation: Rotation2d) = rotation * this

fun Vector2d.rotateBy(rotation: Rotation2d) = this * rotation
fun Vector2d.rotateBy(rotationRadians: Double) = this * Rotation2d.exp(rotationRadians)


fun Pose2d.distanceTo(other: Pose2d) = this.position.distanceTo(other.position)

fun Pose2d.approxEqual(other: Pose2d, epsilon: Double = 1e-15) =
    this.position.approxEqual(other.position, epsilon) && this.heading.approxEqual(other.heading, epsilon)

operator fun Pose2d.times(rotation: Rotation2d) = Pose2d(this.position * rotation, this.heading * rotation)

fun Pose2d.rotateBy(rotation: Rotation2d) = this * rotation
fun Pose2d.rotateBy(rotationRadians: Double) = this * Rotation2d.exp(rotationRadians)


