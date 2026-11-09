package com.example.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.core.view.WindowCompat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.AnnotationEntity
import com.example.data.ChapterEntity
import com.example.data.GlossaryEntity
import com.example.data.NovelEntity
import com.example.data.ReaderSettings
import com.example.data.TextProcessingUtils
import com.example.ui.theme.AvailableReaderFonts
import com.example.ui.theme.HighlightColors
import com.example.ui.theme.ReaderAmoledBg
import com.example.ui.theme.ReaderAmoledText
import com.example.ui.theme.ReaderDarkBg
import com.example.ui.theme.ReaderDarkText
import com.example.ui.theme.ReaderLightBg
import com.example.ui.theme.ReaderLightText
import com.example.ui.theme.ReaderPaperBg
import com.example.ui.theme.ReaderPaperText
import com.example.ui.theme.ReaderSepiaBg
import com.example.ui.theme.ReaderSepiaText
import com.example.ui.theme.resolveReaderFontFamily
import com.example.ui.theme.resolveReaderTypeface
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.SubScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, FlowPreview::class)
@Composable
fun ReaderScreen(
    viewModel: MainViewModel,
    chapter: ChapterEntity,
    novel: NovelEntity?,
    novelChapters: List<ChapterEntity>,
    annotations: List<AnnotationEntity>,
    glossaryTerms: List<GlossaryEntity>,
    globalSettings: ReaderSettings,
    onBack: () -> Unit,
    onOpenAllSettings: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val localView = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val effectiveSettings = remember(novel?.settingsOverrideJson, globalSettings) {
        if (!novel?.settingsOverrideJson.isNullOrBlank()) {
            ReaderSettings.fromJsonString(novel?.settingsOverrideJson)
        } else {
            globalSettings
        }
    }

    // Fast cached paragraph splitting (0ms for cached/normal chapters, async only for huge 100k+ char files)
    val paragraphs by produceState(
        initialValue = remember(chapter.id, chapter.modifiedAt, effectiveSettings.hideTashkeel) {
            if (chapter.content.length < 80_000) {
                TextProcessingUtils.splitIntoParagraphs(chapter.content, effectiveSettings.hideTashkeel)
            } else {
                listOf("جاري فتح الفصل...")
            }
        },
        key1 = chapter.id,
        key2 = chapter.modifiedAt,
        key3 = effectiveSettings.hideTashkeel
    ) {
        if (chapter.content.length >= 80_000) {
            value = withContext(Dispatchers.Default) {
                TextProcessingUtils.splitIntoParagraphs(chapter.content, effectiveSettings.hideTashkeel)
            }
        }
    }

    val initialParagraphIdx = remember(chapter.id) {
        chapter.paragraphIndex.coerceAtLeast(0)
    }

    val nativeListViewRef = remember { arrayOfNulls<ListView>(1) }
    val currentScrollParaIdx = remember(chapter.id) {
        mutableIntStateOf(initialParagraphIdx.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0)))
    }

    val paragraphsPerPage = 6
    val totalPages = max(1, (paragraphs.size + paragraphsPerPage - 1) / paragraphsPerPage)
    val pagerState = rememberPagerState(
        initialPage = (initialParagraphIdx / paragraphsPerPage).coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )

    val scrollToParagraph: (Int) -> Unit = { targetIdx ->
        val safeTarget = targetIdx.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
        currentScrollParaIdx.intValue = safeTarget
        if (effectiveSettings.readingMode == "PAGES") {
            coroutineScope.launch {
                pagerState.scrollToPage(safeTarget / paragraphsPerPage)
            }
        } else {
            nativeListViewRef[0]?.setSelectionFromTop(safeTarget, 0)
        }
    }

    // Ensure position jumps accurately if async paragraph splitting finishes for huge files
    LaunchedEffect(paragraphs.size) {
        if (paragraphs.size > 1 && initialParagraphIdx > 0 && currentScrollParaIdx.intValue == 0) {
            val safeTarget = initialParagraphIdx.coerceIn(0, paragraphs.size - 1)
            currentScrollParaIdx.intValue = safeTarget
            nativeListViewRef[0]?.setSelectionFromTop(safeTarget, 0)
        }
    }

    // UI Bars & Overlays State
    var barsVisible by remember { mutableStateOf(false) }
    var showResumeBanner by remember(chapter.id) { mutableStateOf(chapter.paragraphIndex > 0) }
    var showInTextSearch by remember { mutableStateOf(false) }
    var inTextSearchQuery by remember { mutableStateOf("") }
    var currentSearchMatchIdx by remember { mutableIntStateOf(0) }
    var showChaptersDrawer by remember { mutableStateOf(false) }
    var showLiveSettingsSheet by remember { mutableStateOf(false) }
    var showTopOverflowMenu by remember { mutableStateOf(false) }

    // Selected paragraph for Highlight / Note / Copy / Glossary Lookup
    var selectedParagraphIndex by remember { mutableStateOf<Int?>(null) }
    var showAddNoteDialogForParagraph by remember { mutableStateOf<Int?>(null) }
    var selectedGlossaryPopup by remember { mutableStateOf<GlossaryEntity?>(null) }

    // Text-to-Speech (TTS) State
    var showTtsControls by remember { mutableStateOf(false) }
    var isTtsPlaying by remember { mutableStateOf(false) }
    var ttsSpeakingParagraphIndex by remember { mutableIntStateOf(-1) }
    var ttsSpeed by remember { mutableFloatStateOf(1.0f) }
    var ttsPitch by remember { mutableFloatStateOf(1.0f) }
    var ttsSleepTimerMinutes by remember { mutableIntStateOf(0) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    // Helper lambda to read current paragraph index on-demand without triggering root recomposition during scroll
    val getCurrentParaIdx: () -> Int = {
        if (effectiveSettings.readingMode == "PAGES") {
            (pagerState.currentPage * paragraphsPerPage).coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
        } else {
            currentScrollParaIdx.intValue.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
        }
    }

    // Keep Screen On, In-App Brightness, & White Status Bar Icons over the Shaded Status Bar Scrim
    DisposableEffect(effectiveSettings.keepScreenOn, effectiveSettings.inAppBrightness) {
        val window = (context as? Activity)?.window
        val insetsController = if (window != null) WindowCompat.getInsetsController(window, localView) else null
        val prevLightStatusBars = insetsController?.isAppearanceLightStatusBars ?: true
        // Use light (white) status bar icons so they are crisp and readable over the shaded status bar background
        insetsController?.isAppearanceLightStatusBars = false

        if (effectiveSettings.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (window != null) {
            val params = window.attributes
            params.screenBrightness = if (effectiveSettings.inAppBrightness > 0f) {
                effectiveSettings.inAppBrightness.coerceIn(0.05f, 1.0f)
            } else {
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
            window.attributes = params
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            insetsController?.isAppearanceLightStatusBars = prevLightStatusBars
        }
    }

    // Initialize Android TextToSpeech Engine Lazily ONLY when user opens TTS controls
    DisposableEffect(showTtsControls) {
        var engine: TextToSpeech? = null
        if (showTtsControls) {
            engine = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    engine?.language = Locale("ar")
                    engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}
                        override fun onDone(utteranceId: String?) {
                            val idx = utteranceId?.removePrefix("para_")?.toIntOrNull() ?: return
                            val nextIdx = idx + 1
                            if (nextIdx < paragraphs.size && isTtsPlaying) {
                                ttsSpeakingParagraphIndex = nextIdx
                                coroutineScope.launch {
                                    scrollToParagraph(nextIdx)
                                }
                                engine?.speak(
                                    paragraphs[nextIdx],
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "para_$nextIdx"
                                )
                            } else {
                                isTtsPlaying = false
                                ttsSpeakingParagraphIndex = -1
                            }
                        }

                        override fun onError(utteranceId: String?) {
                            isTtsPlaying = false
                        }
                    })
                }
            }
            ttsEngine = engine
        }
        onDispose {
            engine?.stop()
            engine?.shutdown()
            ttsEngine = null
        }
    }

    // TTS Sleep Timer Countdown
    LaunchedEffect(ttsSleepTimerMinutes, isTtsPlaying) {
        if (isTtsPlaying && ttsSleepTimerMinutes > 0) {
            delay(ttsSleepTimerMinutes * 60_000L)
            ttsEngine?.stop()
            isTtsPlaying = false
            ttsSpeakingParagraphIndex = -1
            ttsSleepTimerMinutes = 0
            viewModel.showToastMessage("توقف الاستماع الصوتي حسب المؤقت")
        }
    }

    // Auto-Dismiss Resume Banner after 3.5 seconds
    LaunchedEffect(showResumeBanner) {
        if (showResumeBanner) {
            delay(3500L)
            showResumeBanner = false
        }
    }

    // Save position to SQLite ONLY on Pause / Stop / Exit / App Background (Zero SQLite writes during scroll!)
    DisposableEffect(lifecycleOwner, chapter.id, paragraphs.size) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                val idx = getCurrentParaIdx()
                viewModel.saveReadingPosition(
                    chapterId = chapter.id,
                    paragraphIndex = idx,
                    charOffset = 0,
                    totalParagraphs = paragraphs.size
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            val idx = getCurrentParaIdx()
            viewModel.saveReadingPosition(
                chapterId = chapter.id,
                paragraphIndex = idx,
                charOffset = 0,
                totalParagraphs = paragraphs.size
            )
        }
    }

    // Auto-Scroll Coroutine
    LaunchedEffect(effectiveSettings.autoScrollSpeed, effectiveSettings.readingMode, barsVisible) {
        if (effectiveSettings.autoScrollSpeed > 0f && effectiveSettings.readingMode == "SCROLL" && !barsVisible) {
            while (true) {
                nativeListViewRef[0]?.scrollListBy((effectiveSettings.autoScrollSpeed * 2.2f).toInt().coerceAtLeast(1))
                delay(16L)
            }
        }
    }

    // Resolve Reader Colors based on active Theme Mode
    val (readerBgColor, readerTextColor) = remember(
        effectiveSettings.themeMode,
        effectiveSettings.customBgHex,
        effectiveSettings.customTextHex
    ) {
        when (effectiveSettings.themeMode) {
            "LIGHT" -> ReaderLightBg to ReaderLightText
            "DARK" -> ReaderDarkBg to ReaderDarkText
            "AMOLED" -> ReaderAmoledBg to ReaderAmoledText
            "SEPIA" -> ReaderSepiaBg to ReaderSepiaText
            "PAPER" -> ReaderPaperBg to ReaderPaperText
            "CUSTOM" -> parseColorSafe(effectiveSettings.customBgHex, ReaderSepiaBg) to
                parseColorSafe(effectiveSettings.customTextHex, ReaderSepiaText)
            else -> ReaderSepiaBg to ReaderSepiaText
        }
    }

    // Group annotations by paragraphIndex in a HashMap for O(1) lookup during scrolling
    val annotationsByParagraph = remember(annotations, chapter.id) {
        val filtered = annotations.filter { it.chapterId == chapter.id }
        if (filtered.isEmpty()) emptyMap() else filtered.groupBy { it.paragraphIndex }
    }

    val activeGlossary = remember(glossaryTerms, chapter.novelId, effectiveSettings.highlightGlossaryInText) {
        if (!effectiveSettings.highlightGlossaryInText || glossaryTerms.isEmpty()) emptyList()
        else glossaryTerms.filter { it.novelId == null || it.novelId == chapter.novelId }
    }

    // Previous & Next Chapter navigation in the same Novel
    val currentChapterOrderIdx = remember(novelChapters, chapter.id) {
        novelChapters.indexOfFirst { it.id == chapter.id }
    }
    val previousChapter = if (currentChapterOrderIdx > 0) novelChapters[currentChapterOrderIdx - 1] else null
    val nextChapter = if (currentChapterOrderIdx >= 0 && currentChapterOrderIdx + 1 < novelChapters.size) {
        novelChapters[currentChapterOrderIdx + 1]
    } else null

    val searchMatchingParagraphIndices = remember(paragraphs, inTextSearchQuery) {
        if (inTextSearchQuery.isBlank()) emptyList()
        else paragraphs.mapIndexedNotNull { index, text ->
            if (text.contains(inTextSearchQuery, ignoreCase = true)) index else null
        }
    }

    val readerFontFamily = remember(effectiveSettings.fontFamily) {
        resolveReaderFontFamily(effectiveSettings.fontFamily)
    }

    val readerTextAlign = when (effectiveSettings.textAlign) {
        "LEFT" -> TextAlign.Left
        "CENTER" -> TextAlign.Center
        "JUSTIFY" -> TextAlign.Justify
        else -> TextAlign.Right
    }

    val paragraphTextStyle = remember(
        readerTextColor,
        readerFontFamily,
        effectiveSettings.fontSizeSp,
        effectiveSettings.isBoldWeight,
        effectiveSettings.lineHeightMultiplier,
        effectiveSettings.letterSpacingSp,
        readerTextAlign,
        effectiveSettings.firstLineIndentDp
    ) {
        TextStyle(
            color = readerTextColor,
            fontFamily = readerFontFamily,
            fontSize = effectiveSettings.fontSizeSp.sp,
            fontWeight = if (effectiveSettings.isBoldWeight) FontWeight.Bold else FontWeight.Normal,
            lineHeight = (effectiveSettings.fontSizeSp * effectiveSettings.lineHeightMultiplier).sp,
            letterSpacing = effectiveSettings.letterSpacingSp.sp,
            textAlign = readerTextAlign,
            textIndent = if (effectiveSettings.firstLineIndentDp > 0.5f) {
                TextIndent(firstLine = effectiveSettings.firstLineIndentDp.sp)
            } else {
                TextIndent.None
            }
        )
    }

    val hasGlobalFormatting = remember(annotationsByParagraph, activeGlossary, inTextSearchQuery) {
        annotationsByParagraph.isNotEmpty() || activeGlossary.isNotEmpty() || inTextSearchQuery.isNotBlank()
    }

    val paragraphSpacing = remember(effectiveSettings.emptyLineBetweenParagraphs, effectiveSettings.paragraphSpacingDp) {
        if (effectiveSettings.emptyLineBetweenParagraphs) effectiveSettings.paragraphSpacingDp.dp
        else (effectiveSettings.paragraphSpacingDp / 3f).dp
    }

    val nativeReaderTypeface = remember(context, effectiveSettings.fontFamily, effectiveSettings.isBoldWeight) {
        resolveReaderTypeface(context, effectiveSettings.fontFamily, effectiveSettings.isBoldWeight)
    }

    val primaryColorInt = MaterialTheme.colorScheme.primary.toArgb()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBgColor)
    ) {
        // Main Reading Area: Clipped cleanly below the shaded system status bar so text NEVER overlaps clock/battery/network icons
        if (effectiveSettings.readingMode == "PAGES") {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .pointerInput(effectiveSettings.touchZonesMode) {
                        detectTapGestures(
                            onTap = { offset ->
                                val third = size.width / 3f
                                when {
                                    effectiveSettings.touchZonesMode == "SIDES_FLIP" && offset.x < third -> {
                                        coroutineScope.launch {
                                            val next = (pagerState.currentPage + 1).coerceAtMost(totalPages - 1)
                                            pagerState.animateScrollToPage(next)
                                        }
                                    }
                                    effectiveSettings.touchZonesMode == "SIDES_FLIP" && offset.x > third * 2 -> {
                                        coroutineScope.launch {
                                            val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                                            pagerState.animateScrollToPage(prev)
                                        }
                                    }
                                    else -> {
                                        if (selectedParagraphIndex != null) selectedParagraphIndex = null
                                        else barsVisible = !barsVisible
                                    }
                                }
                            },
                            onLongPress = {
                                val curr = getCurrentParaIdx()
                                selectedParagraphIndex = if (selectedParagraphIndex == curr) null else curr
                            }
                        )
                    }
                    .padding(
                        start = effectiveSettings.marginHorizontalDp.dp,
                        end = effectiveSettings.marginHorizontalDp.dp,
                        top = effectiveSettings.marginVerticalDp.dp + 8.dp,
                        bottom = effectiveSettings.marginVerticalDp.dp + 28.dp
                    )
            ) { pageIndex ->
                val startPara = pageIndex * paragraphsPerPage
                val endPara = (startPara + paragraphsPerPage).coerceAtMost(paragraphs.size)
                val pageParagraphs = if (startPara < endPara) paragraphs.subList(startPara, endPara) else emptyList()

                Column(
                    verticalArrangement = Arrangement.spacedBy(paragraphSpacing),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    pageParagraphs.forEachIndexed { localIdx, paraText ->
                        val globalIdx = startPara + localIdx
                        if (!hasGlobalFormatting && ttsSpeakingParagraphIndex != globalIdx && selectedParagraphIndex != globalIdx) {
                            Text(
                                text = paraText,
                                style = paragraphTextStyle,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            ReaderParagraphItem(
                                paragraphIndex = globalIdx,
                                paragraphText = paraText,
                                textStyle = paragraphTextStyle,
                                annotations = annotationsByParagraph[globalIdx] ?: emptyList(),
                                glossaryTerms = activeGlossary,
                                searchQuery = inTextSearchQuery,
                                isTtsSpeaking = ttsSpeakingParagraphIndex == globalIdx,
                                isSelected = selectedParagraphIndex == globalIdx
                            )
                        }
                    }
                }
            }
        } else {
            // Hardware-Accelerated Native Android ListView + Recycled TextView ViewHolders for Locked 60fps on Galaxy M02s!
            val readerAdapter = remember {
                ReaderNativeAdapter(
                    paragraphs = paragraphs,
                    chapter = chapter,
                    nextChapter = nextChapter,
                    previousChapter = previousChapter,
                    settings = effectiveSettings,
                    typeface = nativeReaderTypeface,
                    textColorInt = readerTextColor.toArgb(),
                    bgColorInt = readerBgColor.toArgb(),
                    primaryColorInt = primaryColorInt,
                    annotationsByPara = annotationsByParagraph,
                    glossaryTerms = activeGlossary,
                    searchQuery = inTextSearchQuery,
                    ttsSpeakingIdx = ttsSpeakingParagraphIndex,
                    selectedParaIdx = selectedParagraphIndex,
                    onOpenChapter = { targetId ->
                        viewModel.saveReadingPosition(chapter.id, (paragraphs.size - 1).coerceAtLeast(0), 0, paragraphs.size)
                        viewModel.openChapterForReading(targetId)
                    },
                    onChangeStage = { newStage ->
                        viewModel.updateChapter(chapter.copy(translatorStage = newStage))
                    }
                )
            }

            AndroidView(
                factory = { ctx ->
                    var lastTouchX = 0f
                    ListView(ctx).apply {
                        nativeListViewRef[0] = this
                        divider = null
                        dividerHeight = 0
                        clipToPadding = false
                        overScrollMode = View.OVER_SCROLL_NEVER
                        isVerticalScrollBarEnabled = true
                        isFastScrollEnabled = false
                        selector = ColorDrawable(android.graphics.Color.TRANSPARENT)
                        cacheColorHint = readerBgColor.toArgb()

                        val density = ctx.resources.displayMetrics.density
                        val hPad = (effectiveSettings.marginHorizontalDp * density).toInt()
                        val vPadTop = ((effectiveSettings.marginVerticalDp + 8f) * density).toInt()
                        val vPadBottom = ((effectiveSettings.marginVerticalDp + 56f) * density).toInt()
                        setPadding(hPad, vPadTop, hPad, vPadBottom)

                        adapter = readerAdapter

                        val startIdx = initialParagraphIdx.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
                        if (startIdx > 0) {
                            post { setSelectionFromTop(startIdx, 0) }
                        }

                        setOnTouchListener { _, event ->
                            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                                lastTouchX = event.x
                            }
                            false
                        }

                        setOnItemClickListener { _, _, position, _ ->
                            if (position < readerAdapter.paragraphs.size) {
                                val width = width.toFloat().coerceAtLeast(1f)
                                val third = width / 3f
                                val zones = readerAdapter.settings.touchZonesMode
                                when {
                                    zones == "SIDES_FLIP" && lastTouchX < third -> {
                                        val target = (currentScrollParaIdx.intValue + 5)
                                            .coerceAtMost((readerAdapter.paragraphs.size - 1).coerceAtLeast(0))
                                        currentScrollParaIdx.intValue = target
                                        setSelectionFromTop(target, 0)
                                    }
                                    zones == "SIDES_FLIP" && lastTouchX > third * 2f -> {
                                        val target = (currentScrollParaIdx.intValue - 5).coerceAtLeast(0)
                                        currentScrollParaIdx.intValue = target
                                        setSelectionFromTop(target, 0)
                                    }
                                    else -> {
                                        if (selectedParagraphIndex != null) {
                                            selectedParagraphIndex = null
                                        } else {
                                            barsVisible = !barsVisible
                                        }
                                    }
                                }
                            }
                        }

                        setOnItemLongClickListener { view, _, position, _ ->
                            if (position < readerAdapter.paragraphs.size) {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                selectedParagraphIndex = if (selectedParagraphIndex == position) null else position
                                true
                            } else {
                                false
                            }
                        }

                        setOnScrollListener(object : AbsListView.OnScrollListener {
                            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
                            override fun onScroll(
                                view: AbsListView?,
                                firstVisibleItem: Int,
                                visibleItemCount: Int,
                                totalItemCount: Int
                            ) {
                                val paraCount = readerAdapter.paragraphs.size
                                if (paraCount > 0) {
                                    val safeIdx = firstVisibleItem.coerceIn(0, paraCount - 1)
                                    if (safeIdx != currentScrollParaIdx.intValue) {
                                        currentScrollParaIdx.intValue = safeIdx
                                        viewModel.updateInMemoryReadingPosition(
                                            chapterId = readerAdapter.chapter.id,
                                            paragraphIndex = safeIdx,
                                            charOffset = 0,
                                            totalParagraphs = paraCount
                                        )
                                    }
                                }
                            }
                        })
                    }
                },
                update = { listView ->
                    nativeListViewRef[0] = listView
                    val density = listView.resources.displayMetrics.density
                    val hPad = (effectiveSettings.marginHorizontalDp * density).toInt()
                    val vPadTop = ((effectiveSettings.marginVerticalDp + 8f) * density).toInt()
                    val vPadBottom = ((effectiveSettings.marginVerticalDp + 56f) * density).toInt()
                    if (listView.paddingLeft != hPad || listView.paddingTop != vPadTop || listView.paddingBottom != vPadBottom) {
                        listView.setPadding(hPad, vPadTop, hPad, vPadBottom)
                    }
                    listView.cacheColorHint = readerBgColor.toArgb()

                    val chapterChanged = readerAdapter.chapter.id != chapter.id
                    val changed = readerAdapter.updateData(
                        newParagraphs = paragraphs,
                        newChapter = chapter,
                        newNextChapter = nextChapter,
                        newPreviousChapter = previousChapter,
                        newSettings = effectiveSettings,
                        newTypeface = nativeReaderTypeface,
                        newTextColorInt = readerTextColor.toArgb(),
                        newBgColorInt = readerBgColor.toArgb(),
                        newPrimaryColorInt = primaryColorInt,
                        newAnnotationsByPara = annotationsByParagraph,
                        newGlossaryTerms = activeGlossary,
                        newSearchQuery = inTextSearchQuery,
                        newTtsSpeakingIdx = ttsSpeakingParagraphIndex,
                        newSelectedParaIdx = selectedParagraphIndex
                    )
                    if (changed) {
                        readerAdapter.notifyDataSetChanged()
                    }
                    if (chapterChanged) {
                        val startIdx = chapter.paragraphIndex.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
                        currentScrollParaIdx.intValue = startIdx
                        listView.post { listView.setSelectionFromTop(startIdx, 0) }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .testTag("reader_lazy_column")
            )
        }

        // Warm Eye-Care Filter Overlay
        if (effectiveSettings.warmEyeFilterIntensity > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFFFF9800).copy(
                            alpha = effectiveSettings.warmEyeFilterIntensity.coerceIn(0f, 0.55f)
                        )
                    )
            )
        }

        // Shaded Status Bar Scrim behind phone's time, battery, and network icons (Always visible & never overlaps text)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(Color(0xFF121212).copy(alpha = 0.62f))
        )

        // Isolated Chapter Progress Edge Bar (Chapter progress only — lightweight static Box bar)
        if (effectiveSettings.showThinEdgeProgress && !barsVisible) {
            ChapterEdgeProgressOverlay(
                currentScrollParaState = currentScrollParaIdx,
                pagerState = pagerState,
                readingMode = effectiveSettings.readingMode,
                paragraphsCount = paragraphs.size,
                paragraphsPerPage = paragraphsPerPage,
                showCounterText = effectiveSettings.showPercentAndRemaining,
                readerBgColor = readerBgColor,
                readerTextColor = readerTextColor,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Resume Reading Notification Banner (Respects statusBarsPadding)
        AnimatedVisibility(
            visible = showResumeBanner,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                tonalElevation = 6.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "تابعت من الفقرة ${initialParagraphIdx + 1}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(
                        onClick = {
                            showResumeBanner = false
                            scrollToParagraph(0)
                            viewModel.saveReadingPosition(chapter.id, 0, 0, paragraphs.size)
                        }
                    ) {
                        Text("من البداية", color = MaterialTheme.colorScheme.inversePrimary, fontWeight = FontWeight.Bold)
                    }
                    IconButton(
                        onClick = { showResumeBanner = false },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = MaterialTheme.colorScheme.inverseOnSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Selected Paragraph Action Bar (Copy, Highlight with Colors, Note, Search, Share)
        val activeSelectedPara = selectedParagraphIndex
        if (activeSelectedPara != null && activeSelectedPara in paragraphs.indices) {
            val selectedText = paragraphs[activeSelectedPara]
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 68.dp, start = 16.dp, end = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "فقرة ${activeSelectedPara + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            HighlightColors.forEach { hex ->
                                val col = parseColorSafe(hex, Color.Yellow)
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(col)
                                        .border(1.dp, Color.Black.copy(alpha = 0.25f), CircleShape)
                                        .clickable {
                                            viewModel.addAnnotation(
                                                chapterId = chapter.id,
                                                novelId = chapter.novelId,
                                                type = "HIGHLIGHT",
                                                paragraphIndex = activeSelectedPara,
                                                startOffset = 0,
                                                endOffset = selectedText.length,
                                                selectedText = selectedText,
                                                noteText = "",
                                                colorHex = hex
                                            )
                                            selectedParagraphIndex = null
                                        }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                cm?.setPrimaryClip(ClipData.newPlainText("فقرة", selectedText))
                                viewModel.showToastMessage("تم نسخ الفقرة")
                                selectedParagraphIndex = null
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ")
                        }
                        TextButton(
                            onClick = {
                                showAddNoteDialogForParagraph = activeSelectedPara
                            }
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ملاحظة")
                        }
                        TextButton(
                            onClick = {
                                inTextSearchQuery = selectedText.split(" ").firstOrNull() ?: ""
                                showInTextSearch = true
                                selectedParagraphIndex = null
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("بحث")
                        }
                        TextButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, selectedText)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة"))
                                selectedParagraphIndex = null
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مشاركة")
                        }
                    }
                }
            }
        }

        // Top Bar (Appears on center tap)
        AnimatedVisibility(
            visible = barsVisible && effectiveSettings.showTopBarInReader,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            val activeParaOnBarOpen = getCurrentParaIdx()
            val isBookmarked = annotationsByParagraph[activeParaOnBarOpen]?.any { it.type == "BOOKMARK" } == true

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("reader_back_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (novel != null) {
                                Text(
                                    text = novel.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        IconButton(onClick = { showInTextSearch = !showInTextSearch }) {
                            Icon(Icons.Default.Search, contentDescription = "بحث في الفصل")
                        }

                        IconButton(
                            onClick = {
                                val currIdx = getCurrentParaIdx()
                                val existing = annotationsByParagraph[currIdx]?.find {
                                    it.type == "BOOKMARK"
                                }
                                if (existing != null) {
                                    viewModel.deleteAnnotation(existing.id)
                                    viewModel.showToastMessage("تمت إزالة العلامة")
                                } else {
                                    val snippet = paragraphs.getOrNull(currIdx)?.take(90) ?: ""
                                    viewModel.addAnnotation(
                                        chapterId = chapter.id,
                                        novelId = chapter.novelId,
                                        type = "BOOKMARK",
                                        paragraphIndex = currIdx,
                                        startOffset = 0,
                                        endOffset = snippet.length,
                                        selectedText = snippet,
                                        noteText = "علامة عند فقرة ${currIdx + 1}",
                                        colorHex = "#FDE047"
                                    )
                                }
                            },
                            modifier = Modifier.testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "علامة",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box {
                            IconButton(onClick = { showTopOverflowMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "المزيد")
                            }
                            DropdownMenu(
                                expanded = showTopOverflowMenu,
                                onDismissRequest = { showTopOverflowMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("تعديل النص") },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                    onClick = {
                                        showTopOverflowMenu = false
                                        viewModel.closeReader()
                                        viewModel.openSubScreen(SubScreen.EDITOR, chapter.id)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("بحث واستبدال") },
                                    leadingIcon = { Icon(Icons.Default.FindReplace, contentDescription = null) },
                                    onClick = {
                                        showTopOverflowMenu = false
                                        viewModel.closeReader()
                                        viewModel.openSubScreen(SubScreen.FIND_REPLACE, chapter.id)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("القاموس") },
                                    leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null) },
                                    onClick = {
                                        showTopOverflowMenu = false
                                        viewModel.closeReader()
                                        viewModel.openSubScreen(SubScreen.GLOSSARY)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("المحفوظات") },
                                    leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                                    onClick = {
                                        showTopOverflowMenu = false
                                        viewModel.closeReader()
                                        viewModel.openSubScreen(SubScreen.ANNOTATIONS)
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = showInTextSearch) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            OutlinedTextField(
                                value = inTextSearchQuery,
                                onValueChange = {
                                    inTextSearchQuery = it
                                    currentSearchMatchIdx = 0
                                },
                                placeholder = { Text("ابحث في الفصل...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (searchMatchingParagraphIndices.isEmpty()) "0/0"
                                else "${currentSearchMatchIdx + 1}/${searchMatchingParagraphIndices.size}",
                                style = MaterialTheme.typography.labelLarge
                            )
                            IconButton(
                                onClick = {
                                    if (searchMatchingParagraphIndices.isNotEmpty()) {
                                        currentSearchMatchIdx = (currentSearchMatchIdx + 1) % searchMatchingParagraphIndices.size
                                        val targetPara = searchMatchingParagraphIndices[currentSearchMatchIdx]
                                        scrollToParagraph(targetPara)
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "التالي")
                            }
                            IconButton(onClick = {
                                showInTextSearch = false
                                inTextSearchQuery = ""
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق")
                            }
                        }
                    }
                }
            }
        }

        // Bottom Bar (Appears on center tap)
        AnimatedVisibility(
            visible = barsVisible && effectiveSettings.showBottomBarInReader,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            val activeParaIdx = getCurrentParaIdx()
            val progressPct = if (paragraphs.size <= 1) 100 else ((activeParaIdx * 100) / (paragraphs.size - 1)).coerceIn(0, 100)

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Text-to-Speech (TTS) Audio Player Strip
                    AnimatedVisibility(visible = showTtsControls) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isTtsPlaying) "🔊 يقرأ فقرة ${ttsSpeakingParagraphIndex + 1}..."
                                        else "🔊 الاستماع الصوتي",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row {
                                        IconButton(
                                            onClick = {
                                                if (isTtsPlaying) {
                                                    ttsEngine?.stop()
                                                    isTtsPlaying = false
                                                } else {
                                                    val startIdx = getCurrentParaIdx()
                                                    ttsSpeakingParagraphIndex = startIdx
                                                    isTtsPlaying = true
                                                    ttsEngine?.setSpeechRate(ttsSpeed)
                                                    ttsEngine?.setPitch(ttsPitch)
                                                    ttsEngine?.speak(
                                                        paragraphs.getOrElse(startIdx) { "" },
                                                        TextToSpeech.QUEUE_FLUSH,
                                                        null,
                                                        "para_$startIdx"
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = if (isTtsPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "تشغيل/إيقاف"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                ttsEngine?.stop()
                                                isTtsPlaying = false
                                                ttsSpeakingParagraphIndex = -1
                                            }
                                        ) {
                                            Icon(Icons.Default.Stop, contentDescription = "إيقاف")
                                        }
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("السرعة: ${String.format(Locale.US, "%.1fx", ttsSpeed)}", style = MaterialTheme.typography.labelSmall)
                                    Slider(
                                        value = ttsSpeed,
                                        onValueChange = {
                                            ttsSpeed = it
                                            ttsEngine?.setSpeechRate(it)
                                        },
                                        valueRange = 0.5f..2.0f,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AssistChip(
                                        onClick = {
                                            ttsSleepTimerMinutes = when (ttsSleepTimerMinutes) {
                                                0 -> 15
                                                15 -> 30
                                                30 -> 60
                                                else -> 0
                                            }
                                        },
                                        label = {
                                            Text(
                                                if (ttsSleepTimerMinutes == 0) "مؤقت: متوقف"
                                                else "$ttsSleepTimerMinutes د"
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Draggable Chapter Progress Slider (Chapter Progress ONLY — no time stats!)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$progressPct%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = activeParaIdx.toFloat(),
                            onValueChange = { newIdxFloat ->
                                val target = newIdxFloat.toInt().coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
                                scrollToParagraph(target)
                            },
                            valueRange = 0f..(paragraphs.size - 1).coerceAtLeast(1).toFloat(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .testTag("reader_progress_slider")
                        )
                        Text(
                            text = "${activeParaIdx + 1} / ${paragraphs.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Bottom Action Buttons: Prev Chapter | Index | TTS | ⚙ Settings | Next Chapter
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                if (previousChapter != null) {
                                    viewModel.openChapterForReading(previousChapter.id)
                                } else {
                                    viewModel.showToastMessage("هذا هو الفصل الأول")
                                }
                            },
                            enabled = previousChapter != null
                        ) {
                            Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "الفصل السابق")
                        }

                        IconButton(
                            onClick = { showChaptersDrawer = true },
                            modifier = Modifier.testTag("reader_index_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = "الفصول")
                        }

                        IconButton(
                            onClick = { showTtsControls = !showTtsControls },
                            modifier = Modifier.testTag("reader_tts_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "استماع",
                                tint = if (isTtsPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { showLiveSettingsSheet = true },
                            modifier = Modifier.testTag("reader_settings_button")
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "المظهر والخط")
                        }

                        IconButton(
                            onClick = {
                                if (nextChapter != null) {
                                    viewModel.openChapterForReading(nextChapter.id)
                                } else {
                                    viewModel.showToastMessage("وصلت إلى آخر فصل")
                                }
                            },
                            enabled = nextChapter != null
                        ) {
                            Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "الفصل التالي")
                        }
                    }
                }
            }
        }
    }

    // Add Note Dialog for Selected Paragraph
    val noteParaIdx = showAddNoteDialogForParagraph
    if (noteParaIdx != null) {
        var noteText by remember { mutableStateOf("") }
        val paraSnippet = paragraphs.getOrElse(noteParaIdx) { "" }
        AlertDialog(
            onDismissRequest = { showAddNoteDialogForParagraph = null },
            title = { Text("إضافة ملاحظة على الفقرة #${noteParaIdx + 1}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "«${paraSnippet.take(100)}...»",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("ملاحظة المترجم / القارئ") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addAnnotation(
                            chapterId = chapter.id,
                            novelId = chapter.novelId,
                            type = "NOTE",
                            paragraphIndex = noteParaIdx,
                            startOffset = 0,
                            endOffset = paraSnippet.length,
                            selectedText = paraSnippet,
                            noteText = noteText.trim(),
                            colorHex = "#93C5FD"
                        )
                        showAddNoteDialogForParagraph = null
                        selectedParagraphIndex = null
                    }
                ) {
                    Text("حفظ الملاحظة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialogForParagraph = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Glossary Term Quick Popup
    val glossaryItem = selectedGlossaryPopup
    if (glossaryItem != null) {
        AlertDialog(
            onDismissRequest = { selectedGlossaryPopup = null },
            title = { Text("مصطلح من القاموس: ${glossaryItem.term}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("الترجمة المعتمدة: ${glossaryItem.translation}", fontWeight = FontWeight.Bold)
                    Text("التصنيف: ${glossaryItem.category}")
                    if (glossaryItem.note.isNotBlank()) {
                        Text("ملاحظة: ${glossaryItem.note}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedGlossaryPopup = null }) {
                    Text("حسناً")
                }
            }
        )
    }

    // Chapters Index BottomSheet
    if (showChaptersDrawer) {
        ModalBottomSheet(onDismissRequest = { showChaptersDrawer = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = "فهرس الفصول (${novelChapters.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxHeight(0.65f)
                ) {
                    itemsIndexed(novelChapters, key = { _, item -> item.id }) { idx, item ->
                        val isCurrent = item.id == chapter.id
                        Card(
                            onClick = {
                                showChaptersDrawer = false
                                viewModel.openChapterForReading(item.id)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${idx + 1}. ${item.title}",
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.progressPercent.toInt()}%",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Live Settings BottomSheet with Expandable Menus & Safe Navigation Bar Insets
    if (showLiveSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showLiveSettingsSheet = false }) {
            LiveReaderSettingsSheetContent(
                settings = effectiveSettings,
                novel = novel,
                hasNovelOverride = !novel?.settingsOverrideJson.isNullOrBlank(),
                onUpdateSettings = { updated ->
                    if (novel != null && !novel.settingsOverrideJson.isNullOrBlank()) {
                        viewModel.saveNovelSettingsOverride(novel.id, updated)
                    } else {
                        viewModel.updateSettings { updated }
                    }
                },
                onToggleNovelOverride = { enableOverride ->
                    if (novel != null) {
                        viewModel.saveNovelSettingsOverride(
                            novel.id,
                            if (enableOverride) effectiveSettings else null
                        )
                    }
                },
                onOpenFullSettings = {
                    showLiveSettingsSheet = false
                    onOpenAllSettings()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChapterEdgeProgressOverlay(
    currentScrollParaState: androidx.compose.runtime.IntState,
    pagerState: androidx.compose.foundation.pager.PagerState,
    readingMode: String,
    paragraphsCount: Int,
    paragraphsPerPage: Int,
    showCounterText: Boolean,
    readerBgColor: Color,
    readerTextColor: Color,
    modifier: Modifier = Modifier
) {
    // Reading scroll state inside this tiny leaf composable means ONLY this 20dp bar recomposes on scroll,
    // leaving the entire ReaderScreen untouched for 60fps performance on Galaxy M02s!
    val currentParaIdx = if (readingMode == "PAGES") {
        (pagerState.currentPage * paragraphsPerPage).coerceIn(0, (paragraphsCount - 1).coerceAtLeast(0))
    } else {
        currentScrollParaState.intValue.coerceIn(0, (paragraphsCount - 1).coerceAtLeast(0))
    }
    val progressFraction = if (paragraphsCount <= 1) 1f else (currentParaIdx.toFloat() / (paragraphsCount - 1).toFloat()).coerceIn(0f, 1f)
    val progressPct = (progressFraction * 100f).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(readerBgColor.copy(alpha = 0.94f))
            .navigationBarsPadding()
    ) {
        if (showCounterText) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "فقرة ${currentParaIdx + 1} من $paragraphsCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = readerTextColor.copy(alpha = 0.65f)
                )
                Text(
                    text = "$progressPct%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = readerTextColor.copy(alpha = 0.75f)
                )
            }
        }
        // Lightweight static Box progress bar (avoids Material3 LinearProgressIndicator Canvas animations on scroll)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(readerTextColor.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

/**
 * Hardware-Accelerated Native Android BaseAdapter with ViewHolder recycling for locked 60fps scrolling on Galaxy M02s.
 */
private class ReaderNativeAdapter(
    var paragraphs: List<String>,
    var chapter: ChapterEntity,
    var nextChapter: ChapterEntity?,
    var previousChapter: ChapterEntity?,
    var settings: ReaderSettings,
    var typeface: Typeface,
    var textColorInt: Int,
    var bgColorInt: Int,
    var primaryColorInt: Int,
    var annotationsByPara: Map<Int, List<AnnotationEntity>>,
    var glossaryTerms: List<GlossaryEntity>,
    var searchQuery: String,
    var ttsSpeakingIdx: Int,
    var selectedParaIdx: Int?,
    val onOpenChapter: (Long) -> Unit,
    val onChangeStage: (String) -> Unit
) : BaseAdapter() {

    private class ParagraphViewHolder(
        val textView: TextView,
        var appliedStyleSignature: Int = 0
    )

    private var currentStyleSignature: Int = computeStyleSignature()

    private fun computeStyleSignature(): Int {
        var result = settings.fontFamily.hashCode()
        result = 31 * result + settings.fontSizeSp.hashCode()
        result = 31 * result + settings.isBoldWeight.hashCode()
        result = 31 * result + settings.lineHeightMultiplier.hashCode()
        result = 31 * result + settings.letterSpacingSp.hashCode()
        result = 31 * result + settings.textAlign.hashCode()
        result = 31 * result + settings.paragraphSpacingDp.hashCode()
        result = 31 * result + settings.emptyLineBetweenParagraphs.hashCode()
        result = 31 * result + textColorInt
        return result
    }

    fun updateData(
        newParagraphs: List<String>,
        newChapter: ChapterEntity,
        newNextChapter: ChapterEntity?,
        newPreviousChapter: ChapterEntity?,
        newSettings: ReaderSettings,
        newTypeface: Typeface,
        newTextColorInt: Int,
        newBgColorInt: Int,
        newPrimaryColorInt: Int,
        newAnnotationsByPara: Map<Int, List<AnnotationEntity>>,
        newGlossaryTerms: List<GlossaryEntity>,
        newSearchQuery: String,
        newTtsSpeakingIdx: Int,
        newSelectedParaIdx: Int?
    ): Boolean {
        val oldSig = currentStyleSignature
        val changed = paragraphs !== newParagraphs ||
            chapter.id != newChapter.id ||
            chapter.translatorStage != newChapter.translatorStage ||
            nextChapter?.id != newNextChapter?.id ||
            previousChapter?.id != newPreviousChapter?.id ||
            annotationsByPara != newAnnotationsByPara ||
            glossaryTerms != newGlossaryTerms ||
            searchQuery != newSearchQuery ||
            ttsSpeakingIdx != newTtsSpeakingIdx ||
            selectedParaIdx != newSelectedParaIdx ||
            settings.firstLineIndentDp != newSettings.firstLineIndentDp

        paragraphs = newParagraphs
        chapter = newChapter
        nextChapter = newNextChapter
        previousChapter = newPreviousChapter
        settings = newSettings
        typeface = newTypeface
        textColorInt = newTextColorInt
        bgColorInt = newBgColorInt
        primaryColorInt = newPrimaryColorInt
        annotationsByPara = newAnnotationsByPara
        glossaryTerms = newGlossaryTerms
        searchQuery = newSearchQuery
        ttsSpeakingIdx = newTtsSpeakingIdx
        selectedParaIdx = newSelectedParaIdx
        currentStyleSignature = computeStyleSignature()

        return changed || oldSig != currentStyleSignature
    }

    override fun getCount(): Int = paragraphs.size + 1

    override fun getItem(position: Int): Any = if (position < paragraphs.size) paragraphs[position] else "FOOTER"

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int = if (position < paragraphs.size) 0 else 1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        if (getItemViewType(position) == 1) {
            return buildFooterView(context)
        }

        val holder: ParagraphViewHolder
        val view: TextView
        if (convertView == null || convertView.tag !is ParagraphViewHolder) {
            view = TextView(context).apply {
                layoutParams = AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                includeFontPadding = false
                isClickable = false
                isFocusable = false
                isLongClickable = false
                textDirection = View.TEXT_DIRECTION_ANY_RTL
            }
            holder = ParagraphViewHolder(view, 0)
            view.tag = holder
        } else {
            view = convertView as TextView
            holder = view.tag as ParagraphViewHolder
        }

        if (holder.appliedStyleSignature != currentStyleSignature) {
            applyStyleToTextView(context, view)
            holder.appliedStyleSignature = currentStyleSignature
        }

        val rawPara = paragraphs[position]
        val paraText = if (settings.firstLineIndentDp > 0.5f) "    $rawPara" else rawPara
        val anns = annotationsByPara[position]
        val isTts = ttsSpeakingIdx == position
        val isSel = selectedParaIdx == position
        val hasSearch = searchQuery.isNotBlank() && paraText.contains(searchQuery, ignoreCase = true)
        val hasGlossary = glossaryTerms.isNotEmpty() && glossaryTerms.any { it.term.isNotBlank() && paraText.contains(it.term) }

        if (anns.isNullOrEmpty() && !isTts && !isSel && !hasSearch && !hasGlossary) {
            view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            view.text = paraText
        } else {
            when {
                isTts -> view.setBackgroundColor(android.graphics.Color.argb(45, 217, 119, 6))
                isSel -> view.setBackgroundColor(android.graphics.Color.argb(55, 59, 130, 246))
                else -> view.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
            val sb = SpannableStringBuilder(paraText)
            if (hasGlossary) {
                for (g in glossaryTerms) {
                    if (g.term.isNotBlank()) {
                        var startIdx = paraText.indexOf(g.term)
                        val hlCol = parseAndroidColorSafe(g.highlightColorHex, 0x66FDE047)
                        while (startIdx >= 0) {
                            val endIdx = startIdx + g.term.length
                            sb.setSpan(BackgroundColorSpan(hlCol), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            sb.setSpan(StyleSpan(Typeface.BOLD), startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            startIdx = paraText.indexOf(g.term, endIdx)
                        }
                    }
                }
            }
            if (!anns.isNullOrEmpty()) {
                for (ann in anns) {
                    val col = parseAndroidColorSafe(ann.colorHex, 0x77FDE047)
                    sb.setSpan(BackgroundColorSpan(col), 0, paraText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                for (ann in anns) {
                    if (ann.noteText.isNotBlank()) {
                        val noteStart = sb.length
                        sb.append("\n📝 ${ann.noteText}")
                        sb.setSpan(StyleSpan(Typeface.BOLD), noteStart, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            if (hasSearch) {
                var idx = paraText.indexOf(searchQuery, ignoreCase = true)
                while (idx >= 0) {
                    val end = idx + searchQuery.length
                    sb.setSpan(BackgroundColorSpan(0xFFF59E0B.toInt()), idx, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    sb.setSpan(ForegroundColorSpan(android.graphics.Color.BLACK), idx, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    sb.setSpan(StyleSpan(Typeface.BOLD), idx, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    idx = paraText.indexOf(searchQuery, end, ignoreCase = true)
                }
            }
            view.text = sb
        }

        return view
    }

    private fun applyStyleToTextView(context: Context, tv: TextView) {
        val density = context.resources.displayMetrics.density
        val spacingDp = if (settings.emptyLineBetweenParagraphs) settings.paragraphSpacingDp else (settings.paragraphSpacingDp / 3f)
        val halfPadPx = ((spacingDp * density) / 2f).toInt().coerceAtLeast(2)

        tv.setPadding(0, halfPadPx, 0, halfPadPx)
        tv.typeface = typeface
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.fontSizeSp)
        tv.setTextColor(textColorInt)
        tv.setLineSpacing(0f, settings.lineHeightMultiplier)
        tv.letterSpacing = (settings.letterSpacingSp / settings.fontSizeSp.coerceAtLeast(12f)).coerceIn(-0.05f, 0.3f)

        when (settings.textAlign) {
            "LEFT" -> {
                tv.gravity = Gravity.LEFT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.justificationMode = Layout.JUSTIFICATION_MODE_NONE
                }
            }
            "CENTER" -> {
                tv.gravity = Gravity.CENTER_HORIZONTAL
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.justificationMode = Layout.JUSTIFICATION_MODE_NONE
                }
            }
            "JUSTIFY" -> {
                tv.gravity = Gravity.RIGHT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
                }
            }
            else -> {
                tv.gravity = Gravity.RIGHT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.justificationMode = Layout.JUSTIFICATION_MODE_NONE
                }
            }
        }
    }

    private fun buildFooterView(context: Context): View {
        val density = context.resources.displayMetrics.density
        val pad20 = (20 * density).toInt()
        val pad12 = (12 * density).toInt()
        val pad8 = (8 * density).toInt()

        val cardLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(pad20, (28 * density).toInt(), pad20, (32 * density).toInt())
        }

        val innerBox = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(pad20, pad20, pad20, pad20)
            background = GradientDrawable().apply {
                cornerRadius = 20f * density
                setColor(android.graphics.Color.argb(32, 128, 128, 128))
            }
        }

        val titleTv = TextView(context).apply {
            text = "نهاية «${chapter.title}»"
            setTextColor(textColorInt)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }
        innerBox.addView(titleTv)

        val stagesRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, pad12, 0, pad12)
        }
        listOf("DRAFT" to "مسودة", "REVIEWED" to "مُراجَع", "FINAL" to "نهائي").forEach { (stage, label) ->
            val selected = chapter.translatorStage == stage
            val chipTv = TextView(context).apply {
                text = label
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTextColor(if (selected) android.graphics.Color.WHITE else textColorInt)
                setPadding(pad12, pad8, pad12, pad8)
                background = GradientDrawable().apply {
                    cornerRadius = 16f * density
                    setColor(if (selected) primaryColorInt else android.graphics.Color.argb(40, 128, 128, 128))
                }
                setOnClickListener { onChangeStage(stage) }
            }
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (4 * density).toInt()
                marginEnd = (4 * density).toInt()
            }
            stagesRow.addView(chipTv, lp)
        }
        innerBox.addView(stagesRow)

        val next = nextChapter
        if (next != null) {
            val nextBtn = TextView(context).apply {
                text = "الفصل التالي: ${next.title}"
                setTextColor(android.graphics.Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setPadding(pad20, pad12, pad20, pad12)
                background = GradientDrawable().apply {
                    cornerRadius = 24f * density
                    setColor(primaryColorInt)
                }
                setOnClickListener { onOpenChapter(next.id) }
            }
            val nextLp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = pad8
            }
            innerBox.addView(nextBtn, nextLp)
        } else {
            val doneTv = TextView(context).apply {
                text = "وصلت إلى آخر فصل في هذه المجموعة"
                setTextColor(textColorInt)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                gravity = Gravity.CENTER
                setPadding(0, pad8, 0, pad8)
            }
            innerBox.addView(doneTv)
        }

        val prev = previousChapter
        if (prev != null) {
            val prevBtn = TextView(context).apply {
                text = "الفصل السابق: ${prev.title}"
                setTextColor(primaryColorInt)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setPadding(pad12, pad12, pad12, pad8)
                setOnClickListener { onOpenChapter(prev.id) }
            }
            innerBox.addView(prevBtn)
        }

        cardLayout.addView(
            innerBox,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        return cardLayout
    }

    private fun parseAndroidColorSafe(hex: String, fallback: Int): Int {
        return try {
            val raw = android.graphics.Color.parseColor(hex)
            android.graphics.Color.argb(110, android.graphics.Color.red(raw), android.graphics.Color.green(raw), android.graphics.Color.blue(raw))
        } catch (_: Exception) {
            fallback
        }
    }
}

@Composable
private fun ReaderParagraphItem(
    paragraphIndex: Int,
    paragraphText: String,
    textStyle: TextStyle,
    annotations: List<AnnotationEntity>,
    glossaryTerms: List<GlossaryEntity>,
    searchQuery: String,
    isTtsSpeaking: Boolean,
    isSelected: Boolean
) {
    val hasFormatting = remember(paragraphText, annotations, glossaryTerms, searchQuery) {
        annotations.isNotEmpty() ||
            (searchQuery.isNotBlank() && paragraphText.contains(searchQuery, ignoreCase = true)) ||
            (glossaryTerms.isNotEmpty() && glossaryTerms.any { it.term.isNotBlank() && paragraphText.contains(it.term) })
    }

    // Ultra-fast path for 99% of normal paragraphs: a single flat Text node without extra Column wrapper!
    if (!hasFormatting && !isTtsSpeaking && !isSelected && annotations.isEmpty()) {
        Text(
            text = paragraphText,
            style = textStyle,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }

    val containerBg = when {
        isTtsSpeaking -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        isSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (containerBg != Color.Transparent) {
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(containerBg)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                } else {
                    Modifier
                }
            )
    ) {
        if (!hasFormatting) {
            Text(
                text = paragraphText,
                style = textStyle,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            val annotatedText = remember(paragraphText, annotations, glossaryTerms, searchQuery) {
                buildAnnotatedString {
                    append(paragraphText)

                    glossaryTerms.forEach { g ->
                        if (g.term.isNotBlank()) {
                            var startIndex = paragraphText.indexOf(g.term)
                            while (startIndex >= 0) {
                                val endIndex = startIndex + g.term.length
                                addStyle(
                                    style = SpanStyle(
                                        background = parseColorSafe(g.highlightColorHex, Color(0xFFFDE047)).copy(alpha = 0.35f),
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    start = startIndex,
                                    end = endIndex
                                )
                                startIndex = paragraphText.indexOf(g.term, endIndex)
                            }
                        }
                    }

                    annotations.forEach { ann ->
                        val col = parseColorSafe(ann.colorHex, Color(0xFFFDE047)).copy(alpha = 0.45f)
                        addStyle(
                            style = SpanStyle(background = col),
                            start = 0,
                            end = paragraphText.length
                        )
                    }

                    if (searchQuery.isNotBlank()) {
                        var idx = paragraphText.indexOf(searchQuery, ignoreCase = true)
                        while (idx >= 0) {
                            val end = idx + searchQuery.length
                            addStyle(
                                style = SpanStyle(
                                    background = Color(0xFFF59E0B),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                ),
                                start = idx,
                                end = end
                            )
                            idx = paragraphText.indexOf(searchQuery, end, ignoreCase = true)
                        }
                    }
                }
            }
            Text(
                text = annotatedText,
                style = textStyle,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (annotations.isNotEmpty()) {
            annotations.filter { it.noteText.isNotBlank() }.forEach { noteAnn ->
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "📝 ${noteAnn.noteText}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EndOfChapterFooterCard(
    chapter: ChapterEntity,
    nextChapter: ChapterEntity?,
    previousChapter: ChapterEntity?,
    autoOpenNext: Boolean,
    onToggleAutoOpenNext: (Boolean) -> Unit,
    onOpenChapter: (Long) -> Unit,
    onChangeTranslatorStage: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "نهاية «${chapter.title}»",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                listOf("DRAFT" to "مسودة", "REVIEWED" to "مُراجَع", "FINAL" to "نهائي").forEach { (stage, label) ->
                    FilterChip(
                        selected = chapter.translatorStage == stage,
                        onClick = { onChangeTranslatorStage(stage) },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (nextChapter != null) {
                Button(
                    onClick = { onOpenChapter(nextChapter.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("next_chapter_footer_button")
                ) {
                    Text("الفصل التالي: ${nextChapter.title}")
                }
            } else {
                Text(
                    text = "وصلت إلى آخر فصل في هذه المجموعة",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (previousChapter != null) {
                TextButton(onClick = { onOpenChapter(previousChapter.id) }) {
                    Text("الفصل السابق: ${previousChapter.title}")
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Switch(
                    checked = autoOpenNext,
                    onCheckedChange = onToggleAutoOpenNext
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "فتح الفصل التالي تلقائيًا",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LiveReaderSettingsSheetContent(
    settings: ReaderSettings,
    novel: NovelEntity?,
    hasNovelOverride: Boolean,
    onUpdateSettings: (ReaderSettings) -> Unit,
    onToggleNovelOverride: (Boolean) -> Unit,
    onOpenFullSettings: () -> Unit
) {
    var expandedSection by remember { mutableStateOf("FONT") }
    var showFontDropdown by remember { mutableStateOf(false) }

    val currentFontLabel = remember(settings.fontFamily) {
        AvailableReaderFonts.find { it.first == settings.fontFamily }?.second ?: settings.fontFamily
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "إعدادات القراءة السريعة",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onOpenFullSettings) {
                Text("كل الإعدادات")
            }
        }

        if (novel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "تخصيص لرواية «${novel.title}» فقط",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = hasNovelOverride,
                    onCheckedChange = onToggleNovelOverride
                )
            }
        }

        // Menu 1: الخط والحجم
        ReaderExpandableMenuCard(
            title = "الخط والحجم ($currentFontLabel • ${settings.fontSizeSp.toInt()})",
            expanded = expandedSection == "FONT",
            onToggle = { expandedSection = if (expandedSection == "FONT") "" else "FONT" }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showFontDropdown = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(currentFontLabel, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ExpandMore, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = showFontDropdown,
                    onDismissRequest = { showFontDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    AvailableReaderFonts.forEach { (fontKey, fontLabel) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = fontLabel,
                                    fontWeight = if (settings.fontFamily == fontKey) FontWeight.Bold else FontWeight.Normal,
                                    color = if (settings.fontFamily == fontKey) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onUpdateSettings(settings.copy(fontFamily = fontKey))
                                showFontDropdown = false
                            }
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("الحجم: ${settings.fontSizeSp.toInt()}", modifier = Modifier.width(72.dp))
                Slider(
                    value = settings.fontSizeSp,
                    onValueChange = { onUpdateSettings(settings.copy(fontSizeSp = it)) },
                    valueRange = 14f..36f,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = settings.isBoldWeight,
                    onClick = { onUpdateSettings(settings.copy(isBoldWeight = !settings.isBoldWeight)) },
                    label = { Text("عريض") }
                )
            }
        }

        // Menu 2: الألوان والإضاءة
        ReaderExpandableMenuCard(
            title = "الألوان وراحة العين",
            expanded = expandedSection == "THEME",
            onToggle = { expandedSection = if (expandedSection == "THEME") "" else "THEME" }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                val themes = listOf(
                    "SEPIA" to "سيبيا مريح",
                    "PAPER" to "ورقي",
                    "LIGHT" to "فاتح",
                    "DARK" to "داكن",
                    "AMOLED" to "أسود"
                )
                themes.forEach { (mode, label) ->
                    FilterChip(
                        selected = settings.themeMode == mode,
                        onClick = { onUpdateSettings(settings.copy(themeMode = mode)) },
                        label = { Text(label) }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "دفء الشاشة:",
                    modifier = Modifier.width(90.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = settings.warmEyeFilterIntensity,
                    onValueChange = { onUpdateSettings(settings.copy(warmEyeFilterIntensity = it)) },
                    valueRange = 0f..0.5f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Menu 3: المسافات والمحاذاة
        ReaderExpandableMenuCard(
            title = "تباعد الأسطر والمحاذاة",
            expanded = expandedSection == "TEXT",
            onToggle = { expandedSection = if (expandedSection == "TEXT") "" else "TEXT" }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "تباعد الأسطر: ${String.format(Locale.US, "%.1f", settings.lineHeightMultiplier)}",
                    modifier = Modifier.width(105.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = settings.lineHeightMultiplier,
                    onValueChange = { onUpdateSettings(settings.copy(lineHeightMultiplier = it)) },
                    valueRange = 1.2f..2.6f,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("المحاذاة:", style = MaterialTheme.typography.bodySmall)
                Row {
                    IconButton(onClick = { onUpdateSettings(settings.copy(textAlign = "RIGHT")) }) {
                        Icon(Icons.AutoMirrored.Filled.FormatAlignRight, contentDescription = "يمين (سريع)")
                    }
                    IconButton(onClick = { onUpdateSettings(settings.copy(textAlign = "JUSTIFY")) }) {
                        Icon(Icons.Default.FormatAlignJustify, contentDescription = "ضبط")
                    }
                    IconButton(onClick = { onUpdateSettings(settings.copy(textAlign = "CENTER")) }) {
                        Icon(Icons.Default.FormatAlignCenter, contentDescription = "وسط")
                    }
                    IconButton(onClick = { onUpdateSettings(settings.copy(textAlign = "LEFT")) }) {
                        Icon(Icons.AutoMirrored.Filled.FormatAlignLeft, contentDescription = "يسار")
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = settings.hideTashkeel,
                    onClick = { onUpdateSettings(settings.copy(hideTashkeel = !settings.hideTashkeel)) },
                    label = { Text("إخفاء التشكيل") }
                )
                FilterChip(
                    selected = settings.highlightGlossaryInText,
                    onClick = { onUpdateSettings(settings.copy(highlightGlossaryInText = !settings.highlightGlossaryInText)) },
                    label = { Text("تلوين كلمات القاموس") }
                )
            }
        }

        // Menu 4: عداد الفصل والتمرير
        ReaderExpandableMenuCard(
            title = "عدّاد الفصل ونمط القراءة",
            expanded = expandedSection == "BEHAVIOR",
            onToggle = { expandedSection = if (expandedSection == "BEHAVIOR") "" else "BEHAVIOR" }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = settings.readingMode == "SCROLL",
                    onClick = { onUpdateSettings(settings.copy(readingMode = "SCROLL")) },
                    label = { Text("تمرير عمودي") }
                )
                FilterChip(
                    selected = settings.readingMode == "PAGES",
                    onClick = { onUpdateSettings(settings.copy(readingMode = "PAGES")) },
                    label = { Text("صفحات") }
                )
                FilterChip(
                    selected = settings.showThinEdgeProgress,
                    onClick = { onUpdateSettings(settings.copy(showThinEdgeProgress = !settings.showThinEdgeProgress)) },
                    label = { Text("شريط عداد الفصل") }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReaderExpandableMenuCard(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (expanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    HorizontalDivider()
                    content()
                }
            }
        }
    }
}
