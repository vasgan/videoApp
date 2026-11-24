package com.example.myapplication.data.parse.dreamcast

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DreamCastHtmlParserTest {
    private val parser = DreamCastHtmlParser()

    @Test
    fun `parses release metadata from fixture`() {
        val release = parser.parseRelease(fixture("release.html"), "42")

        assertEquals("42", release.id)
        assertEquals("Русское название", release.russian)
        assertEquals("Original title", release.original)
        assertEquals("https://cdn.example.test/cover.jpg", release.image)
    }

    @Test
    fun `parses description and decoded series from fixture`() {
        val result = parser.parseDescriptionAndSeries(fixture("details.html"))

        assertEquals("Описание релиза", result.description)
        assertEquals(listOf("https://video/1.m3u8", "https://video/2.m3u8"), result.series)
    }

    @Test
    fun `returns empty fields for incomplete html`() {
        val release = parser.parseRelease("<html></html>", "42")
        val details = parser.parseDescriptionAndSeries("<html>atob(\"not-base64\").split('||||')</html>")

        assertTrue(release.russian.isEmpty())
        assertTrue(release.image.isEmpty())
        assertTrue(details.description.isEmpty())
        assertTrue(details.series.isEmpty())
    }

    @Test
    fun `returns empty description and series when expected markers are absent`() {
        val noDescription = parser.parseDescriptionAndSeries("<html>atob(\"YQ==\").split('||||')</html>")
        val noAtob = parser.parseDescriptionAndSeries("<meta name=\"description\" content=\"text\">")
        val noSplit = parser.parseDescriptionAndSeries("<html>atob(\"YQ==\")</html>")

        assertTrue(noDescription.description.isEmpty())
        assertTrue(noAtob.series.isEmpty())
        assertTrue(noSplit.series.isEmpty())
    }

    private fun fixture(name: String): String =
        javaClass.classLoader!!.getResource(name)!!.readText()
}
