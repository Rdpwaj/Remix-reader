package com.example.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.HapticFeedbackConstants
import android.view.WindowManager
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

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialParagraphIdx.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
    )

    // Ensure position jumps accurately if async paragraph splitting finishes for huge files
    LaunchedEffect(paragraphs.size) {
        if (paragraphs.size > 1 && initialParagraphIdx > 0 && listState.firstVisibleItemIndex == 0) {
            listState.scrollToItem(initialParagraphIdx.coerceIn(0, paragraphs.size - 1))
        }
    }

    val paragraphsPerPage = 6
    val totalPages = max(1, (paragraphs.size + paragraphsPerPage - 1) / paragraphsPerPage)
    val pagerState = rememberPagerState(
        initialPage = (initialParagraphIdx / paragraphsPerPage).coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )

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
            listState.firstVisibleItemIndex.coerceIn(0, (paragraphs.size - 1).coerceAtLeast(0))
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
                                    listState.animateScrollToItem(nextIdx)
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

    // Debounced Background Position Saving (Zero scroll lag on Galaxy M02s!)
    LaunchedEffect(chapter.id, paragraphs.size, effectiveSettings.readingMode) {
        snapshotFlow {
            if (effectiveSettings.readingMode == "PAGES") {
                pagerState.currentPage * paragraphsPerPage
            } else {
                listState.firstVisibleItemIndex
            }
        }
            .distinctUntilChanged()
            .debounce(2500L)
            .collectLatest { paraIdx ->
                if (paragraphs.size > 1) {
                    val safeIdx = paraIdx.coerceIn(0, paragraphs.size - 1)
                    val approxCharOffset = (listState.firstVisibleItemScrollOffset / 4).coerceAtLeast(0)
                    viewModel.saveReadingPosition(
                        chapterId = chapter.id,
                        paragraphIndex = safeIdx,
                        charOffset = approxCharOffset,
                        totalParagraphs = paragraphs.size
                    )
                }
            }
    }

    // Save position immediately on Pause / Stop / Exit / App Background
    DisposableEffect(lifecycleOwner, chapter.id, paragraphs.size) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                val idx = getCurrentParaIdx()
                val approxCharOffset = (listState.firstVisibleItemScrollOffset / 4).coerceAtLeast(0)
                viewModel.saveReadingPosition(
                    chapterId = chapter.id,
                    paragraphIndex = idx,
                    charOffset = approxCharOffset,
                    totalParagraphs = paragraphs.size
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            val idx = getCurrentParaIdx()
            val approxCharOffset = (listState.firstVisibleItemScrollOffset / 4).coerceAtLeast(0)
            viewModel.saveReadingPosition(
                chapterId = chapter.id,
                paragraphIndex = idx,
                charOffset = approxCharOffset,
                totalParagraphs = paragraphs.size
            )
        }
    }

    // Auto-Scroll Coroutine
    LaunchedEffect(effectiveSettings.autoScrollSpeed, effectiveSettings.readingMode, barsVisible) {
        if (effectiveSettings.autoScrollSpeed > 0f && effectiveSettings.readingMode == "SCROLL" && !barsVisible) {
            while (true) {
                listState.scrollBy(effectiveSettings.autoScrollSpeed * 1.4f)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBgColor)
            .pointerInput(effectiveSettings.touchZonesMode, effectiveSettings.readingMode) {
                detectTapGestures(
                    onTap = { offset ->
                        val width = size.width
                        val third = width / 3f
                        when {
                            effectiveSettings.touchZonesMode == "SIDES_FLIP" && offset.x < third -> {
                                coroutineScope.launch {
                                    if (effectiveSettings.readingMode == "PAGES") {
                                        val next = (pagerState.currentPage + 1).coerceAtMost(totalPages - 1)
                                        pagerState.animateScrollToPage(next)
                                    } else {
                                        val target = (getCurrentParaIdx() + 5).coerceAtMost(paragraphs.size - 1)
                                        listState.scrollToItem(target)
                                    }
                                }
                            }
                            effectiveSettings.touchZonesMode == "SIDES_FLIP" && offset.x > third * 2 -> {
                                coroutineScope.launch {
                                    if (effectiveSettings.readingMode == "PAGES") {
                                        val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                                        pagerState.animateScrollToPage(prev)
                                    } else {
                                        val target = (getCurrentParaIdx() - 5).coerceAtLeast(0)
                                        listState.scrollToItem(target)
                                    }
                                }
                            }
                            else -> {
                                if (selectedParagraphIndex != null) {
                                    selectedParagraphIndex = null
                                } else {
                                    barsVisible = !barsVisible
                                }
                            }
                        }
                    },
                    onLongPress = {
                        val curr = getCurrentParaIdx()
                        selectedParagraphIndex = if (selectedParagraphIndex == curr) null else curr
                    }
                )
            }
    ) {
        // Main Reading Area: Clipped cleanly below the shaded system status bar so text NEVER overlaps clock/battery/network icons
        if (effectiveSettings.readingMode == "PAGES") {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
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
            // Ultra-Fast Virtualized LazyColumn optimized for Galaxy M02s (60fps)
            // statusBarsPadding() on the LazyColumn modifier ensures scrolling text is clipped strictly below the status bar!
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = effectiveSettings.marginHorizontalDp.dp,
                    end = effectiveSettings.marginHorizontalDp.dp,
                    top = effectiveSettings.marginVerticalDp.dp + 8.dp,
                    bottom = effectiveSettings.marginVerticalDp.dp + 56.dp
                ),
                verticalArrangement = Arrangement.spacedBy(paragraphSpacing),
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .testTag("reader_lazy_column")
            ) {
                itemsIndexed(
                    items = paragraphs,
                    key = { idx, _ -> idx },
                    contentType = { _, _ -> 0 }
                ) { idx, paraText ->
                    // Zero-overhead fast path: renders a single flat Text node directly when no special formatting applies
                    if (!hasGlobalFormatting && ttsSpeakingParagraphIndex != idx && selectedParagraphIndex != idx) {
                        Text(
                            text = paraText,
                            style = paragraphTextStyle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ReaderParagraphItem(
                            paragraphIndex = idx,
                            paragraphText = paraText,
                            textStyle = paragraphTextStyle,
                            annotations = annotationsByParagraph[idx] ?: emptyList(),
                            glossaryTerms = activeGlossary,
                            searchQuery = inTextSearchQuery,
                            isTtsSpeaking = ttsSpeakingParagraphIndex == idx,
                            isSelected = selectedParagraphIndex == idx
                        )
                    }
                }

                // End of Chapter Footer Card
                item(key = "chapter_footer", contentType = 1) {
                    EndOfChapterFooterCard(
                        chapter = chapter,
                        nextChapter = nextChapter,
                        previousChapter = previousChapter,
                        autoOpenNext = effectiveSettings.autoOpenNextChapter,
                        onToggleAutoOpenNext = { enabled ->
                            viewModel.updateSettings { it.copy(autoOpenNextChapter = enabled) }
                        },
                        onOpenChapter = { targetId ->
                            viewModel.saveReadingPosition(chapter.id, paragraphs.size - 1, 0, paragraphs.size)
                            viewModel.openChapterForReading(targetId)
                        },
                        onChangeTranslatorStage = { newStage ->
                            viewModel.updateChapter(chapter.copy(translatorStage = newStage))
                        }
                    )
                }
            }
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
                listState = listState,
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
                            coroutineScope.launch {
                                if (effectiveSettings.readingMode == "PAGES") {
                                    pagerState.scrollToPage(0)
                                } else {
                                    listState.scrollToItem(0)
                                }
                                viewModel.saveReadingPosition(chapter.id, 0, 0, paragraphs.size)
                            }
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
                                        coroutineScope.launch { listState.scrollToItem(targetPara) }
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
                                coroutineScope.launch {
                                    if (effectiveSettings.readingMode == "PAGES") {
                                        pagerState.scrollToPage(target / paragraphsPerPage)
                                    } else {
                                        listState.scrollToItem(target)
                                    }
                                }
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
    listState: androidx.compose.foundation.lazy.LazyListState,
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
    // leaving the entire ReaderScreen and LazyColumn untouched for 60fps performance on Galaxy M02s!
    val currentParaIdx by remember(readingMode, paragraphsCount, paragraphsPerPage) {
        derivedStateOf {
            if (readingMode == "PAGES") {
                (pagerState.currentPage * paragraphsPerPage).coerceIn(0, (paragraphsCount - 1).coerceAtLeast(0))
            } else {
                listState.firstVisibleItemIndex.coerceIn(0, (paragraphsCount - 1).coerceAtLeast(0))
            }
        }
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
