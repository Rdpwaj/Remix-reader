package com.example.data

import android.content.Context
import android.util.LruCache
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "reader_preferences")

data class CachedUiSessionState(
    val currentTabName: String = "LIBRARY",
    val selectedNovelId: Long? = null,
    val statusFilter: String = "ALL",
    val activeChapterId: Long? = null
)

class ReaderRepository(
    private val context: Context,
    private val dao: AppDao
) {
    private val settingsKey = stringPreferencesKey("global_reader_settings_json")
    private val sessionStateKey = stringPreferencesKey("cached_ui_session_state_json")
    private val fastPrefs = context.getSharedPreferences("reader_fast_cache", Context.MODE_PRIVATE)

    // In-memory LRU cache of full chapters (with content) for 0ms instant opening
    private val hotChaptersRamCache = LruCache<Long, ChapterEntity>(8)

    private val _globalSettingsFlow = MutableStateFlow(loadInitialSettingsSync())
    val globalSettingsFlow: Flow<ReaderSettings> = _globalSettingsFlow.asStateFlow()

    val activeNovelsFlow: Flow<List<NovelEntity>> = dao.observeActiveNovels()
        .onEach { list -> saveCachedNovels(list) }
        .flowOn(Dispatchers.IO)

    val deletedNovelsFlow: Flow<List<NovelEntity>> = dao.observeDeletedNovels()
        .flowOn(Dispatchers.IO)

    val activeChaptersFlow: Flow<List<ChapterEntity>> = dao.observeActiveChapters()
        .onEach { list -> saveCachedChapters(list) }
        .flowOn(Dispatchers.IO)

    val deletedChaptersFlow: Flow<List<ChapterEntity>> = dao.observeDeletedChapters()
        .flowOn(Dispatchers.IO)
    val allAnnotationsFlow: Flow<List<AnnotationEntity>> = dao.observeAllAnnotations()
        .flowOn(Dispatchers.IO)
    val allGlossaryFlow: Flow<List<GlossaryEntity>> = dao.observeAllGlossary()
        .flowOn(Dispatchers.IO)
    val readingStatsFlow: Flow<List<ReadingStatsEntity>> = dao.observeReadingStats()
        .flowOn(Dispatchers.IO)

    init {
        // Pre-warm the hot chapter into RAM and paragraph LruCache on repository creation
        loadCachedHotChapterSync()?.let { hot ->
            hotChaptersRamCache.put(hot.id, hot)
            if (hot.content.isNotBlank() && hot.content.length < 80_000) {
                TextProcessingUtils.splitIntoParagraphs(hot.content, _globalSettingsFlow.value.hideTashkeel)
            }
        }
    }

    fun loadInitialSettingsSync(): ReaderSettings {
        val raw = fastPrefs.getString("global_reader_settings_json", null)
        return if (!raw.isNullOrBlank()) {
            ReaderSettings.fromJsonString(raw)
        } else {
            ReaderSettings(onboardingCompleted = true)
        }
    }

    fun loadUiSessionStateSync(): CachedUiSessionState {
        val raw = fastPrefs.getString("cached_ui_session_state_v1", null) ?: return CachedUiSessionState()
        return try {
            val o = JSONObject(raw)
            val novelId = o.optLong("selectedNovelId", -1L)
            val activeChId = o.optLong("activeChapterId", -1L)
            CachedUiSessionState(
                currentTabName = o.optString("currentTabName", "LIBRARY"),
                selectedNovelId = if (novelId <= 0L) null else novelId,
                statusFilter = o.optString("statusFilter", "ALL"),
                activeChapterId = if (activeChId <= 0L) null else activeChId
            )
        } catch (_: Exception) {
            CachedUiSessionState()
        }
    }

    fun saveUiSessionStateSync(
        currentTabName: String,
        selectedNovelId: Long?,
        statusFilter: String,
        activeChapterId: Long?
    ) {
        try {
            val o = JSONObject()
            o.put("currentTabName", currentTabName)
            o.put("selectedNovelId", selectedNovelId ?: -1L)
            o.put("statusFilter", statusFilter)
            o.put("activeChapterId", activeChapterId ?: -1L)
            val json = o.toString()
            fastPrefs.edit().putString("cached_ui_session_state_v1", json).apply()
        } catch (_: Exception) {}
    }

    fun loadCachedHotChapterSync(): ChapterEntity? {
        val raw = fastPrefs.getString("cached_hot_chapter_v1", null) ?: return null
        return try {
            val o = JSONObject(raw)
            val id = o.optLong("id", -1L)
            if (id <= 0L) return null
            val nId = o.optLong("novelId", -1L)
            ChapterEntity(
                id = id,
                novelId = if (nId <= 0L) null else nId,
                title = o.optString("title"),
                content = o.optString("content"),
                format = o.optString("format", "TXT"),
                wordCount = o.optInt("wordCount", 0),
                readingStatus = o.optString("readingStatus", "NEW"),
                translatorStage = o.optString("translatorStage", "DRAFT"),
                favorite = o.optBoolean("favorite", false),
                tags = o.optString("tags", ""),
                paragraphIndex = o.optInt("paragraphIndex", 0),
                charOffset = o.optInt("charOffset", 0),
                progressPercent = o.optDouble("progressPercent", 0.0).toFloat(),
                createdAt = o.optLong("createdAt"),
                modifiedAt = o.optLong("modifiedAt"),
                lastReadAt = o.optLong("lastReadAt", 0L)
            )
        } catch (_: Exception) {
            null
        }
    }

    fun getCachedHotChapter(chapterId: Long): ChapterEntity? {
        hotChaptersRamCache.get(chapterId)?.let { return it }
        val diskHot = loadCachedHotChapterSync()
        if (diskHot != null && diskHot.id == chapterId) {
            hotChaptersRamCache.put(chapterId, diskHot)
            return diskHot
        }
        return null
    }

    fun cacheHotChapterSync(chapter: ChapterEntity) {
        if (chapter.content.isEmpty()) return
        hotChaptersRamCache.put(chapter.id, chapter)
        // Only persist chapters under 250KB to SharedPreferences so disk flush is < 2ms
        if (chapter.content.length <= 250_000) {
            try {
                val o = JSONObject()
                o.put("id", chapter.id)
                o.put("novelId", chapter.novelId ?: -1L)
                o.put("title", chapter.title)
                o.put("content", chapter.content)
                o.put("format", chapter.format)
                o.put("wordCount", chapter.wordCount)
                o.put("readingStatus", chapter.readingStatus)
                o.put("translatorStage", chapter.translatorStage)
                o.put("favorite", chapter.favorite)
                o.put("tags", chapter.tags)
                o.put("paragraphIndex", chapter.paragraphIndex)
                o.put("charOffset", chapter.charOffset)
                o.put("progressPercent", chapter.progressPercent.toDouble())
                o.put("createdAt", chapter.createdAt)
                o.put("modifiedAt", chapter.modifiedAt)
                o.put("lastReadAt", chapter.lastReadAt)
                fastPrefs.edit().putString("cached_hot_chapter_v1", o.toString()).apply()
            } catch (_: Exception) {}
        }
    }

    /**
     * Flushes all essential application state to local cache & DataStore when the app is closing/pausing.
     */
    suspend fun flushEssentialCacheOnClose(
        settings: ReaderSettings,
        novels: List<NovelEntity>,
        chapters: List<ChapterEntity>,
        currentTabName: String,
        selectedNovelId: Long?,
        statusFilter: String,
        activeChapterId: Long?,
        activeReadingChapter: ChapterEntity?
    ) = withContext(Dispatchers.IO) {
        val settingsJson = settings.toJsonString()
        fastPrefs.edit().putString("global_reader_settings_json", settingsJson).apply()
        saveUiSessionStateSync(currentTabName, selectedNovelId, statusFilter, activeChapterId)
        if (novels.isNotEmpty()) saveCachedNovels(novels)
        if (chapters.isNotEmpty()) saveCachedChapters(chapters)

        val chapterToCache = activeReadingChapter
            ?: activeChapterId?.let { dao.getChapterById(it) }
            ?: chapters.maxByOrNull { it.lastReadAt }?.let { dao.getChapterById(it.id) }

        if (chapterToCache != null) {
            cacheHotChapterSync(chapterToCache)
        }

        runCatching {
            context.dataStore.edit { prefs ->
                prefs[settingsKey] = settingsJson
                fastPrefs.getString("cached_ui_session_state_v1", null)?.let {
                    prefs[sessionStateKey] = it
                }
            }
        }
    }

    fun loadCachedNovelsSync(): List<NovelEntity> {
        val raw = fastPrefs.getString("cached_active_novels_v1", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = ArrayList<NovelEntity>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    NovelEntity(
                        id = o.optLong("id"),
                        title = o.optString("title"),
                        description = o.optString("description"),
                        colorHex = o.optString("colorHex", "#D97706"),
                        category = o.optString("category", "عام"),
                        tags = o.optString("tags", ""),
                        sortBy = o.optString("sortBy", "NATURAL_NAME"),
                        sortAscending = o.optBoolean("sortAscending", true),
                        settingsOverrideJson = o.optString("settingsOverrideJson", "").ifBlank { null },
                        createdAt = o.optLong("createdAt")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun loadCachedChaptersSync(): List<ChapterEntity> {
        val raw = fastPrefs.getString("cached_active_chapters_v1", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = ArrayList<ChapterEntity>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val nId = o.optLong("novelId", -1L)
                list.add(
                    ChapterEntity(
                        id = o.optLong("id"),
                        novelId = if (nId <= 0L) null else nId,
                        title = o.optString("title"),
                        content = "",
                        format = o.optString("format", "TXT"),
                        wordCount = o.optInt("wordCount", 0),
                        readingStatus = o.optString("readingStatus", "NEW"),
                        translatorStage = o.optString("translatorStage", "DRAFT"),
                        favorite = o.optBoolean("favorite", false),
                        tags = o.optString("tags", ""),
                        paragraphIndex = o.optInt("paragraphIndex", 0),
                        charOffset = o.optInt("charOffset", 0),
                        progressPercent = o.optDouble("progressPercent", 0.0).toFloat(),
                        createdAt = o.optLong("createdAt"),
                        modifiedAt = o.optLong("modifiedAt"),
                        lastReadAt = o.optLong("lastReadAt", 0L)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveCachedNovels(novels: List<NovelEntity>) {
        try {
            val arr = JSONArray()
            for (n in novels) {
                val o = JSONObject()
                o.put("id", n.id)
                o.put("title", n.title)
                o.put("description", n.description)
                o.put("colorHex", n.colorHex)
                o.put("category", n.category)
                o.put("tags", n.tags)
                o.put("sortBy", n.sortBy)
                o.put("sortAscending", n.sortAscending)
                o.put("settingsOverrideJson", n.settingsOverrideJson ?: "")
                o.put("createdAt", n.createdAt)
                arr.put(o)
            }
            fastPrefs.edit().putString("cached_active_novels_v1", arr.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun saveCachedChapters(chapters: List<ChapterEntity>) {
        try {
            val arr = JSONArray()
            for (c in chapters) {
                val o = JSONObject()
                o.put("id", c.id)
                o.put("novelId", c.novelId ?: -1L)
                o.put("title", c.title)
                o.put("format", c.format)
                o.put("wordCount", c.wordCount)
                o.put("readingStatus", c.readingStatus)
                o.put("translatorStage", c.translatorStage)
                o.put("favorite", c.favorite)
                o.put("tags", c.tags)
                o.put("paragraphIndex", c.paragraphIndex)
                o.put("charOffset", c.charOffset)
                o.put("progressPercent", c.progressPercent.toDouble())
                o.put("createdAt", c.createdAt)
                o.put("modifiedAt", c.modifiedAt)
                o.put("lastReadAt", c.lastReadAt)
                arr.put(o)
            }
            fastPrefs.edit().putString("cached_active_chapters_v1", arr.toString()).apply()
        } catch (_: Exception) {}
    }

    fun observeFullChapterById(chapterId: Long): Flow<ChapterEntity?> =
        dao.observeChapterById(chapterId)
            .onStart {
                getCachedHotChapter(chapterId)?.let { emit(it) }
            }
            .onEach { ch ->
                if (ch != null && ch.content.isNotEmpty()) {
                    hotChaptersRamCache.put(ch.id, ch)
                }
            }
            .flowOn(Dispatchers.IO)

    suspend fun getFullChapterById(chapterId: Long): ChapterEntity? = withContext(Dispatchers.IO) {
        getCachedHotChapter(chapterId) ?: dao.getChapterById(chapterId)?.also { hotChaptersRamCache.put(it.id, it) }
    }

    /**
     * Zero-latency in-memory position update during active Reader scrolling.
     * Avoids triggering Room table invalidations and UI recompositions while the user is scrolling.
     */
    fun updateHotChapterPositionInRam(
        chapterId: Long,
        paragraphIndex: Int,
        charOffset: Int,
        totalParagraphs: Int
    ) {
        val cached = hotChaptersRamCache.get(chapterId) ?: return
        val safeTotal = totalParagraphs.coerceAtLeast(1)
        val rawPercent = if (safeTotal <= 1) 100f else ((paragraphIndex.toFloat() / (safeTotal - 1).toFloat()) * 100f).coerceIn(0f, 100f)
        val status = when {
            rawPercent >= 96f -> "COMPLETED"
            rawPercent > 0f || paragraphIndex > 0 || charOffset > 0 -> "READING"
            else -> "NEW"
        }
        val finalPercent = if (status == "COMPLETED") 100f else rawPercent
        hotChaptersRamCache.put(
            chapterId,
            cached.copy(
                paragraphIndex = paragraphIndex,
                charOffset = charOffset,
                progressPercent = finalPercent,
                readingStatus = status,
                lastReadAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun searchActiveChapterIds(query: String): Set<Long> = withContext(Dispatchers.IO) {
        if (query.isBlank()) emptySet()
        else dao.searchActiveChapterIds(query.trim()).toSet()
    }

    suspend fun initializeAndSeed() = withContext(Dispatchers.IO) {
        // 1. Migrate legacy DataStore settings once if fastPrefs is empty
        if (!fastPrefs.contains("global_reader_settings_json")) {
            val legacyJson = runCatching { context.dataStore.data.first()[settingsKey] }.getOrNull()
            if (!legacyJson.isNullOrBlank()) {
                val migrated = ReaderSettings.fromJsonString(legacyJson).copy(onboardingCompleted = true)
                fastPrefs.edit().putString("global_reader_settings_json", migrated.toJsonString()).apply()
                _globalSettingsFlow.value = migrated
            }
        }

        // 2. Purge any legacy seeded test chapters/novels once so the user never sees them again
        if (!fastPrefs.getBoolean("legacy_test_cleaned_v12", false)) {
            SampleDataSeeder.removeLegacySeededDataIfPresent(dao)
            fastPrefs.edit().putBoolean("legacy_test_cleaned_v12", true).apply()
        }

        // 3. Purge expired 30-day trash items
        val cutoff = System.currentTimeMillis() - 30L * 24L * 3600L * 1000L
        dao.purgeExpiredTrashChapters(cutoff)
        dao.purgeExpiredTrashNovels(cutoff)
    }

    suspend fun saveGlobalSettings(settings: ReaderSettings) = withContext(Dispatchers.IO) {
        val json = settings.toJsonString()
        fastPrefs.edit().putString("global_reader_settings_json", json).apply()
        _globalSettingsFlow.value = settings
        runCatching {
            context.dataStore.edit { prefs ->
                prefs[settingsKey] = json
            }
        }
    }

    suspend fun saveNovelSettingsOverride(novelId: Long, settings: ReaderSettings?) = withContext(Dispatchers.IO) {
        val novel = dao.getNovelById(novelId) ?: return@withContext
        dao.updateNovel(novel.copy(settingsOverrideJson = settings?.toJsonString()))
    }

    // --- Novel Operations ---
    suspend fun createNovel(title: String, description: String, colorHex: String, category: String, tags: String): Long =
        withContext(Dispatchers.IO) {
            dao.insertNovel(
                NovelEntity(
                    title = title.trim().ifBlank { "رواية جديدة" },
                    description = description.trim(),
                    colorHex = colorHex,
                    category = category.ifBlank { "عام" },
                    tags = tags.trim()
                )
            )
        }

    suspend fun updateNovel(novel: NovelEntity) = withContext(Dispatchers.IO) {
        dao.updateNovel(novel)
    }

    suspend fun moveNovelToTrash(novelId: Long) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val novel = dao.getNovelById(novelId) ?: return@withContext
        dao.updateNovel(novel.copy(deletedAt = now))
        dao.updateNovelChaptersDeletedAt(novelId, now)
    }

    suspend fun restoreNovelFromTrash(novel: NovelEntity) = withContext(Dispatchers.IO) {
        dao.updateNovel(novel.copy(deletedAt = null))
        dao.updateNovelChaptersDeletedAt(novel.id, null)
    }

    suspend fun permanentlyDeleteNovel(novelId: Long) = withContext(Dispatchers.IO) {
        val allChapters = dao.getAllChaptersSnapshot().filter { it.novelId == novelId }
        allChapters.forEach { dao.permanentlyDeleteChapter(it.id) }
        dao.permanentlyDeleteNovel(novelId)
    }

    // --- Chapter Operations ---
    suspend fun createChapter(
        novelId: Long?,
        title: String,
        content: String,
        format: String = "TXT",
        translatorStage: String = "DRAFT",
        tags: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val wordCount = TextProcessingUtils.countWords(content)
        dao.insertChapter(
            ChapterEntity(
                novelId = novelId,
                title = title.trim().ifBlank { TextProcessingUtils.suggestTitleFromContent(content) },
                content = content,
                format = format.uppercase(),
                wordCount = wordCount,
                translatorStage = translatorStage,
                tags = tags
            )
        )
    }

    suspend fun createChaptersBatch(novelId: Long?, previews: List<SplitChapterPreview>, format: String = "TXT"): List<Long> =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val entities = previews.mapIndexed { index, item ->
                ChapterEntity(
                    novelId = novelId,
                    title = item.title,
                    content = item.content,
                    format = format,
                    wordCount = item.wordCount,
                    createdAt = now + index,
                    modifiedAt = now + index
                )
            }
            dao.insertChapters(entities)
        }

    /**
     * Safely updates a chapter. If the passed chapter came from the lightweight Library list
     * (where content is empty to save RAM), it preserves the full content stored in SQLite.
     */
    suspend fun updateChapter(chapter: ChapterEntity) = withContext(Dispatchers.IO) {
        val actualContent = if (chapter.content.isNotEmpty()) {
            chapter.content
        } else {
            dao.getChapterById(chapter.id)?.content ?: ""
        }
        val updatedWords = if (chapter.content.isNotEmpty()) {
            TextProcessingUtils.countWords(actualContent)
        } else {
            chapter.wordCount
        }
        val updatedEntity = chapter.copy(
            content = actualContent,
            wordCount = updatedWords,
            modifiedAt = System.currentTimeMillis()
        )
        dao.updateChapter(updatedEntity)
        cacheHotChapterSync(updatedEntity)
    }

    suspend fun saveReadingPosition(
        chapterId: Long,
        paragraphIndex: Int,
        charOffset: Int,
        totalParagraphs: Int
    ) = withContext(Dispatchers.IO) {
        val safeTotal = totalParagraphs.coerceAtLeast(1)
        val rawPercent = if (safeTotal <= 1) {
            100f
        } else {
            ((paragraphIndex.toFloat() / (safeTotal - 1).toFloat()) * 100f).coerceIn(0f, 100f)
        }
        val status = when {
            rawPercent >= 96f -> "COMPLETED"
            rawPercent > 0f || paragraphIndex > 0 || charOffset > 0 -> "READING"
            else -> "NEW"
        }
        val finalPercent = if (status == "COMPLETED") 100f else rawPercent
        val now = System.currentTimeMillis()
        dao.updateReadingPosition(
            chapterId = chapterId,
            paragraphIndex = paragraphIndex,
            charOffset = charOffset,
            progressPercent = finalPercent,
            readingStatus = status,
            lastReadAt = now
        )
        getCachedHotChapter(chapterId)?.let { cached ->
            cacheHotChapterSync(
                cached.copy(
                    paragraphIndex = paragraphIndex,
                    charOffset = charOffset,
                    progressPercent = finalPercent,
                    readingStatus = status,
                    lastReadAt = now
                )
            )
        }
    }

    suspend fun duplicateChapter(chapter: ChapterEntity): Long = withContext(Dispatchers.IO) {
        val full = dao.getChapterById(chapter.id) ?: chapter
        val now = System.currentTimeMillis()
        dao.insertChapter(
            full.copy(
                id = 0,
                title = "${full.title} (نسخة)",
                createdAt = now,
                modifiedAt = now
            )
        )
    }

    suspend fun convertChapterFormat(
        chapter: ChapterEntity,
        targetFormat: String,
        saveAsNewCopy: Boolean
    ): Long = withContext(Dispatchers.IO) {
        val full = dao.getChapterById(chapter.id) ?: chapter
        val convertedContent = TextProcessingUtils.convertTextFormat(
            title = full.title,
            currentContent = full.content,
            fromFormat = full.format,
            toFormat = targetFormat
        )
        val now = System.currentTimeMillis()
        if (saveAsNewCopy) {
            dao.insertChapter(
                full.copy(
                    id = 0,
                    title = "${full.title} ($targetFormat)",
                    content = convertedContent,
                    format = targetFormat,
                    wordCount = TextProcessingUtils.countWords(convertedContent),
                    createdAt = now,
                    modifiedAt = now
                )
            )
        } else {
            dao.updateChapter(
                full.copy(
                    content = convertedContent,
                    format = targetFormat,
                    wordCount = TextProcessingUtils.countWords(convertedContent),
                    modifiedAt = now
                )
            )
            full.id
        }
    }

    suspend fun batchRenameChapters(
        chapters: List<ChapterEntity>,
        pattern: String,
        startNumber: Int,
        zeroPadding: Int
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        chapters.forEachIndexed { idx, ch ->
            val num = startNumber + idx
            val numStr = num.toString().padStart(zeroPadding.coerceAtLeast(1), '0')
            val newTitle = if (pattern.contains("{رقم}")) {
                pattern.replace("{رقم}", numStr)
            } else {
                "$pattern $numStr"
            }
            dao.updateChapterTitleAndTags(ch.id, newTitle.trim(), ch.tags, now)
        }
    }

    suspend fun mergeChapters(
        chapters: List<ChapterEntity>,
        mergedTitle: String,
        targetNovelId: Long?,
        deleteOriginalsToTrash: Boolean
    ): Long = withContext(Dispatchers.IO) {
        val fullChapters = chapters.mapNotNull { dao.getChapterById(it.id) }
        val combinedContent = fullChapters.joinToString("\n\n* * *\n\n") { ch ->
            "${ch.title}\n\n${ch.content.trim()}"
        }
        val newId = createChapter(
            novelId = targetNovelId,
            title = mergedTitle.ifBlank { "فصل مدمج (${fullChapters.size} فصول)" },
            content = combinedContent,
            format = fullChapters.firstOrNull()?.format ?: "TXT"
        )
        if (deleteOriginalsToTrash) {
            val now = System.currentTimeMillis()
            fullChapters.forEach { ch ->
                dao.updateChapterDeletedAt(ch.id, now)
            }
        }
        newId
    }

    suspend fun moveChaptersToTrash(chapterIds: List<Long>) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        chapterIds.forEach { id ->
            dao.updateChapterDeletedAt(id, now)
        }
    }

    suspend fun restoreChaptersFromTrash(chapterIds: List<Long>) = withContext(Dispatchers.IO) {
        chapterIds.forEach { id ->
            dao.updateChapterDeletedAt(id, null)
        }
    }

    suspend fun permanentlyDeleteChapter(id: Long) = withContext(Dispatchers.IO) {
        dao.permanentlyDeleteChapter(id)
    }

    suspend fun emptyAllTrash() = withContext(Dispatchers.IO) {
        dao.emptyTrashChapters()
        dao.emptyTrashNovels()
    }

    // --- Find and Replace across Chapter or Novel ---
    suspend fun replaceInChapters(
        targetChapters: List<ChapterEntity>,
        findQuery: String,
        replaceWith: String,
        matchCase: Boolean
    ): Int = withContext(Dispatchers.IO) {
        if (findQuery.isEmpty()) return@withContext 0
        var totalReplacements = 0
        val regex = if (matchCase) {
            Regex(Regex.escape(findQuery))
        } else {
            Regex(Regex.escape(findQuery), RegexOption.IGNORE_CASE)
        }
        targetChapters.forEach { summary ->
            val ch = dao.getChapterById(summary.id) ?: return@forEach
            val matchesCount = regex.findAll(ch.content).count()
            if (matchesCount > 0) {
                totalReplacements += matchesCount
                val newContent = regex.replace(ch.content, replaceWith)
                dao.updateChapter(
                    ch.copy(
                        content = newContent,
                        wordCount = TextProcessingUtils.countWords(newContent),
                        modifiedAt = System.currentTimeMillis()
                    )
                )
            }
        }
        totalReplacements
    }

    // --- Annotations & Glossary ---
    suspend fun addAnnotation(annotation: AnnotationEntity): Long = withContext(Dispatchers.IO) {
        dao.insertAnnotation(annotation)
    }

    suspend fun deleteAnnotation(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteAnnotationById(id)
    }

    suspend fun saveGlossaryTerm(term: GlossaryEntity): Long = withContext(Dispatchers.IO) {
        dao.insertGlossaryTerm(term)
    }

    suspend fun deleteGlossaryTerm(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteGlossaryTermById(id)
    }

    // --- Reading Stats Recording ---
    suspend fun recordReadingProgress(minutesDelta: Int, wordsDelta: Int, completedChapterDelta: Int = 0) =
        withContext(Dispatchers.IO) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val current = dao.getStatsForDate(today)
            if (current == null) {
                dao.upsertReadingStats(
                    ReadingStatsEntity(
                        dateString = today,
                        minutesRead = minutesDelta.coerceAtLeast(0),
                        wordsRead = wordsDelta.coerceAtLeast(0),
                        chaptersCompleted = completedChapterDelta.coerceAtLeast(0)
                    )
                )
            } else {
                dao.upsertReadingStats(
                    current.copy(
                        minutesRead = (current.minutesRead + minutesDelta).coerceAtLeast(0),
                        wordsRead = (current.wordsRead + wordsDelta).coerceAtLeast(0),
                        chaptersCompleted = (current.chaptersCompleted + completedChapterDelta).coerceAtLeast(0),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }

    // --- Backup & Restore (JSON) ---
    suspend fun exportFullBackupJson(currentSettings: ReaderSettings): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())
        root.put("settings", JSONObject(currentSettings.toJsonString()))

        val novelsArr = JSONArray()
        dao.getAllNovelsSnapshot().forEach { n ->
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("title", n.title)
            obj.put("description", n.description)
            obj.put("colorHex", n.colorHex)
            obj.put("category", n.category)
            obj.put("tags", n.tags)
            obj.put("sortBy", n.sortBy)
            obj.put("sortAscending", n.sortAscending)
            obj.put("settingsOverrideJson", n.settingsOverrideJson ?: "")
            obj.put("createdAt", n.createdAt)
            novelsArr.put(obj)
        }
        root.put("novels", novelsArr)

        val chaptersArr = JSONArray()
        dao.getAllChaptersSnapshot().forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("novelId", c.novelId ?: -1L)
            obj.put("title", c.title)
            obj.put("content", c.content)
            obj.put("format", c.format)
            obj.put("wordCount", c.wordCount)
            obj.put("readingStatus", c.readingStatus)
            obj.put("translatorStage", c.translatorStage)
            obj.put("favorite", c.favorite)
            obj.put("tags", c.tags)
            obj.put("paragraphIndex", c.paragraphIndex)
            obj.put("charOffset", c.charOffset)
            obj.put("progressPercent", c.progressPercent.toDouble())
            obj.put("createdAt", c.createdAt)
            obj.put("modifiedAt", c.modifiedAt)
            obj.put("lastReadAt", c.lastReadAt)
            chaptersArr.put(obj)
        }
        root.put("chapters", chaptersArr)

        val glossaryArr = JSONArray()
        dao.getAllGlossarySnapshot().forEach { g ->
            val obj = JSONObject()
            obj.put("novelId", g.novelId ?: -1L)
            obj.put("term", g.term)
            obj.put("translation", g.translation)
            obj.put("category", g.category)
            obj.put("note", g.note)
            obj.put("highlightColorHex", g.highlightColorHex)
            glossaryArr.put(obj)
        }
        root.put("glossary", glossaryArr)

        val annotationsArr = JSONArray()
        dao.getAllAnnotationsSnapshot().forEach { a ->
            val obj = JSONObject()
            obj.put("chapterId", a.chapterId)
            obj.put("novelId", a.novelId ?: -1L)
            obj.put("type", a.type)
            obj.put("paragraphIndex", a.paragraphIndex)
            obj.put("startOffset", a.startOffset)
            obj.put("endOffset", a.endOffset)
            obj.put("selectedText", a.selectedText)
            obj.put("noteText", a.noteText)
            obj.put("colorHex", a.colorHex)
            annotationsArr.put(obj)
        }
        root.put("annotations", annotationsArr)

        root.toString(2)
    }

    suspend fun importFullBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.has("settings")) {
                val settings = ReaderSettings.fromJsonString(root.getJSONObject("settings").toString())
                saveGlobalSettings(settings.copy(lastBackupTimestamp = System.currentTimeMillis()))
            }
            if (root.has("novels")) {
                val arr = root.getJSONArray("novels")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    dao.insertNovel(
                        NovelEntity(
                            id = o.optLong("id", 0L),
                            title = o.optString("title", "رواية"),
                            description = o.optString("description", ""),
                            colorHex = o.optString("colorHex", "#D97706"),
                            category = o.optString("category", "عام"),
                            tags = o.optString("tags", ""),
                            sortBy = o.optString("sortBy", "NATURAL_NAME"),
                            sortAscending = o.optBoolean("sortAscending", true),
                            settingsOverrideJson = o.optString("settingsOverrideJson", "").ifBlank { null },
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }
            if (root.has("chapters")) {
                val arr = root.getJSONArray("chapters")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val nId = o.optLong("novelId", -1L)
                    dao.insertChapter(
                        ChapterEntity(
                            id = o.optLong("id", 0L),
                            novelId = if (nId <= 0L) null else nId,
                            title = o.optString("title", "فصل"),
                            content = o.optString("content", ""),
                            format = o.optString("format", "TXT"),
                            wordCount = o.optInt("wordCount", 0),
                            readingStatus = o.optString("readingStatus", "NEW"),
                            translatorStage = o.optString("translatorStage", "DRAFT"),
                            favorite = o.optBoolean("favorite", false),
                            tags = o.optString("tags", ""),
                            paragraphIndex = o.optInt("paragraphIndex", 0),
                            charOffset = o.optInt("charOffset", 0),
                            progressPercent = o.optDouble("progressPercent", 0.0).toFloat(),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                            modifiedAt = o.optLong("modifiedAt", System.currentTimeMillis()),
                            lastReadAt = o.optLong("lastReadAt", 0L)
                        )
                    )
                }
            }
            if (root.has("glossary")) {
                val arr = root.getJSONArray("glossary")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val nId = o.optLong("novelId", -1L)
                    dao.insertGlossaryTerm(
                        GlossaryEntity(
                            novelId = if (nId <= 0L) null else nId,
                            term = o.optString("term", ""),
                            translation = o.optString("translation", ""),
                            category = o.optString("category", "شخصية"),
                            note = o.optString("note", ""),
                            highlightColorHex = o.optString("highlightColorHex", "#FDE047")
                        )
                    )
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
