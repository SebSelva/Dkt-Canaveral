package com.decathlon.core.gamestats.data

const val DART_MISS_PERCENT = 284
const val DART_1_PERCENT = 285
const val DART_2_PERCENT = 286
const val DART_3_PERCENT = 287
const val DART_4_PERCENT = 288
const val DART_5_PERCENT = 289
const val DART_6_PERCENT = 290
const val DART_7_PERCENT = 291
const val DART_8_PERCENT = 292
const val DART_9_PERCENT = 293
const val DART_10_PERCENT = 294
const val DART_11_PERCENT = 295
const val DART_12_PERCENT = 296
const val DART_13_PERCENT = 297
const val DART_14_PERCENT = 298
const val DART_15_PERCENT = 299
const val DART_16_PERCENT = 300
const val DART_17_PERCENT = 301
const val DART_18_PERCENT = 302
const val DART_19_PERCENT = 303
const val DART_20_PERCENT = 304
const val DART_BULL_PERCENT = 305
const val DOUBLE_PERCENT = 306
const val TRIPLE_PERCENT = 307
const val TRIPLE_19_PERCENT = 309
const val TRIPLE_20_PERCENT = 310

const val DART_MISS_COUNT = 327
const val DART_1_COUNT = 328
const val DART_2_COUNT = 329
const val DART_3_COUNT = 330
const val DART_4_COUNT = 331
const val DART_5_COUNT = 332
const val DART_6_COUNT = 333
const val DART_7_COUNT = 334
const val DART_8_COUNT = 335
const val DART_9_COUNT = 336
const val DART_10_COUNT = 337
const val DART_11_COUNT = 338
const val DART_12_COUNT = 339
const val DART_13_COUNT = 340
const val DART_14_COUNT = 341
const val DART_15_COUNT = 270
const val DART_16_COUNT = 271
const val DART_17_COUNT = 272
const val DART_18_COUNT = 273
const val DART_19_COUNT = 274
const val DART_20_COUNT = 275
const val DART_BULL_COUNT = 262
const val DOUBLE_COUNT = 334
const val TRIPLE_COUNT = 343
const val TRIPLE_19_COUNT = 344
const val TRIPLE_20_COUNT = 345

const val GAMES_WON_PERCENT = 219
const val LEGS_WON_PERCENT = 222

object StdUtils {

    fun getCountIndexFromPercentIndex(percent: Int): Int {
        return when (percent) {
            DART_MISS_PERCENT -> DART_MISS_COUNT
            DART_1_PERCENT -> DART_1_COUNT
            DART_2_PERCENT -> DART_2_COUNT
            DART_3_PERCENT -> DART_3_COUNT
            DART_4_PERCENT -> DART_4_COUNT
            DART_5_PERCENT -> DART_5_COUNT
            DART_6_PERCENT -> DART_6_COUNT
            DART_7_PERCENT -> DART_7_COUNT
            DART_8_PERCENT -> DART_8_COUNT
            DART_9_PERCENT -> DART_9_COUNT
            DART_10_PERCENT -> DART_10_COUNT
            DART_11_PERCENT -> DART_11_COUNT
            DART_12_PERCENT -> DART_12_COUNT
            DART_13_PERCENT -> DART_13_COUNT
            DART_14_PERCENT -> DART_14_COUNT
            DART_15_PERCENT -> DART_15_COUNT
            DART_16_PERCENT -> DART_16_COUNT
            DART_17_PERCENT -> DART_17_COUNT
            DART_18_PERCENT -> DART_18_COUNT
            DART_19_PERCENT -> DART_19_COUNT
            DART_20_PERCENT -> DART_20_COUNT
            DART_BULL_PERCENT -> DART_BULL_COUNT
            DOUBLE_PERCENT -> DOUBLE_COUNT
            TRIPLE_PERCENT -> TRIPLE_COUNT
            TRIPLE_19_PERCENT -> TRIPLE_19_COUNT
            TRIPLE_20_PERCENT -> TRIPLE_20_COUNT
            else -> {
                -1
            }
        }
    }
}
