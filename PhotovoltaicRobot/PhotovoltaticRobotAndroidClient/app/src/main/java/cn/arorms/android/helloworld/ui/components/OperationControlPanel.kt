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
fun OperationControlPanel(
    onOperationCommand: (String) -> Unit,
    onSendHelloWorld: () -> Unit
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
                text = "系统操作机构控制",
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Blue
            )
            
            // 第一行按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onOperationCommand("START") }) {
                    Text("启动")
                }
                Button(onClick = { onOperationCommand("STOP") }) {
                    Text("停止")
                }
                Button(onClick = { onOperationCommand("RESET") }) {
                    Text("复位")
                }
            }
            
            // 第二行按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onOperationCommand("LIFT_UP") }) {
                    Text("顶升气缸上升")
                }
                Button(onClick = { onOperationCommand("LIFT_DOWN") }) {
                    Text("顶升气缸下降")
                }
            }
            
            // 第三行按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onOperationCommand("MAIN_UP") }) {
                    Text("主压气缸上升")
                }
                Button(onClick = { onOperationCommand("MAIN_DOWN") }) {
                    Text("主压气缸下降")
                }
            }
            
            // 第四行按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onOperationCommand("ASSIST_UP") }) {
                    Text("辅压气缸上升")
                }
                Button(onClick = { onOperationCommand("ASSIST_DOWN") }) {
                    Text("辅压气缸下降")
                }
            }
            
            // 添加发送helloworld消息的按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onSendHelloWorld) {
                    Text("发送HelloWorld消息")
                }
            }
        }
    }
}
