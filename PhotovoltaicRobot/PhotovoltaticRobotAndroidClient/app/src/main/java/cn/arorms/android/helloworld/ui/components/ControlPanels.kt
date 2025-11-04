package cn.arorms.android.helloworld.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.arorms.android.helloworld.model.Position

@Composable
fun PositionControlPanel(
    currentPosition: Position,
    onPileGripperControl: (Boolean) -> Unit,
    onGantryRotationChange: (Float) -> Unit,
    onGantryHeightChange: (Float) -> Unit,
    onBeamGripperControl: (Boolean) -> Unit
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
                text = "定位机构控制",
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Blue
            )
            
            // 抱桩夹爪控制
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("抱桩夹爪:")
                Row {
                    Button(
                        onClick = { onPileGripperControl(true) },
                        enabled = !currentPosition.pileGripperClosed
                    ) {
                        Text("夹紧")
                    }
                    Button(
                        onClick = { onPileGripperControl(false) },
                        enabled = currentPosition.pileGripperClosed,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("松开")
                    }
                }
            }
            
            // 门架旋转控制
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("门架旋转:")
                var rotationAngle by remember { mutableStateOf(currentPosition.gantryRotationAngle) }
                Column {
                    Slider(
                        value = rotationAngle,
                        onValueChange = {
                            rotationAngle = it
                            onGantryRotationChange(it)
                        },
                        valueRange = 0f..360f,
                        modifier = Modifier.width(200.dp)
                    )
                    Text("角度: ${String.format("%.1f", rotationAngle)}°")
                }
            }
            
            // 门架升降控制
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("门架升降:")
                var gantryHeight by remember { mutableStateOf(currentPosition.gantryHeight) }
                Column {
                    Slider(
                        value = gantryHeight,
                        onValueChange = {
                            gantryHeight = it
                            onGantryHeightChange(it)
                        },
                        valueRange = 0f..200f,
                        modifier = Modifier.width(200.dp)
                    )
                    Text("高度: ${String.format("%.1f", gantryHeight)}cm")
                }
            }
            
            // 斜梁夹爪控制
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("斜梁夹爪:")
                Row {
                    Button(
                        onClick = { onBeamGripperControl(false) },
                        enabled = currentPosition.beamGripperOpen
                    ) {
                        Text("夹紧")
                    }
                    Button(
                        onClick = { onBeamGripperControl(true) },
                        enabled = !currentPosition.beamGripperOpen,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("松开")
                    }
                }
            }
        }
    }
}
