package cn.arorms.android.helloworld.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.arorms.android.helloworld.communication.CommunicationManager
import cn.arorms.android.helloworld.model.Position
import cn.arorms.android.helloworld.model.SensorData
import cn.arorms.android.helloworld.model.SystemState
import cn.arorms.android.helloworld.process.ProcessManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 主界面ViewModel，用于管理UI状态
 */
class MainViewModel : ViewModel() {
    private val _systemState = MutableStateFlow(SystemState())
    val systemState: StateFlow<SystemState> = _systemState
    
    private val _sensorData = MutableStateFlow(SensorData())
    val sensorData: StateFlow<SensorData> = _sensorData
    
    private val communicationManager = CommunicationManager.getInstance()
    private val processManager = ProcessManager(communicationManager)
    
    init {
        // 启动传感器数据监听
        viewModelScope.launch {
            communicationManager.getSensorDataStream().collect { data ->
                _sensorData.value = data
            }
        }
    }
    
    /**
     * 切换到自动模式
     */
    fun switchToAutoMode() {
        _systemState.value = _systemState.value.copy(
            isAutoMode = true,
            isManualMode = false,
            systemStatus = "自动模式"
        )
    }
    
    /**
     * 切换到手动模式
     */
    fun switchToManualMode() {
        _systemState.value = _systemState.value.copy(
            isAutoMode = false,
            isManualMode = true,
            systemStatus = "手动模式"
        )
    }
    
    /**
     * 控制抱桩夹爪
     */
    fun controlPileGripper(close: Boolean) {
        val newPosition = _systemState.value.currentPosition.copy(pileGripperClosed = close)
        _systemState.value = _systemState.value.copy(
            currentPosition = newPosition
        )
        
        // 发送控制命令到设备
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.sendPositionControlCommand(newPosition)
            }
        }
    }
    
    /**
     * 设置门架旋转角度
     */
    fun setGantryRotationAngle(angle: Float) {
        val newPosition = _systemState.value.currentPosition.copy(gantryRotationAngle = angle)
        _systemState.value = _systemState.value.copy(
            currentPosition = newPosition
        )
        
        // 发送控制命令到设备
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.sendPositionControlCommand(newPosition)
            }
        }
    }
    
    /**
     * 设置门架高度
     */
    fun setGantryHeight(height: Float) {
        val newPosition = _systemState.value.currentPosition.copy(gantryHeight = height)
        _systemState.value = _systemState.value.copy(
            currentPosition = newPosition
        )
        
        // 发送控制命令到设备
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.sendPositionControlCommand(newPosition)
            }
        }
    }
    
    /**
     * 控制斜梁夹爪
     */
    fun controlBeamGripper(open: Boolean) {
        val newPosition = _systemState.value.currentPosition.copy(beamGripperOpen = open)
        _systemState.value = _systemState.value.copy(
            currentPosition = newPosition
        )
        
        // 发送控制命令到设备
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.sendPositionControlCommand(newPosition)
            }
        }
    }
    
    /**
     * 发送系统操作命令
     */
    fun sendOperationCommand(command: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.sendOperationControlCommand(command)
            }
        }
    }
    
    /**
     * 发送helloworld消息
     */
    fun sendHelloWorldMessage() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.sendHelloWorldMessage()
            }
        }
    }
    
    /**
     * 发送自定义TCP消息
     */
    fun sendCustomTcpMessage(message: String): Boolean {
        return try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    communicationManager.sendCustomTcpMessage(message)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 连接到设备
     */
    fun connectToDevice(ipAddress: String, port: Int): Boolean {
        var result = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                result = communicationManager.connectToDevice(ipAddress, port)
                withContext(Dispatchers.Main) {
                    if (result) {
                        _systemState.value = _systemState.value.copy(
                            systemStatus = "已连接"
                        )
                    } else {
                        _systemState.value = _systemState.value.copy(
                            systemStatus = "连接失败",
                            alarmStatus = "通信错误"
                        )
                    }
                }
            }
        }
        return result
    }
    
    /**
     * 断开设备连接
     */
    fun disconnectFromDevice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                communicationManager.disconnectFromDevice()
                withContext(Dispatchers.Main) {
                    _systemState.value = _systemState.value.copy(
                        systemStatus = "已断开",
                        alarmStatus = "正常"
                    )
                }
            }
        }
    }
    
    /**
     * 开始安装流程
     */
    fun startInstallationProcess() {
        if (_systemState.value.isAutoMode) {
            processManager.startInstallationProcess(
                onProgress = { progress ->
                    _systemState.value = _systemState.value.copy(
                        systemStatus = progress
                    )
                },
                onComplete = {
                    _systemState.value = _systemState.value.copy(
                        systemStatus = "安装完成"
                    )
                }
            )
        }
    }
    
    /**
     * 暂停安装流程
     */
    fun pauseInstallationProcess() {
        if (_systemState.value.isAutoMode) {
            processManager.pauseProcess()
            _systemState.value = _systemState.value.copy(
                systemStatus = "流程已暂停"
            )
        }
    }
    
    /**
     * 恢复安装流程
     */
    fun resumeInstallationProcess() {
        if (_systemState.value.isAutoMode) {
            processManager.resumeProcess()
            _systemState.value = _systemState.value.copy(
                systemStatus = "流程已恢复"
            )
        }
    }
    
    /**
     * 停止安装流程
     */
    fun stopInstallationProcess() {
        if (_systemState.value.isAutoMode) {
            processManager.stopProcess()
            _systemState.value = _systemState.value.copy(
                systemStatus = "流程已停止"
            )
        }
    }
}