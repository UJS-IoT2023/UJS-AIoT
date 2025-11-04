package cn.arorms.android.helloworld.communication

import cn.arorms.android.helloworld.model.Position
import cn.arorms.android.helloworld.model.SensorData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.InetSocketAddress
import java.net.SocketTimeoutException

/**
 * 与Hi3861开发套件的TCP通信管理器
 */
class CommunicationManager {
    companion object {
        private var instance: CommunicationManager? = null
        
        fun getInstance(): CommunicationManager {
            if (instance == null) {
                instance = CommunicationManager()
            }
            return instance!!
        }
    }
    
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var isConnected = false
    private val CONNECTION_TIMEOUT = 5000 // 5秒连接超时
    
    /**
     * 通过TCP连接到设备
     */
    fun connectToDevice(ipAddress: String, port: Int): Boolean {
        return try {
            // 关闭之前的连接（如果存在）
            disconnectFromDevice()
            
            // 创建带超时设置的Socket连接
            socket = Socket()
            socket?.connect(InetSocketAddress(ipAddress, port), CONNECTION_TIMEOUT)
            
            writer = PrintWriter(socket!!.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            isConnected = true
            true
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            println("连接超时: 无法连接到 $ipAddress:$port")
            isConnected = false
            false
        } catch (e: Exception) {
            e.printStackTrace()
            println("连接失败: ${e.message}")
            isConnected = false
            false
        }
    }
    
    /**
     * 断开设备连接
     */
    fun disconnectFromDevice() {
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader = null
            writer = null
            socket = null
            isConnected = false
        }
    }
    
    /**
     * 发送自定义TCP消息
     */
    fun sendCustomTcpMessage(message: String): Boolean {
        if (!isConnected) {
            println("发送消息失败: 未连接到设备")
            return false
        }
        
        return try {
            writer?.println(message)
            writer?.flush()
            
            if (writer?.checkError() == true) {
                println("发送消息时发生错误")
                false
            } else {
                println("成功发送消息: $message")
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("发送消息失败: ${e.message}")
            false
        }
    }
    
    /**
     * 发送定位机构控制命令
     */
    fun sendPositionControlCommand(position: Position): Boolean {
        if (!isConnected) return false
        
        return try {
            val command = "POS:${position.pileGripperClosed},${position.gantryRotationAngle},${position.gantryHeight},${position.beamGripperOpen}"
            writer?.println(command)
            writer?.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 发送系统操作机构控制命令
     */
    fun sendOperationControlCommand(command: String): Boolean {
        if (!isConnected) return false
        
        return try {
            writer?.println("CMD:$command")
            writer?.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 发送helloworld消息
     */
    fun sendHelloWorldMessage(): Boolean {
        if (!isConnected) return false
        
        return try {
            writer?.println("helloworld")
            writer?.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取传感器数据流
     */
    fun getSensorDataStream(): Flow<SensorData> = flow {
        if (!isConnected) {
            // 如果未连接，则模拟发送一些数据
            while (true) {
                emit(SensorData(
                    temperature = (20..30).random().toFloat(),
                    humidity = (40..60).random().toFloat(),
                    ultrasonicDistance = (50..150).random().toFloat(),
                    pressure = (100..105).random().toFloat(),
                    voltage = (11..13).random().toFloat(),
                    current = (1..5).random().toFloat()
                ))
                kotlinx.coroutines.delay(1000) // 每秒更新一次
            }
        } else {
            // 从TCP服务器接收传感器数据
            try {
                while (isConnected) {
                    val line = reader?.readLine()
                    if (line != null) {
                        // 解析接收到的数据
                        // 假设数据格式为: "TEMP:value,HUM:value,ULTRA:value,PRES:value,VOLT:value,CURR:value"
                        val dataMap = mutableMapOf<String, Float>()
                        line.split(",").forEach { pair ->
                            val parts = pair.split(":")
                            if (parts.size == 2) {
                                dataMap[parts[0]] = parts[1].toFloatOrNull() ?: 0f
                            }
                        }
                        
                        emit(SensorData(
                            temperature = dataMap["TEMP"] ?: 25.0f,
                            humidity = dataMap["HUM"] ?: 50.0f,
                            ultrasonicDistance = dataMap["ULTRA"] ?: 100.0f,
                            pressure = dataMap["PRES"] ?: 101.3f,
                            voltage = dataMap["VOLT"] ?: 12.0f,
                            current = dataMap["CURR"] ?: 2.5f
                        ))
                    }
                    kotlinx.coroutines.delay(100) // 每100毫秒检查一次新数据
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("接收数据时发生错误: ${e.message}")
                // 如果发生错误，回退到模拟数据
                while (true) {
                    emit(SensorData(
                        temperature = (20..30).random().toFloat(),
                        humidity = (40..60).random().toFloat(),
                        ultrasonicDistance = (50..150).random().toFloat(),
                        pressure = (100..105).random().toFloat(),
                        voltage = (11..13).random().toFloat(),
                        current = (1..5).random().toFloat()
                    ))
                    kotlinx.coroutines.delay(1000) // 每秒更新一次
                }
            }
        }
    }
}