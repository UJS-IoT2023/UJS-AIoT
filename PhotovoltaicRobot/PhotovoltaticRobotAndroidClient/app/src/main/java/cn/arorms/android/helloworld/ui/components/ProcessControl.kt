package cn.arorms.android.helloworld.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProcessControl(
    isAutoMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    onStartProcess: () -> Unit,
    onPauseProcess: () -> Unit,
    onResumeProcess: () -> Unit,
    onStopProcess: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "流程管理",
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Blue
            )
            
            // 模式切换
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("运行模式:")
                Row {
                    Button(
                        onClick = { onModeChange(false) },
                        enabled = isAutoMode
                    ) {
                        Text("手动模式")
                    }
                    Button(
                        onClick = { onModeChange(true) },
                        enabled = !isAutoMode,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("自动模式")
                    }
                }
            }
            
            // 流程控制按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = onStartProcess) {
                    Text("开始")
                }
                Button(onClick = onPauseProcess) {
                    Text("暂停")
                }
                Button(onClick = onResumeProcess) {
                    Text("恢复")
                }
                Button(onClick = onStopProcess) {
                    Text("停止")
                }
            }
        }
    }
}
