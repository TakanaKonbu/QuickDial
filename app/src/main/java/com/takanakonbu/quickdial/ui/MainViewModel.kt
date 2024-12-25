package com.takanakonbu.quickdial.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.takanakonbu.quickdial.data.ContactPreferences
import com.takanakonbu.quickdial.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val contactPreferences: ContactPreferences
) : ViewModel() {

    val contacts: StateFlow<List<Contact>> = contactPreferences.contacts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val showConfirmDialog: StateFlow<Boolean> = contactPreferences.showDialog
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact

    fun updateShowDialog(show: Boolean) {
        viewModelScope.launch {
            contactPreferences.setShowDialog(show)
        }
    }

    fun saveContact(position: Int, name: String, phoneNumber: String) {
        if (name.isBlank() || phoneNumber.isBlank()) return
        viewModelScope.launch {
            contactPreferences.saveContact(
                position,
                Contact(
                    id = position,
                    name = name,
                    phoneNumber = phoneNumber
                )
            )
        }
    }

    fun deleteContact(position: Int) {
        viewModelScope.launch {
            contactPreferences.deleteContact(position)
        }
    }

    fun setSelectedContact(contact: Contact) {
        _selectedContact.value = contact
    }

    fun clearSelectedContact() {
        _selectedContact.value = null
    }

    class Factory(private val contactPreferences: ContactPreferences) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(contactPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}