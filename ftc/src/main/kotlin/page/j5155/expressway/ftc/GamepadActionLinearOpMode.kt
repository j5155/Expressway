package page.j5155.expressway.ftc

/**
 * This is an OpMode wrapper that lets you use a GamepadActionRunner (and its associated features) without dispersing
 * casts throughout your code.
 */
abstract class GamepadActionLinearOpMode(): ActionLinearOpMode() {
    override val runner: GamepadActionRunner = GamepadActionRunner(gamepad1, gamepad2)
}