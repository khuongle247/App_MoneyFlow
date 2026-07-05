package com.example.moneyflow.features.settings.presentation.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moneyflow.R
import com.example.moneyflow.features.settings.presentation.model.SettingsEffect
import com.example.moneyflow.features.settings.presentation.viewmodel.SettingsViewModel
import com.example.moneyflow.ui.navigation.AppRoute
import com.example.moneyflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()
    val language by viewModel.language.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                SettingsEffect.NavigateToOnboarding -> {
                    navController.navigate(AppRoute.Splash.route) { popUpTo(0) }
                }
            }
        }
    }

    var showNameDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.updateAvatar(context, it) }
    }

    val backupLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importBackup(context, it) { } }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA), // Light gray background
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Cài Đặt", fontWeight = FontWeight.ExtraBold) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.Default.HelpOutline, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Profile Section - Clean White Card
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(90.dp)) {
                        if (avatarUri != null) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color(0xFF5C9DFF).copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp), tint = Color(0xFF5C9DFF))
                                }
                            }
                        }
                        Surface(
                            modifier = Modifier.align(Alignment.BottomEnd).size(28.dp).clickable { avatarLauncher.launch("image/*") },
                            shape = CircleShape,
                            color = Color(0xFF5C9DFF),
                            shadowElevation = 4.dp
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Thành viên Premium", color = Color(0xFF5C9DFF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Settings Groups
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), 
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("TÀI KHOẢN", style = MaterialTheme.typography.labelLarge, color = TextGray, modifier = Modifier.padding(start = 8.dp))
                
                PremiumSettingsItem(Icons.Default.Badge, "Đổi tên hiển thị", userName) { showNameDialog = true }
                PremiumSettingsItem(Icons.Default.Translate, "Ngôn ngữ", if (language == "vi") "Tiếng Việt" else "English") { showLanguageDialog = true }
                
                Spacer(modifier = Modifier.height(12.dp))
                Text("ỨNG DỤNG", style = MaterialTheme.typography.labelLarge, color = TextGray, modifier = Modifier.padding(start = 8.dp))

                // Dark Mode Toggle Item
                Surface(
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DarkMode, null, tint = Color(0xFF5C9DFF))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Giao diện tối", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Switch(
                            checked = isDarkMode ?: false, 
                            onCheckedChange = { viewModel.toggleDarkMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF5C9DFF))
                        )
                    }
                }
                
                PremiumSettingsItem(Icons.Default.CloudSync, "Sao lưu & Khôi phục") {
                    viewModel.exportBackup(context) {}
                }

                PremiumSettingsItem(Icons.Default.DeleteSweep, "Xóa toàn bộ dữ liệu", tint = ErrorRed) {
                    // Handled by delete dialog
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            Text("MoneyFlow v1.0.0", color = TextGray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showNameDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Đổi tên người dùng") },
            text = { OutlinedTextField(value = tempName, onValueChange = { tempName = it }, shape = RoundedCornerShape(12.dp)) },
            confirmButton = { TextButton(onClick = { viewModel.updateUserName(tempName); showNameDialog = false }) { Text("Lưu") } }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Ngôn ngữ") },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.updateLanguage("vi"); showLanguageDialog = false }.padding(16.dp)) {
                        Text("Tiếng Việt", modifier = Modifier.weight(1f))
                        if (language == "vi") Icon(Icons.Default.Check, null, tint = PrimaryBlue)
                    }
                    Row(modifier = Modifier.fillMaxWidth().clickable { viewModel.updateLanguage("en"); showLanguageDialog = false }.padding(16.dp)) {
                        Text("English", modifier = Modifier.weight(1f))
                        if (language == "en") Icon(Icons.Default.Check, null, tint = PrimaryBlue)
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
    }
}

@Composable
fun PremiumSettingsItem(icon: ImageVector, title: String, value: String? = null, tint: Color = Color(0xFF5C9DFF), onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = tint)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            if (value != null) {
                Text(value, color = TextGray, fontSize = 13.sp, modifier = Modifier.padding(end = 8.dp))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
        }
    }
}
