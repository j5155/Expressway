import com.acmerobotics.dashboard.FtcDashboard

/**
 * ActionRunner allows for running actions asynchronously during an OpMode.
 * This just wraps NoPreviewActionRunner from core and uses FtcDashboard for preview.
 */
open class ActionRunner: NoPreviewActionRunner(FtcDashboard.getInstance()::sendTelemetryPacket)