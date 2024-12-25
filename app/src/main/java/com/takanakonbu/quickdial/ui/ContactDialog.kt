package com.takanakonbu.quickdial.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.takanakonbu.quickdial.model.Contact

@Composable
fun ContactDialog(
    contact: Contact?,
    position: Int,
    onDismiss: () -> Unit,
    onSave: (Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(contact?.name ?: "") }
    var phoneNumber by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (contact == null) "新しい連絡先を追加" else "連絡先を編集",
                    fontSize = 24.sp
                )

                // 名前入力フィールド
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        showError = false
                    },
                    label = { Text("お名前", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    singleLine = true,
                    isError = showError && name.isBlank()
                )

                // 電話番号入力フィールド
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        // 数字とハイフンのみ許可
                        if (input.all { it.isDigit() || it == '-' }) {
                            phoneNumber = input
                            showError = false
                        }
                    },
                    label = { Text("電話番号", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = showError && phoneNumber.isBlank(),
                    supportingText = {
                        Text(
                            "ハイフン(-)は省略可能です",
                            fontSize = 16.sp
                        )
                    }
                )

                if (showError) {
                    Text(
                        "お名前と電話番号を入力してください",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // キャンセルボタン
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("キャンセル", fontSize = 18.sp)
                    }

                    // 保存ボタン
                    Button(
                        onClick = {
                            if (name.isBlank() || phoneNumber.isBlank()) {
                                showError = true
                            } else {
                                // 電話番号からハイフンを除去
                                val cleanPhoneNumber = phoneNumber.replace("-", "")
                                onSave(position, name, cleanPhoneNumber)
                                onDismiss()
                            }
                        }
                    ) {
                        Text("保存", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}