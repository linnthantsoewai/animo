package com.ltsw.animo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ltsw.animo.data.model.Pet
import com.ltsw.animo.ui.viewmodel.PetViewModel
import com.ltsw.animo.ui.components.TopHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: PetViewModel) {
    val allPets by viewModel.allPets.collectAsState()
    val selectedPet by viewModel.selectedPet.collectAsState()

    var showAddPetDialog by remember { mutableStateOf(false) }
    var showEditPetDialog by remember { mutableStateOf(false) }
    var petToEdit by remember { mutableStateOf<Pet?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Title Header
        TopHeader("Profile")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Pet Selector Row
                if (allPets.isNotEmpty()) {
                    PetSelectorRow(
                        pets = allPets,
                        selectedPet = selectedPet,
                        onPetSelected = { viewModel.selectPet(it) },
                        onAddPet = { showAddPetDialog = true }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (selectedPet != null) {
                item {
                    // Pet Avatar and Basic Info
                    PetAvatarSection(pet = selectedPet!!)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                petToEdit = selectedPet
                                showEditPetDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profile")
                        }

                        if (allPets.size > 1) {
                            OutlinedButton(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Basic Stats
                    PetStatsCard(pet = selectedPet!!)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Medical Info
                    MedicalInfoSection(pet = selectedPet!!)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Veterinarian Info
                    VeterinarianSection(pet = selectedPet!!)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Additional Notes
                    if (selectedPet!!.notes.isNotEmpty()) {
                        NotesSection(notes = selectedPet!!.notes)
                    }
                }
            } else if (allPets.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                    EmptyPetState(onAddPet = { showAddPetDialog = true })
                }
            }
        }
    }

    // Add Pet Dialog
    if (showAddPetDialog) {
        AddEditPetDialog(
            pet = null,
            onDismiss = { showAddPetDialog = false },
            onSave = { pet ->
                viewModel.insert(pet)
                showAddPetDialog = false
            }
        )
    }

    // Edit Pet Dialog
    if (showEditPetDialog && petToEdit != null) {
        AddEditPetDialog(
            pet = petToEdit,
            onDismiss = {
                showEditPetDialog = false
                petToEdit = null
            },
            onSave = { pet ->
                viewModel.update(pet)
                showEditPetDialog = false
                petToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && selectedPet != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete ${selectedPet!!.name}?") },
            text = { Text("Are you sure you want to delete this pet profile? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(selectedPet!!)
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PetSelectorRow(
    pets: List<Pet>,
    selectedPet: Pet?,
    onPetSelected: (Pet) -> Unit,
    onAddPet: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Your Pets",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pets) { pet ->
                PetSelectorItem(
                    pet = pet,
                    isSelected = pet.id == selectedPet?.id,
                    onClick = { onPetSelected(pet) }
                )
            }

            item {
                AddPetButton(onClick = onAddPet)
            }
        }
    }
}

@Composable
fun PetSelectorItem(
    pet: Pet,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pet.name.firstOrNull()?.toString() ?: "?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = pet.name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AddPetButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Pet",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Add Pet",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PetAvatarSection(pet: Pet) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                pet.name.firstOrNull()?.toString() ?: "?",
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            pet.name,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            pet.breed,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
        if (pet.color.isNotEmpty()) {
            Text(
                pet.color,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun PetStatsCard(pet: Pet) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (pet.age > 0) {
                ProfileStat("Age", "${pet.age} ${if (pet.age == 1) "Year" else "Years"}")
            }
            if (pet.weight > 0) {
                ProfileStat("Weight", "${pet.weight} lbs")
            }
            if (pet.sex.isNotEmpty()) {
                ProfileStat("Sex", pet.sex)
            }
        }
    }
}

@Composable
fun MedicalInfoSection(pet: Pet) {
    if (pet.allergies.isEmpty() && pet.medications.isEmpty() && pet.microchipId.isEmpty()) {
        return
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Medical Information",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (pet.microchipId.isNotEmpty()) {
            InfoCard(
                icon = Icons.Default.QrCode2,
                title = "Microchip ID",
                content = pet.microchipId
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (pet.allergies.isNotEmpty()) {
            InfoCard(
                icon = Icons.Default.Warning,
                title = "Allergies",
                content = pet.allergies
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (pet.medications.isNotEmpty()) {
            InfoCard(
                icon = Icons.Default.MedicalServices,
                title = "Current Medications",
                content = pet.medications
            )
        }
    }
}

@Composable
fun VeterinarianSection(pet: Pet) {
    if (pet.vetName.isEmpty() && pet.vetPhone.isEmpty()) {
        return
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Veterinarian",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (pet.vetName.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            pet.vetName,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (pet.vetPhone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            pet.vetPhone,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesSection(notes: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            "Notes",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text(
                notes,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    content,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun EmptyPetState(onAddPet: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Pets,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No Pets Yet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add your first pet to get started!",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddPet) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Your First Pet")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPetDialog(
    pet: Pet?,
    onDismiss: () -> Unit,
    onSave: (Pet) -> Unit
) {
    var name by remember { mutableStateOf(pet?.name ?: "") }
    var breed by remember { mutableStateOf(pet?.breed ?: "") }
    var age by remember { mutableStateOf(pet?.age?.toString() ?: "") }
    var weight by remember { mutableStateOf(pet?.weight?.let { if (it > 0) it.toString() else "" } ?: "") }
    var sex by remember { mutableStateOf(pet?.sex ?: "") }
    var color by remember { mutableStateOf(pet?.color ?: "") }
    var microchipId by remember { mutableStateOf(pet?.microchipId ?: "") }
    var allergies by remember { mutableStateOf(pet?.allergies ?: "") }
    var medications by remember { mutableStateOf(pet?.medications ?: "") }
    var vetName by remember { mutableStateOf(pet?.vetName ?: "") }
    var vetPhone by remember { mutableStateOf(pet?.vetPhone ?: "") }
    var notes by remember { mutableStateOf(pet?.notes ?: "") }
    var expanded by remember { mutableStateOf(false) }

    // Scroll state to track scrollbar
    val scrollState = rememberLazyListState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp), // Limit height to make it practical
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                item {
                    Text(
                        text = if (pet == null) "Add New Pet" else "Edit ${pet.name}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                // Required Fields
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = breed,
                        onValueChange = { breed = it },
                        label = { Text("Breed *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Basic Info Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) age = it },
                            label = { Text("Age (years)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    weight = it
                                }
                            },
                            label = { Text("Weight (lbs)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }
                }

                // Sex Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = sex,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sex") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("Male", "Female", "Unknown").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        sex = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = color,
                        onValueChange = { color = it },
                        label = { Text("Color") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Medical Info
                item {
                    OutlinedTextField(
                        value = microchipId,
                        onValueChange = { microchipId = it },
                        label = { Text("Microchip ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Allergies") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., Chicken, Wheat") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = medications,
                        onValueChange = { medications = it },
                        label = { Text("Current Medications") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Vet Info
                item {
                    OutlinedTextField(
                        value = vetName,
                        onValueChange = { vetName = it },
                        label = { Text("Veterinarian Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = vetPhone,
                        onValueChange = { vetPhone = it },
                        label = { Text("Veterinarian Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Additional Notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )
                }

                // Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank() && breed.isNotBlank()) {
                                    val newPet = Pet(
                                        id = pet?.id ?: 0,
                                        name = name.trim(),
                                        breed = breed.trim(),
                                        age = age.toIntOrNull() ?: 0,
                                        weight = weight.toDoubleOrNull() ?: 0.0,
                                        sex = sex,
                                        color = color.trim(),
                                        microchipId = microchipId.trim(),
                                        allergies = allergies.trim(),
                                        medications = medications.trim(),
                                        vetName = vetName.trim(),
                                        vetPhone = vetPhone.trim(),
                                        notes = notes.trim()
                                    )
                                    onSave(newPet)
                                }
                            },
                            enabled = name.isNotBlank() && breed.isNotBlank()
                        ) {
                            Text(if (pet == null) "Add" else "Save")
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    }
}



