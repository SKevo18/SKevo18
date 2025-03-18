package com.pkg_dot_zip.kobwebportfoliotemplate.util.date

import com.pkg_dot_zip.kobwebportfoliotemplate.Res

object SpecialDateHandler {

    /**
     * These are hardcoded emojis that will be displayed on a specific date.
     * The specialDateText will be displayed in the Footer.
     *
     * For questions about priority see [getValueOnSpecialDate].
     */
    private fun getSpecialDates(): Set<SpecialDate> = setOf(
        SpecialDate(WebDate(months = 3, days = 18)).apply {
            title = "Site birthday"
            specialDateText = "Happy Birthday to this site!"
            frontPageEmoji = Res.AnimatedEmojis.PARTYING_FACE
        },

        SpecialDate(WebDate(months = 5, days = 2)).apply {
            title = "Birthday"
            specialDateText = "Happy Birthday to... me?!"
            frontPageEmoji = Res.AnimatedEmojis.PARTYING_FACE
        },
    )

    /**
     * Retrieves a value based on the closest match between a given date and a list of [SpecialDate] instances.
     *
     * The matching is determined by the following priority (in descending order):
     *
     * 1. Exact match of the date (including hour) – highest priority.
     * 2. Match on the same date (including year).
     * 3. Match on the same date (month and day, ignoring year).
     * 4. If no matches or relevant dates are found, `null` is returned.
     *
     * @param T The type of value to retrieve.
     * @param date The reference date for matching. Defaults to the current date if not provided.
     * @param valueSelector A function that selects a value for each [SpecialDate]. If the function returns `null`, the date is ignored.
     *
     * @return The value of type [T] corresponding to the best-matched [SpecialDate], or `null` if no match or no non-null value is found.
     *
     * @see WebDate for the date type used.
     * @see SpecialDate for the date type used in matching.
     */
    fun <T> getValueOnSpecialDate(
        date: WebDate = WebDate.now(),
        valueSelector: (SpecialDate) -> T?
    ): T? {
        val specialDates = getSpecialDates()
        val valueMap = mutableMapOf<SpecialDate, Int>()

        for (specialDate in specialDates) {
            if (valueSelector(specialDate) == null) continue
            when {
                specialDate.date == date -> valueMap[specialDate] = 4
                specialDate.date.onSameDateAndYearAndHour(date) -> valueMap[specialDate] = 3
                specialDate.date.onSameDateAndYear(date) -> valueMap[specialDate] = 2
                specialDate.date.onSameDate(date) -> valueMap[specialDate] = 1
                else -> valueMap[specialDate] = 0
            }
        }

        val maxEntry = valueMap.maxByOrNull { it.value } // Find the entry with the maximum value.
        if (maxEntry == null || maxEntry.value == 0) return null // Return null if all values are 0.
        return maxEntry.key.let(valueSelector)
    }

    data class SpecialDate(
        var date: WebDate,
        var frontPageEmoji: String? = null,
        var specialDateText: String? = null,
        var title: String? = null,
    )
}