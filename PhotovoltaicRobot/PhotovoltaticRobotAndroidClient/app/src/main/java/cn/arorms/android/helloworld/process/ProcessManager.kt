package cn.arorms.android.helloworld.process

import cn.arorms.android.helloworld.communication.CommunicationManager
import cn.arorms.android.helloworld.model.Position
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 流程管理器，用于处理自动模式下的流程控制
 */
class ProcessManager(private val communicationManager: CommunicationManager) {
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isProcessRunning = false
    private var isProcessPaused = false
    
    /**
     * 开始自动安装流程
     */
    fun startInstallationProcess(onProgress: (String) -> Unit, onComplete: () -> Unit) {
        if (isProcessRunning) return
        
        isProcessRunning = true
        isProcessPaused = false
        
        coroutineScope.launch {
            onProgress("开始自动安装流程")
            
            // 步骤1: 抱桩夹爪夹紧
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤1: 抱桩夹爪夹紧")
                communicationManager.sendPositionControlCommand(
                    Position(pileGripperClosed = true)
                )
                delay(2000) // 等待2秒
            }
            
            // 步骤2: 门架旋转到指定角度
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤2: 门架旋转到指定角度")
                communicationManager.sendPositionControlCommand(
                    Position(pileGripperClosed = true, gantryRotationAngle = 90f)
                )
                delay(3000) // 等待3秒
            }
            
            // 步骤3: 门架下降到指定高度
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤3: 门架下降到指定高度")
                communicationManager.sendPositionControlCommand(
                    Position(
                        pileGripperClosed = true,
                        gantryRotationAngle = 90f,
                        gantryHeight = 50f
                    )
                )
                delay(2000) // 等待2秒
            }
            
            // 步骤4: 斜梁夹爪夹紧
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤4: 斜梁夹爪夹紧")
                communicationManager.sendPositionControlCommand(
                    Position(
                        pileGripperClosed = true,
                        gantryRotationAngle = 90f,
                        gantryHeight = 50f,
                        beamGripperOpen = false
                    )
                )
                delay(2000) // 等待2秒
            }
            
            // 步骤5: 门架上升
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤5: 门架上升")
                communicationManager.sendPositionControlCommand(
                    Position(
                        pileGripperClosed = true,
                        gantryRotationAngle = 90f,
                        gantryHeight = 100f,
                        beamGripperOpen = false
                    )
                )
                delay(2000) // 等待2秒
            }
            
            // 步骤6: 门架旋转回到原位
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤6: 门架旋转回到原位")
                communicationManager.sendPositionControlCommand(
                    Position(
                        pileGripperClosed = true,
                        gantryRotationAngle = 0f,
                        gantryHeight = 100f,
                        beamGripperOpen = false
                    )
                )
                delay(3000) // 等待3秒
            }
            
            // 步骤7: 斜梁夹爪松开
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤7: 斜梁夹爪松开")
                communicationManager.sendPositionControlCommand(
                    Position(
                        pileGripperClosed = true,
                        gantryRotationAngle = 0f,
                        gantryHeight = 100f,
                        beamGripperOpen = true
                    )
                )
                delay(2000) // 等待2秒
            }
            
            // 步骤8: 门架回到初始位置
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤8: 门架回到初始位置")
                communicationManager.sendPositionControlCommand(
                    Position()
                )
                delay(2000) // 等待2秒
            }
            
            // 步骤9: 抱桩夹爪松开
            if (!isProcessPaused && isProcessRunning) {
                onProgress("步骤9: 抱桩夹爪松开")
                communicationManager.sendPositionControlCommand(
                    Position(pileGripperClosed = false)
                )
                delay(2000) // 等待2秒
            }
            
            if (isProcessRunning) {
                onProgress("安装流程完成")
                isProcessRunning = false
                onComplete()
            }
        }
    }
    
    /**
     * 暂停流程
     */
    fun pauseProcess() {
        isProcessPaused = true
    }
    
    /**
     * 恢复流程
     */
    fun resumeProcess() {
        isProcessPaused = false
    }
    
    /**
     * 停止流程
     */
    fun stopProcess() {
        isProcessRunning = false
        isProcessPaused = false
    }
    
    /**
     * 检查流程是否正在运行
     */
    fun isRunning(): Boolean = isProcessRunning
    
    /**
     * 检查流程是否已暂停
     */
    fun isPaused(): Boolean = isProcessPaused
}
