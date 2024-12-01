package page.j5155.expressway.ftc.actions

import com.acmerobotics.dashboard.FtcDashboard
import page.j5155.expressway.core.actions.NoPreviewActionRunner

// should this exist? should this wrapping happen only in OpModes?
/**
 * ActionRunner allows for running actions asynchronously during an OpMode.
 * This just wraps NoPreviewActionRunner from core and uses FtcDashboard for preview.
 */
open class ActionRunner: NoPreviewActionRunner(FtcDashboard.getInstance()::sendTelemetryPacket)
