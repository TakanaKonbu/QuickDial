package com.takanakonbu.quickdial.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takanakonbu.quickdial.model.Contact

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onCallClick: (String) -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    val showDialog by viewModel.showConfirmDialog.collectAsState()
    val selectedContact by viewModel.selectedContact.collectAsState()
    var showContactDialog by remember { mutableStateOf(false) }
    var editingPosition by remember { mutableStateOf(-1) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 設定スイッチ
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "発信前に確認する",
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = showDialog,
                    onCheckedChange = { viewModel.updateShowDialog(it) }
                )
            }

            Divider()

            // 連絡先リスト
            contacts.forEachIndexed { index, contact ->
                ContactCard(
                    contact = contact,
                    onCallClick = { phoneNumber ->
                        if (showDialog) {
                            viewModel.setSelectedContact(contact)
                        } else {
                            onCallClick(phoneNumber)
                        }
                    },
                    onEditClick = {
                        editingPosition = index
                        editingContact = contact
                        showContactDialog = true
                    },
                    onDeleteClick = {
                        viewModel.deleteContact(index)
                    }
                )
            }

            // 空きスロットの表示
            repeat(4 - contacts.size) { index ->
                EmptyContactSlot(
                    position = contacts.size + index,
                    onAddClick = {
                        editingPosition = contacts.size + index
                        editingContact = null
                        showContactDialog = true
                    }
                )
            }
        }

        // 発信確認ダイアログ
        selectedContact?.let { contact ->
            AlertDialog(
                onDismissRequest = { viewModel.clearSelectedContact() },
                title = { Text("発信の確認", fontSize = 24.sp) },
                text = {
                    Text(
                        "${contact.name}さんに\n電話をかけますか？",
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCallClick(contact.phoneNumber)
                            viewModel.clearSelectedContact()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("はい", fontSize = 20.sp)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.clearSelectedContact() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("いいえ", fontSize = 20.sp)
                    }
                }
            )
        }

        // 連絡先追加/編集ダイアログ
        if (showContactDialog) {
            ContactDialog(
                contact = editingContact,
                position = editingPosition,
                onDismiss = { showContactDialog = false },
                onSave = { position, name, phone ->
                    viewModel.saveContact(position, name, phone)
                }
            )
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    onCallClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCallClick(contact.phoneNumber) }
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 20.sp
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "編集",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "削除",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyContactSlot(
    position: Int,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAddClick)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "タップして連絡先を追加",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}