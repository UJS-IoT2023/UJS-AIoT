package cn.arorms.android.helloworld.model

/**
 * 传感器数据模型
 */
data class SensorData(
    val temperature: Float = 25.0f,
    val humidity: Float = 50.0f,
    val ultrasonicDistance: Float = 100.0f,
    val pressure: Float = 101.3f,
    val voltage: Float = 12.0f,
    val current: Float = 2.5f
)
