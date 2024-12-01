package page.j5155.expressway.ftc

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * This is an ActionRunner that runs actions automatically when certain buttons are pressed on the gamepads.
 */
class GamepadActionRunner(gamepad1: Gamepad, gamepad2: Gamepad): UniqueActionRunner() {
    /**
     * A GamepadEx reference used for mapping actions to buttons/joysticks/triggers
     */
    val gamepad1 = GamepadEx(gamepad1)

    /**
     * A GamepadEx reference used for mapping actions to buttons/joysticks/triggers
     */
    val gamepad2 = GamepadEx(gamepad2)

    /**
     * This function should be called every loop, as it handles updating the gamepads and running their mapped actions
     */
    fun update() {
        gamepad1.update().forEach {
            runAsync(it)
        }
        gamepad2.update().forEach {
            runAsync(it)
        }
    }
}