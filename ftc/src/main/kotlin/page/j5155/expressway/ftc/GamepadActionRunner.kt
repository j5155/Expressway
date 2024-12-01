package page.j5155.expressway.ftc

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * This is an ActionRunner that runs actions automatically when certain buttons are pressed on the gamepads.
 */
class GamepadActionRunner(qualcommGamepad1: Gamepad, qualcommGamepad2: Gamepad): UniqueActionRunner() {
    /**
     * A page.j5155.expressway.ftc.GamepadEx reference used for mapping actions to buttons/joysticks/triggers
     */
    val gamepad1 = GamepadEx(qualcommGamepad1)

    /**
     * A page.j5155.expressway.ftc.GamepadEx reference used for mapping actions to buttons/joysticks/triggers
     */
    val gamepad2 = GamepadEx(qualcommGamepad2)

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