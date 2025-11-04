package cn.arorms.android.helloworld.ui.components

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.net.NetworkInterface
import java.util.Collections

fun getLocalIpAddress(context: Context): String {
    try {
        // 尝试通过WiFi获取IP地址
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        if (ipAddress != 0) {
            return Formatter.formatIpAddress(ipAddress)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    try {
        // 尝试通过网络接口获取IP地址
        val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in networkInterfaces) {
            val addresses = Collections.list(networkInterface.inetAddresses)
            for (address in addresses) {
                if (!address.isLoopbackAddress && address.hostAddress?.indexOf(':') == -1) {
                    return address.hostAddress ?: "无法获取IP地址"
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    return "无法获取IP地址"
}

@Composable
fun ConnectionSetup(
    isConnected: Boolean,
    onConnect: (String, Int) -> Unit,
    onDisconnect: () -> Unit
) {
    val context = LocalContext.current
    val localIpAddress = getLocalIpAddress(context)
    
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
                text = "设备连接设置",
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Blue
            )
            
            // 显示本地IP地址
            Text(
                text = "本地IP地址: $localIpAddress",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            var ipAddress by remember { mutableStateOf("192.168.0.158") }
            var port by remember { mutableStateOf("5678") }
            var connectionResult by remember { mutableStateOf<String?>(null) }
            
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("IP地址") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                isError = connectionResult?.startsWith("连接失败") == true,
                supportingText = {
                    if (connectionResult?.startsWith("连接失败") == true) {
                        Text(
                            text = connectionResult ?: "",
                            color = Color.Red
                        )
                    }
                }
            )
            
            OutlinedTextField(
                value = port,
                onValueChange = { port = it },
                label = { Text("端口号") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        connectionResult = null
                        val portNumber = port.toIntOrNull() ?: 8080
                        onConnect(ipAddress, portNumber)
                        connectionResult = "正在连接..."
                    },
                    enabled = !isConnected
                ) {
                    Text("连接设备")
                }
                
                Button(
                    onClick = {
                        onDisconnect()
                        connectionResult = "已断开连接"
                    },
                    enabled = isConnected,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text("断开连接")
                }
            }
            
            // 显示连接状态
            connectionResult?.let {
                Text(
                    text = it,
                    color = if (it == "连接成功" || it == "已断开连接") Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}