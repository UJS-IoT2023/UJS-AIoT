package cn.arorms.android.helloworld.model

/**
 * 系统状态数据模型
 */
data class SystemState(
    val isAutoMode: Boolean = false,
    val isManualMode: Boolean = true,
    val currentPosition: Position = Position(),
    val targetPosition: Position = Position(),
    val systemStatus: String = "待机",
    val alarmStatus: String = "正常"
)

/**
 * 位置信息数据模型
 */
data class Position(
    val pileGripperClosed: Boolean = false,
    val gantryRotationAngle: Float = 0f,
    val gantryHeight: Float = 0f,
    val beamGripperOpen: Boolean = true
)
