import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.Action
import com.qualcomm.robotcore.hardware.Gamepad
import kotlin.math.abs

/**
 * This is a wrapper class to map gamepad buttons to actions upon various events.
 *
 * @param gamepad the Qualcomm gamepad to observe. It should be `gamepad1` or `gamepad2`, unless you're doing something funky.
 */
class GamepadEx(gamepad: Gamepad) {

    private var runningActions: List<Action> = mutableListOf()

    val a = Button("A") { gamepad.a }
    val b = Button("B") { gamepad.b }
    val x = Button("X") { gamepad.x }
    val y = Button("Y") { gamepad.y }

    val dpadUp = Button("DPad Up") { gamepad.dpad_up }
    val dpadDown = Button("DPad Down") { gamepad.dpad_down }
    val dpadLeft = Button("DPad Left") { gamepad.dpad_left }
    val dpadRight = Button("DPad Right") { gamepad.dpad_right }

    val leftBumper = Button("Left Bumper") { gamepad.left_bumper }
    val rightBumper = Button("Right Bumper") { gamepad.right_bumper }

    val leftTrigger = Trigger("Left Trigger") { gamepad.left_trigger }
    val rightTrigger = Trigger("Right Trigger") { gamepad.right_trigger }

    val leftStick = JoyStick("Left Stick", { gamepad.left_stick_x }, { gamepad.left_stick_y }, Button("Left Stick Button") { gamepad.left_stick_button })
    val rightStick = JoyStick("Right Stick", { gamepad.right_stick_x }, { gamepad.right_stick_y }, Button("Right Stick Button") { gamepad.right_stick_button })

    val controls = listOf(a, b, x, y, dpadUp, dpadDown, dpadLeft, dpadRight, leftBumper, rightBumper,
        leftTrigger, rightTrigger, leftStick, rightStick)

    val updateAction = UpdateGamepadAction()

    fun setTriggerThresholds(threshold: Float) {
        leftTrigger.threshold = threshold
        rightTrigger.threshold = threshold
    }

    fun setJoyStickThresholds(threshold: Float) {
        leftStick.threshold = threshold
        rightStick.threshold = threshold
    }

    /**
     * In order to map actions to gamepad buttons, this Action must be added. It handles updating the controls, and runs
     * the actions as needed. This class should not be directly instantiated by the user, instead use the `updateAction`
     * variable to reference the already-created instance of this class.
     */
    inner class UpdateGamepadAction: Action {
        override fun run(p: TelemetryPacket): Boolean {
            controls.forEach {
                it.update()
            }

            val newRunningActionList = mutableListOf<Action>()
            runningActions.forEach { action ->
                action.preview(p.fieldOverlay())
                if (action.run(p)) {
                    newRunningActionList.add(action)
                }
            }
            runningActions = newRunningActionList

            return true
        }
    }


    /**
     * Wrapper for an individual button on a gamepad.
     * @param name the (human-readable) name of the button
     * @param controlToWatch a lambda for the control to watch. This will be re-invoked every time update is called, so
     *          that it will automatically match the gamepad's value
     */
    inner class Button(private val name: String = "Unknown Button", private val controlToWatch: () -> Boolean): Control {
        /**
         * Whether this button is currently pushed down
         */
        var down = false

        /**
         * Whether this button was pressed between the last call to update and this one
         */
        var justPressed = false

        /**
         * Whether this button was released between the last call to update and this one
         */
        var justReleased = false

        /**
         * Map an action to the onPress event. It will be run once the button is pressed
         */
        var onPressActionMap: Action? = null

        /**
         * Map an action to the onRelease event. It will be run once the button is released
         */
        var onReleaseActionMap: Action? = null

        /**
         * This one is a little bit special. It runs the action EVERY TIME the button is pressed (and update gets
         * called).
         */
        var heldActionMap: Action? = null

        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        override fun update() {
            justPressed = controlToWatch.invoke() && !down
            justReleased = !controlToWatch.invoke() && down
            down = controlToWatch.invoke()

            // If there are mapped actions, request to run them
            if (justPressed && onPressActionMap != null) {
                runningActions += onPressActionMap!!
            }
            if (justReleased && onReleaseActionMap != null) {
                runningActions += onReleaseActionMap!!
            }

            if (down && heldActionMap != null) {
                // We only want to add one instance of the heldAction at a time.
                if (!runningActions.contains(heldActionMap!!)) {
                    runningActions += heldActionMap!!
                }
            }
        }

        override fun toString(): String {
            val set = mutableSetOf<String>()
            if (down) set.add("Down")
            if (justPressed) set.add("Just Pressed")
            if (justReleased) set.add("Just Released")
            return if (set.isNotEmpty()) "$name: ${set.joinToString(", ")}" else ""
        }
    }

    /**
     * Wrapper for an individual trigger on a gamepad.
     * @param name the (human-readable) name of the trigger
     * @param controlToWatch a lambda for the control to watch. This will be re-invoked every time update is called, so
     *          that it will automatically match the gamepad's value
     */
    inner class Trigger(private val name: String = "Unknown Trigger", private val controlToWatch: () -> Float): Control {
        /**
         * How far off of default the trigger should move before it is considered "down"
         */
        var threshold: Float = 0f

        /**
         * Whether this button is currently pushed down
         */
        val down: Boolean
            get() = amount > threshold

        /**
         * Whether this button was pressed between the last call to update and this one
         */
        var justPressed = false

        /**
         * Whether this button was released between the last call to update and this one
         */
        var justReleased = false

        /**
         * The amount the trigger is pressed down
         */
        var amount = 0f

        /**
         * Map an action to the onPress event. It will be run once the button is pressed
         */
        var onPressActionMap: Action? = null

        /**
         * Map an action to the onRelease event. It will be run once the button is released
         */
        var onReleaseActionMap: Action? = null

        /**
         * This one is a little bit special. It runs the action EVERY TIME the button is pressed (and update gets
         * called).
         */
        var heldActionMap: Action? = null

        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        override fun update() {
            justPressed = controlToWatch.invoke() > threshold && !down
            justReleased = controlToWatch.invoke() <= threshold && down
            amount = controlToWatch.invoke()

            // If there are mapped actions, request to run them
            if (justPressed && onPressActionMap != null) {
                runningActions += onPressActionMap!!
            }
            if (justReleased && onReleaseActionMap != null) {
                runningActions += onReleaseActionMap!!
            }

            if (down && heldActionMap != null) {
                // We only want to add one instance of the heldAction at a time.
                if (!runningActions.contains(heldActionMap!!)) {
                    runningActions += heldActionMap!!
                }
            }
        }

        /**
         * Returns a string showing whether the trigger was just pressed, was just released, and/or
         * how much it's being pressed down.
         * @return whether the trigger was just pressed, was just released, and/or how much it's
         * being pressed down.
         */
        override fun toString(): String {
            val set = mutableSetOf<String>()
            if (justPressed) set.add("Just Pressed")
            if (justReleased) set.add("Just Released")
            if (amount != 0.0f) set.add("Amount Pressed: $amount")
            return if (set.isNotEmpty()) "$name: ${set.joinToString(", ")}" else ""
        }
    }

    /**
     * Wrapper for an individual joystick on a gamepad
     * @param name the (human-readable) name of the joystick
     * @param xToWatch the x value to watch
     * @param yToWatch the y value to watch
     * @param button the button to watch
     */
    inner class JoyStick(private val name: String, private val xToWatch: () -> Float, private val yToWatch: () -> Float, private val button: Button): Control {
        /**
         * How far off center the joystick should move before it is considered "down"
         */
        var threshold: Float = 0f

        /**
         * Whether the joystick is off of its center position
         */
        val moved: Boolean
            get() = abs(x) > threshold || abs(y) > threshold

        /**
         * Whether the joystick just moved off of its center position
         */
        var justMoved = false

        /**
         * Whether the joystick just returned to its center position
         */
        var justCentered = false

        /**
         * The x-value of the joystick
         */
        var x = 0.0f

        /**
         * The y-value of the joystick
         */
        var y = 0.0f

        /**
         * Map an action to run when the joystick first moves off center
         */
        var onMoveActionMap: Action? = null

        /**
         * Map an action to run when the joystick first moves back to the center position
         */
        var onCenterActionMap: Action? = null

        /**
         * Map an action to run whenever the joystick is off of the center position
         */
        var offCenterActionMap: Action? = null

        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        override fun update() {
            button.update()

            justMoved = (abs(xToWatch.invoke()) > threshold || abs(yToWatch.invoke()) > threshold) && !moved
            justCentered = (abs(xToWatch.invoke()) <= threshold && abs(yToWatch.invoke()) <= threshold) && moved

            x = xToWatch.invoke()
            y = yToWatch.invoke()

            // If there are mapped actions, request to run them
            if (justMoved && onMoveActionMap != null) {
                runningActions += onMoveActionMap!!
            }
            if (justCentered && onCenterActionMap != null) {
                runningActions += onCenterActionMap!!
            }

            if (moved && offCenterActionMap != null) {
                // We only want to add one instance of the heldAction at a time.
                if (!runningActions.contains(offCenterActionMap!!)) {
                    runningActions += offCenterActionMap!!
                }
            }
        }

        /**
         * Returns a string showing whether the button was just pressed, was just released, and/or
         * how much it's being pressed down, as well as whether the joystick just started or
         * stopped moving, and its x and y coordinates (if they aren't 0).
         * @return information about the joystick's current state
         */
        override fun toString(): String {
            val set = mutableSetOf<String>()
            if (button.down) set.add("Button Down")
            if (button.justPressed) set.add("Button Just Pressed")
            if (button.justReleased) set.add("Button Just Released")
            if (justMoved) set.add("Just Moved Off-Center")
            if (justCentered) set.add("Just Moved Back to Center")
            if (set.isNotEmpty() || x != 0.0f || y != 0.0f) {
                set.add("X: $x")
                set.add("Y: $y")
            }
            return if (set.isNotEmpty()) "$name: ${set.joinToString(", ")}" else ""
        }
    }

    interface Control {
        /**
         * Updates the various values, and adds mapped actions if applicable
         */
        fun update()
    }

}