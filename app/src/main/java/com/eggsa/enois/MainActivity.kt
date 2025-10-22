package com.eggsa.enois

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EggSafeTheme {
                EggSafeApp()
            }
        }
    }
}

object PreferencesHelper {
    private const val PREFS_NAME = "eggsafe_prefs"
    private const val KEY_FAVORITES = "favorites"

    fun saveFavorites(context: Context, favorites: Set<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    fun loadFavorites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }
}

val CreamyWhite = Color(0xFFFFF9E6)
val YolkYellow = Color(0xFFFFD93D)
val FreshGreen = Color(0xFF4ACFAC)
val BrownEgg = Color(0xFFC88752)
val DarkBrown = Color(0xFF8B4513)
val LightYellow = Color(0xFFFFF8DC)
val SoftGreen = Color(0xFFE8F5F1)

@Composable
fun EggSafeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = YolkYellow,
            secondary = FreshGreen,
            background = CreamyWhite,
            surface = Color.White,
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.Black
        ),
        content = content
    )
}

data class EggType(
    val name: String,
    val bird: String,
    val color: Color,
    val emoji: String,
    val storageDays: Int,
    val roomTempDays: Int,
    val frozenMonths: Int,
    val temp: String,
    val facts: List<String>,
    val nutrition: String,
    val size: String,
    val weight: String,
    val cookingTips: List<String>,
    val dangers: String
)

@Composable
fun EggSafeApp() {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    var favorites by remember { mutableStateOf(PreferencesHelper.loadFavorites(context)) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedEgg by remember { mutableStateOf<EggType?>(null) }
    var showFreshnessTest by remember { mutableStateOf(false) }

    // Функция для обновления избранного с сохранением
    val updateFavorites: (String) -> Unit = { eggName ->
        favorites = if (eggName in favorites) {
            favorites - eggName
        } else {
            favorites + eggName
        }
        PreferencesHelper.saveFavorites(context, favorites)
    }

    val eggTypes = remember {
        listOf(
            EggType(
                "Chicken Egg", "Chicken", Color(0xFFFFF5E6), "🥚", 35, 7, 12,
                "2-4°C",
                listOf(
                    "Most common egg worldwide, accounting for 93% of global egg production",
                    "Contains all 9 essential amino acids needed by humans",
                    "The eggshell has about 17,000 tiny pores for gas exchange",
                    "Fresh eggs sink in water, old eggs float due to air pocket expansion"
                ),
                "Protein: 6g, Fat: 5g, Calories: 70, Vitamin D: 10% DV, B12: 9% DV",
                "Medium (50-60g)", "50-60g",
                listOf(
                    "Perfect for all cooking methods: boiling, frying, baking",
                    "Boil for 6-7 minutes for soft, 10-12 for hard-boiled",
                    "Best at room temperature for baking to incorporate air better"
                ),
                "Risk of Salmonella - always cook thoroughly to 71°C internal temp"
            ),
            EggType(
                "Duck Egg", "Duck", Color(0xFFD4E8D4), "🦆", 28, 5, 10,
                "2-4°C",
                listOf(
                    "Contains 30% more protein than chicken eggs",
                    "Richer flavor due to higher fat content (9g vs 5g)",
                    "Shell is thicker and harder to crack than chicken eggs",
                    "Popular in Asian cuisine, especially for century eggs and salted eggs"
                ),
                "Protein: 9g, Fat: 9g, Calories: 130, Vitamin A: 12% DV, Selenium: 25% DV",
                "Large (70-80g)", "70-80g",
                listOf(
                    "Excellent for rich baked goods like cakes and pastries",
                    "Creates fluffier texture in baking due to higher fat",
                    "Best for omelets with creamy texture"
                ),
                "Higher risk of Salmonella than chicken eggs - must cook to 74°C"
            ),
            EggType(
                "Quail Egg", "Quail", Color(0xFFFFFAF0), "🐦", 42, 10, 12,
                "2-6°C",
                listOf(
                    "Contains 5x more iron and potassium than chicken eggs per gram",
                    "Distinctive spotted pattern on shell - no two are identical",
                    "Longest refrigerated shelf life among common bird eggs",
                    "Considered a delicacy in many cultures and used in gourmet dishes"
                ),
                "Protein: 1.2g, Fat: 1g, Calories: 14, Iron: 3% DV, Vitamin B12: 6% DV",
                "Small (10-12g)", "10-12g",
                listOf(
                    "Perfect for appetizers and garnishes",
                    "Boil for only 2-3 minutes for perfectly cooked eggs",
                    "Great for bento boxes and salads"
                ),
                "Low risk if properly handled - cook to 71°C for safety"
            ),
            EggType(
                "Turkey Egg", "Turkey", Color(0xFFF5F0E8), "🦃", 30, 6, 10,
                "2-4°C",
                listOf(
                    "50% larger than chicken eggs with creamier texture",
                    "Stronger, earthier flavor compared to chicken eggs",
                    "Rare commercially due to turkeys producing fewer eggs (100 vs 300/year)",
                    "Shell is speckled with brown spots, similar to quail but much larger"
                ),
                "Protein: 10.8g, Fat: 8.4g, Calories: 135, Calcium: 8% DV, Iron: 15% DV",
                "Large (75-85g)", "75-85g",
                listOf(
                    "Excellent for breakfast dishes and omelets",
                    "Use in place of chicken eggs at 1:1 ratio",
                    "Best scrambled or in frittatas to showcase rich flavor"
                ),
                "Similar Salmonella risk to chicken eggs - cook to 71°C minimum"
            ),
            EggType(
                "Goose Egg", "Goose", Color(0xFFFAFAFA), "🦢", 25, 4, 8,
                "2-4°C",
                listOf(
                    "One goose egg equals approximately 3 chicken eggs in volume",
                    "Much oilier and richer taste than chicken eggs",
                    "Thick, hard shell that requires significant force to crack",
                    "Prized for their large yolks, perfect for custards and rich desserts"
                ),
                "Protein: 20g, Fat: 19g, Calories: 266, Vitamin A: 18% DV, Folate: 16% DV",
                "Extra Large (140-170g)", "140-170g",
                listOf(
                    "Perfect for large family omelets or quiches",
                    "Excellent for making fresh pasta dough",
                    "Best for baking rich cakes and custards"
                ),
                "Higher bacterial risk - always cook thoroughly to 74°C internal"
            ),
            EggType(
                "Ostrich Egg", "Ostrich", Color(0xFFFFFEF0), "🪶", 45, 0, 6,
                "2-4°C",
                listOf(
                    "Largest egg in the world, weighing 1.4-2kg (3-4 lbs)",
                    "Equivalent to 20-24 chicken eggs in volume",
                    "Shell is so thick (2-3mm) it can support an adult's weight",
                    "Takes 90-120 minutes to hard boil completely"
                ),
                "Protein: 235g, Fat: 200g, Calories: 2000, Iron: 450% DV, Vitamin B12: 1200% DV",
                "Giant (1.4-2kg)", "1400-2000g",
                listOf(
                    "Can feed 10-12 people in one scrambled egg dish",
                    "Use a drill or hammer to crack the incredibly thick shell",
                    "Perfect for large gatherings and special events"
                ),
                "Must cook thoroughly - massive size makes temperature control critical"
            ),
            EggType(
                "Pigeon Egg", "Pigeon", Color(0xFFFFFFFF), "🕊️", 20, 3, 6,
                "2-4°C",
                listOf(
                    "Smallest commonly eaten bird egg, considered a delicacy",
                    "Pure white, smooth shell with no markings",
                    "Texture is slightly firmer than chicken eggs",
                    "Popular in Chinese cuisine, especially in soups and dim sum"
                ),
                "Protein: 1g, Fat: 0.8g, Calories: 11, Calcium: 2% DV, Phosphorus: 2% DV",
                "Tiny (8-10g)", "8-10g",
                listOf(
                    "Delicate - boil for only 2 minutes maximum",
                    "Often used in upscale Asian restaurants",
                    "Perfect for appetizers and bite-sized snacks"
                ),
                "Moderate risk - ensure proper cooking to 71°C despite small size"
            ),
            EggType(
                "Emu Egg", "Emu", Color(0xFF2D5F3F), "🦤", 40, 0, 8,
                "2-4°C",
                listOf(
                    "Stunning dark green to teal shell color - most visually striking egg",
                    "Second largest egg after ostrich, weighing 450-650g",
                    "Equals 10-12 chicken eggs in volume",
                    "Native to Australia, increasingly farmed for eggs and meat"
                ),
                "Protein: 100g, Fat: 85g, Calories: 900, Iron: 180% DV, Vitamin A: 25% DV",
                "Very Large (450-650g)", "450-650g",
                listOf(
                    "Takes 45-60 minutes to hard boil",
                    "Excellent for large family meals",
                    "Rich flavor similar to chicken but more intense"
                ),
                "Cook to 74°C minimum - large size requires careful temperature monitoring"
            )
        )
    }

    val filteredEggs = remember(searchQuery, eggTypes) {
        if (searchQuery.isBlank()) eggTypes
        else eggTypes.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.bird.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = CreamyWhite,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier.shadow(12.dp)
            ) {
                val items = listOf(
                    Triple(Icons.Default.Home, "Home", 0),
                    Triple(Icons.Default.Build, "Storage", 1),
                    Triple(Icons.Default.Email, "Encyclopedia", 2),
                    Triple(Icons.Default.CheckCircle, "Tests", 3),
                    Triple(Icons.Default.Star, "Favorites", 4)
                )
                items.forEach { (icon, label, index) ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icon,
                                label,
                                modifier = Modifier.size(if (selectedTab == index) 28.dp else 24.dp)
                            )
                        },
                        label = {
                            Text(
                                label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = YolkYellow,
                            selectedTextColor = DarkBrown,
                            indicatorColor = LightYellow,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(eggTypes, onEggClick = { selectedEgg = it }, onCheckFreshness = { showFreshnessTest = true })
                1 -> StorageScreen(filteredEggs, favorites, searchQuery,
                    onFavoriteToggle = updateFavorites,
                    onSearchChange = { searchQuery = it },
                    onEggClick = { selectedEgg = it }
                )
                2 -> EncyclopediaScreen(filteredEggs, favorites, searchQuery,
                    onFavoriteToggle = updateFavorites,
                    onSearchChange = { searchQuery = it },
                    onEggClick = { selectedEgg = it }
                )
                3 -> TestsScreen(onStartTest = { showFreshnessTest = true })
                4 -> FavoritesScreen(eggTypes.filter { it.name in favorites }, onEggClick = { selectedEgg = it })
            }
        }

        if (selectedEgg != null) {
            EggDetailDialog(
                egg = selectedEgg!!,
                isFavorite = selectedEgg!!.name in favorites,
                onDismiss = { selectedEgg = null },
                onFavoriteToggle = {
                    updateFavorites(selectedEgg!!.name)
                }
            )
        }

        if (showFreshnessTest) {
            FreshnessTestDialog(onDismiss = { showFreshnessTest = false })
        }
    }
}

@Composable
fun HomeScreen(eggTypes: List<EggType>, onEggClick: (EggType) -> Unit, onCheckFreshness: () -> Unit) {
    val randomFact = remember { eggTypes.random() }
    var animatedScale by remember { mutableStateOf(0.8f) }

    LaunchedEffect(Unit) {
        animatedScale = 1f
    }

    val scale by animateFloatAsState(
        targetValue = animatedScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(
                    "EggSafe",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkBrown,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    "Your Complete Egg Encyclopedia",
                    fontSize = 16.sp,
                    color = BrownEgg,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .shadow(12.dp, RoundedCornerShape(28.dp))
                    .clickable { onEggClick(randomFact) },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(randomFact.color.copy(alpha = 0.3f), Color.White)
                            )
                        )
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨", fontSize = 40.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Daily Discovery",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Egg Fact of the Day",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrown
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(randomFact.color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(randomFact.emoji, fontSize = 48.sp)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    randomFact.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrown
                                )
                                Text(
                                    randomFact.bird,
                                    fontSize = 14.sp,
                                    color = BrownEgg
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            randomFact.facts.first(),
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Tap to learn more →",
                            fontSize = 13.sp,
                            color = FreshGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Quick Actions",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    emoji = "🔍",
                    title = "Check Freshness",
                    subtitle = "Water test",
                    color = FreshGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onCheckFreshness
                )
//                QuickActionCard(
//                    emoji = "📚",
//                    title = "Browse All",
//                    subtitle = "${eggTypes.size} types",
//                    color = YolkYellow,
//                    modifier = Modifier.weight(1f),
//                    onClick = { }
//                )
            }
        }

//        item {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                QuickActionCard(
//                    emoji = "🌡️",
//                    title = "Storage Tips",
//                    subtitle = "Keep fresh",
//                    color = BrownEgg.copy(alpha = 0.7f),
//                    modifier = Modifier.weight(1f),
//                    onClick = { }
//                )
//                QuickActionCard(
//                    emoji = "🍳",
//                    title = "Cooking Guide",
//                    subtitle = "Recipes",
//                    color = Color(0xFFFFB74D),
//                    modifier = Modifier.weight(1f),
//                    onClick = { }
//                )
//            }
//        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Popular Eggs",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
//                Text(
//                    "View All →",
//                    fontSize = 14.sp,
//                    color = FreshGreen,
//                    fontWeight = FontWeight.SemiBold
//                )
            }
        }

        items(eggTypes.take(5)) { egg ->
            EnhancedEggCard(egg, onClick = { onEggClick(egg) })
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            PrivacyPolicyCard()
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
fun PrivacyPolicyCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://eggsafeencyclopedia.com/privacy-policy.html"))
                context.startActivity(intent)
            }
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Privacy Policy",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "View details",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionCard(
    emoji: String,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "scale")

    Card(
        modifier = modifier
            .height(140.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clickable {
                pressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(20.dp)
    ) {
        LaunchedEffect(pressed) {
            if (pressed) {
                delay(100)
                pressed = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji, fontSize = 44.sp)
            Column {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EnhancedEggCard(egg: EggType, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(egg.color),
                contentAlignment = Alignment.Center
            ) {
                Text(egg.emoji, fontSize = 38.sp)
            }
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    egg.name,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    egg.bird,
                    fontSize = 14.sp,
                    color = BrownEgg,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = FreshGreen
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${egg.storageDays} days",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        egg.weight,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "View details",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun StorageScreen(
    eggTypes: List<EggType>,
    favorites: Set<String>,
    searchQuery: String,
    onFavoriteToggle: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onEggClick: (EggType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "Storage Guide",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    placeholder = { Text("Search eggs...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = FreshGreen,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        if (eggTypes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No eggs found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            "Try a different search term",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        } else {
            items(eggTypes) { egg ->
                StorageCard(egg, egg.name in favorites, onFavoriteToggle, onEggClick)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun StorageCard(egg: EggType, isFavorite: Boolean, onFavoriteToggle: (String) -> Unit, onEggClick: (EggType) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { onEggClick(egg) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(egg.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(egg.emoji, fontSize = 32.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            egg.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Text(
                            egg.bird,
                            fontSize = 15.sp,
                            color = BrownEgg,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                IconButton(onClick = { onFavoriteToggle(egg.name) }) {
                    Icon(
                        if (isFavorite) Icons.Default.Star else Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) YolkYellow else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StorageInfoBox(
                    icon = "❄️",
                    label = "Refrigerated",
                    value = "${egg.storageDays} days",
                    color = FreshGreen.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
                StorageInfoBox(
                    icon = "🏠",
                    label = "Room Temp",
                    value = "${egg.roomTempDays} days",
                    color = YolkYellow.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StorageInfoBox(
                    icon = "🧊",
                    label = "Frozen",
                    value = "${egg.frozenMonths} months",
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                StorageInfoBox(
                    icon = "🌡️",
                    label = "Best Temp",
                    value = egg.temp,
                    color = BrownEgg.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("⚠️", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Safety Notice",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            egg.dangers,
                            fontSize = 13.sp,
                            color = Color(0xFF6D4C41),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StorageInfoBox(
    icon: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EncyclopediaScreen(
    eggTypes: List<EggType>,
    favorites: Set<String>,
    searchQuery: String,
    onFavoriteToggle: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onEggClick: (EggType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "Encyclopedia",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SoftGreen, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏆", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Collection Progress",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            "Discovered: ${eggTypes.size}/8 species",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    placeholder = { Text("Search encyclopedia...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = YolkYellow,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        if (eggTypes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No eggs found",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(eggTypes) { egg ->
                EncyclopediaCard(egg, egg.name in favorites, onFavoriteToggle, onEggClick)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun EncyclopediaCard(egg: EggType, isFavorite: Boolean, onFavoriteToggle: (String) -> Unit, onEggClick: (EggType) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(28.dp))
            .clickable { onEggClick(egg) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(egg.color, egg.color.copy(alpha = 0.6f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(egg.emoji, fontSize = 90.sp)
                IconButton(
                    onClick = { onFavoriteToggle(egg.name) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Star else Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) YolkYellow else Color.Gray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    egg.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    egg.bird,
                    fontSize = 16.sp,
                    color = BrownEgg,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LightYellow
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Nutrition Facts (per egg)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            egg.nutrition,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Interesting Facts",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Spacer(Modifier.height(8.dp))

                egg.facts.take(2).forEach { fact ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            "• ",
                            color = FreshGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            fact,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Tap to see full details →",
                    fontSize = 13.sp,
                    color = FreshGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun TestsScreen(onStartTest: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Freshness Tests",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartTest() },
                colors = CardDefaults.cardColors(
                    containerColor = FreshGreen
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🧪", fontSize = 40.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Start Interactive Test",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Check if your egg is fresh",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        item {
            Text(
                "Test Methods",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item { TestCard("💧 Water Float Test", "Place egg in water. Fresh eggs sink and lay flat, older eggs stand upright, bad eggs float.", FreshGreen, "Most Reliable") }
        item { TestCard("👃 Smell Test", "Crack the egg into a bowl. Fresh eggs have minimal odor, spoiled eggs smell distinctly sulfuric or rotten.", Color(0xFFFF6B6B), "100% Accurate") }
        item { TestCard("👂 Shake Test", "Gently shake the egg near your ear. Fresh eggs are silent, old eggs produce sloshing sounds.", YolkYellow, "Quick Check") }
        item { TestCard("🔦 Candling Test", "Hold egg up to a bright light in a dark room. Fresh eggs appear mostly opaque, old eggs show large air pockets.", Color(0xFF9B59B6), "Professional") }
        item { TestCard("🍳 Visual Crack Test", "Crack egg onto a flat plate. Fresh: firm, rounded yolk standing tall. Old: flat yolk, watery whites.", BrownEgg, "Final Verification") }

        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "Storage Best Practices",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )
        }

        item { TipCard("Store eggs in original carton to prevent moisture loss and odor absorption", "🥚", YolkYellow) }
        item { TipCard("Keep eggs in main refrigerator body (not door) at consistent 2-4°C temperature", "❄️", FreshGreen) }
        item { TipCard("Don't wash eggs until ready to use - natural coating protects against bacteria", "💦", Color(0xFF64B5F6)) }
        item { TipCard("Store pointed end down to keep yolk centered and extend freshness", "⬇️", BrownEgg) }
        item { TipCard("Use oldest eggs first - write purchase date on carton with marker", "📅", Color(0xFFFFB74D)) }
        item { TipCard("Never store eggs near strong-smelling foods - shells are porous", "👃", Color(0xFFBA68C8)) }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun TestCard(title: String, description: String, color: Color, badge: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun TipCard(text: String, emoji: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text,
                fontSize = 14.sp,
                color = DarkBrown,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FavoritesScreen(favoriteEggs: List<EggType>, onEggClick: (EggType) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamyWhite)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "My Favorites",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (favoriteEggs.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = YolkYellow.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 32.sp)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "You have ${favoriteEggs.size} favorite egg${if (favoriteEggs.size != 1) "s" else ""}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBrown
                            )
                        }
                    }
                }
            }
        }

        if (favoriteEggs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⭐", fontSize = 80.sp)
                        Spacer(Modifier.height(20.dp))
                        Text(
                            "No favorites yet",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Star your favorite eggs in Storage or Encyclopedia to see them here",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        } else {
            items(favoriteEggs) { egg ->
                EnhancedEggCard(egg, onClick = { onEggClick(egg) })
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
fun EggDetailDialog(
    egg: EggType,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = null,
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(egg.color, egg.color.copy(alpha = 0.5f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(egg.emoji, fontSize = 80.sp)
                    }
                }

                item {
                    Column {
                        Text(
                            egg.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Text(
                            egg.bird,
                            fontSize = 18.sp,
                            color = BrownEgg,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = LightYellow
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "📊 Nutrition (per egg)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBrown
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                egg.nutrition,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            "📏 Physical Properties",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = SoftGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Size", fontSize = 12.sp, color = Color.Gray)
                                    Text(egg.size, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkBrown, textAlign = TextAlign.Center)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = YolkYellow.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Weight", fontSize = 12.sp, color = Color.Gray)
                                    Text(egg.weight, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkBrown, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            "❄️ Storage Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(Modifier.height(12.dp))
                        StorageInfoRow("Refrigerated (${egg.temp})", "${egg.storageDays} days")
                        StorageInfoRow("Room Temperature", "${egg.roomTempDays} days")
                        StorageInfoRow("Frozen", "${egg.frozenMonths} months")
                    }
                }

                item {
                    Column {
                        Text(
                            "✨ Interesting Facts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(Modifier.height(8.dp))
                        egg.facts.forEach { fact ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = FreshGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(fact, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 19.sp)
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            "🍳 Cooking Tips",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(Modifier.height(8.dp))
                        egg.cookingTips.forEach { tip ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", color = YolkYellow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(tip, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 19.sp)
                            }
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("⚠️", fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Safety Warning",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    egg.dangers,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6D4C41),
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isFavorite) YolkYellow else Color.LightGray
                    )
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Star else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isFavorite) Color.White else Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isFavorite) "Favorited" else "Add Favorite")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FreshGreen
                    )
                ) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun StorageInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
    }
}

@Composable
fun FreshnessTestDialog(onDismiss: () -> Unit) {
    var currentStep by remember { mutableStateOf(0) }
    var testResult by remember { mutableStateOf<String?>(null) }

    val steps = listOf(
        Triple("💧 Water Test", "Fill a bowl with cold water and gently place the egg in it.", "What happens?"),
        Triple("Egg sinks flat", "Your egg is very fresh! The air cell is small.", "fresh"),
        Triple("Egg stands upright", "Your egg is older but still safe to eat. Use soon.", "okay"),
        Triple("Egg floats", "Your egg is likely spoiled. Don't eat it!", "bad")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(28.dp),
        title = {
            Column {
                Text(
                    "Freshness Test",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    "Step ${currentStep + 1} of 4",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    0 -> {
                        Text("💧", fontSize = 80.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Water Float Test",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Fill a bowl with cold water and gently place the egg in it. Observe what happens.",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "What does your egg do?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkBrown
                        )
                    }
                    1 -> {
                        Text("⬇️", fontSize = 80.sp)
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = FreshGreen.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "✅ Very Fresh!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreshGreen
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Your egg sinks and lays flat on the bottom. This means the air cell inside is very small, indicating maximum freshness.",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Safe to eat raw or lightly cooked",
                                    fontSize = 13.sp,
                                    color = FreshGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    2 -> {
                        Text("⬆️", fontSize = 80.sp)
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = YolkYellow.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "⚠️ Older But OK",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8F00)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Your egg stands upright or tilts in the water. The air cell has grown larger as the egg aged, but it's still safe to consume.",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Cook thoroughly and use within 3-5 days",
                                    fontSize = 13.sp,
                                    color = Color(0xFFFF8F00),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    3 -> {
                        Text("🚫", fontSize = 80.sp)
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFCDD2)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "❌ Spoiled!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD32F2F)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Your egg floats to the surface. The air cell is very large, indicating the egg has gone bad and bacterial growth has occurred.",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "DO NOT EAT - Discard immediately",
                                    fontSize = 13.sp,
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (currentStep == 0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { currentStep = 1 },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("⬇️ Sinks Flat", fontSize = 16.sp)
                    }
                    Button(
                        onClick = { currentStep = 2 },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = YolkYellow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("⬆️ Stands Upright", fontSize = 16.sp, color = Color.Black)
                    }
                    Button(
                        onClick = { currentStep = 3 },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🎈 Floats", fontSize = 16.sp)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { currentStep = 0 },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Test Again")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBrown),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    )
}