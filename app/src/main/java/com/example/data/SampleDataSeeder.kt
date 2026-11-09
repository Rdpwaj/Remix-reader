package com.example.data

object SampleDataSeeder {

    /**
     * Sample/test chapters are disabled as requested by the user.
     */
    suspend fun seedIfEmpty(dao: AppDao) {
        // Intentionally empty: do not seed any test or sample chapters.
    }

    /**
     * Instantly removes any previously seeded test novels/chapters using direct SQLite DELETE
     * queries (0 bytes of chapter text loaded into RAM). Preserves 100% of the user's own data.
     */
    suspend fun removeLegacySeededDataIfPresent(dao: AppDao) {
        dao.purgeLegacySeededChapters()
        dao.purgeLegacySeededNovels()
    }
}
