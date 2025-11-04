package cn.arorms.android.helloworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.arorms.android.helloworld.ui.components.ConnectionSetup
import cn.arorms.android.helloworld.ui.components.OperationControlPanel
import cn.arorms.android.helloworld.ui.components.PositionControlPanel
import cn.arorms.android.helloworld.ui.components.ProcessControl
import cn.arorms.android.helloworld.ui.components.SensorDataDisplay
import cn.arorms.android.helloworld.ui.components.StatusDisplay
import cn.arorms.android.helloworld.ui.theme.HelloWorldTheme
import cn.arorms.android.helloworld.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloWorldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainViewModel = viewModel()) {
    val systemState by viewModel.systemState.collectAsState()
    val sensorData by viewModel.sensorData.collectAsState()
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 应用标题
        Text(
            text = "光伏自动安装辅助系统",
            modifier = Modifier.padding(16.dp)
        )
        
        // 设备连接设置
        ConnectionSetup(
            isConnected = systemState.systemStatus == "已连接",
            onConnect = { ipAddress, port ->
                viewModel.connectToDevice(ipAddress, port)
            },
            onDisconnect = {
                viewModel.disconnectFromDevice()
            }
        )
        
        // 系统状态和报警信息显示
        StatusDisplay(systemState = systemState)
        
        // 流程管理
        ProcessControl(
            isAutoMode = systemState.isAutoMode,
            onModeChange = { isAuto ->
                if (isAuto) {
                    viewModel.switchToAutoMode()
                } else {
                    viewModel.switchToManualMode()
                }
            },
            onStartProcess = { viewModel.startInstallationProcess() },
            onPauseProcess = { viewModel.pauseInstallationProcess() },
            onResumeProcess = { viewModel.resumeInstallationProcess() },
            onStopProcess = { viewModel.stopInstallationProcess() }
        )
        
        // 定位机构控制
        PositionControlPanel(
            currentPosition = systemState.currentPosition,
            onPileGripperControl = { close ->
                viewModel.controlPileGripper(close)
            },
            onGantryRotationChange = { angle ->
                viewModel.setGantryRotationAngle(angle)
            },
            onGantryHeightChange = { height ->
                viewModel.setGantryHeight(height)
            },
            onBeamGripperControl = { open ->
                viewModel.controlBeamGripper(open)
            }
        )
        
        // 系统操作机构控制
        OperationControlPanel(
            onOperationCommand = { command ->
                viewModel.sendOperationCommand(command)
            },
            onSendHelloWorld = {
                viewModel.sendHelloWorldMessage()
            }
        )
        
        // 传感器数据展示
        SensorDataDisplay(sensorData = sensorData)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HelloWorldTheme {
        MainScreen()
    }
}
