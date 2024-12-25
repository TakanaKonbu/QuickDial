package com.takanakonbu.quickdial.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.takanakonbu.quickdial.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ContactPreferences(private val context: Context) {

    companion object {
        private val SHOW_DIALOG = booleanPreferencesKey("show_dialog")
        private val CONTACTS = listOf(
            Triple(
                stringPreferencesKey("contact_name_1"),
                stringPreferencesKey("contact_phone_1"),
                intPreferencesKey("contact_id_1")
            ),
            Triple(
                stringPreferencesKey("contact_name_2"),
                stringPreferencesKey("contact_phone_2"),
                intPreferencesKey("contact_id_2")
            ),
            Triple(
                stringPreferencesKey("contact_name_3"),
                stringPreferencesKey("contact_phone_3"),
                intPreferencesKey("contact_id_3")
            ),
            Triple(
                stringPreferencesKey("contact_name_4"),
                stringPreferencesKey("contact_phone_4"),
                intPreferencesKey("contact_id_4")
            )
        )
    }

    val showDialog: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_DIALOG] ?: true
        }

    suspend fun setShowDialog(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_DIALOG] = show
        }
    }

    suspend fun saveContact(position: Int, contact: Contact) {
        if (position !in 0..3) return

        context.dataStore.edit { preferences ->
            preferences[CONTACTS[position].first] = contact.name
            preferences[CONTACTS[position].second] = contact.phoneNumber
            preferences[CONTACTS[position].third] = contact.id
        }
    }

    val contacts: Flow<List<Contact>> = context.dataStore.data
        .map { preferences ->
            CONTACTS.mapNotNull { (nameKey, phoneKey, idKey) ->
                val name = preferences[nameKey] ?: return@mapNotNull null
                val phone = preferences[phoneKey] ?: return@mapNotNull null
                val id = preferences[idKey] ?: return@mapNotNull null
                Contact(id, name, phone)
            }
        }

    suspend fun deleteContact(position: Int) {
        if (position !in 0..3) return

        context.dataStore.edit { preferences ->
            preferences.remove(CONTACTS[position].first)
            preferences.remove(CONTACTS[position].second)
            preferences.remove(CONTACTS[position].third)
        }
    }
}