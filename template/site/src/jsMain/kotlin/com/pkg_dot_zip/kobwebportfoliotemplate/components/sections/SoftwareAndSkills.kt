package com.pkg_dot_zip.kobwebportfoliotemplate.components.sections

import AppearanceAwareImage
import androidx.compose.runtime.Composable
import com.pkg_dot_zip.kobwebportfoliotemplate.Res
import com.pkg_dot_zip.kobwebportfoliotemplate.components.widgets.GlassBox
import com.pkg_dot_zip.kobwebportfoliotemplate.components.widgets.IconButtonWithHover
import com.pkg_dot_zip.kobwebportfoliotemplate.components.widgets.scaleOnHoverAnimation
import com.pkg_dot_zip.kobwebportfoliotemplate.util.FontHandler
import com.pkg_dot_zip.kobwebportfoliotemplate.util.Logger
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.browser.window
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1

/**
 * The boxes you see on the homepage with icons of language and software.
 */
@Composable
fun SoftwareAndSkills() {
    // These are the boxes we want to create.
    val data = getToolBoxAreas()

    H1 {
        Row(
            Modifier.flexWrap(FlexWrap.Wrap),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SpanText("I Work(ed) With...", modifier = Modifier.fontFamily(FontHandler.getFont("h1")))
            Image(
                src = Res.AnimatedEmojis.MAN_LIFTING_WEIGHTS,
                modifier = Modifier.width(48.px).align(Alignment.CenterVertically)
            )
        }
    }

    SimpleGrid(numColumns(1, 1, 2, 2, 2)) {
        data.forEach { createGlassBoxContainer(it) }
    }
}

@Composable
fun createGlassBoxContainer(area: ToolsBoxArea) {
    val logger = Logger.get("createGlassBoxContainer")

    val ctx = rememberPageContext()

    logger.info("Creating Box Container for: ${area.name}")

    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                area.icon()
                SpanText(area.name, modifier = Modifier.fontFamily(FontHandler.getFont("h1")).padding(left = 4.px))
            }

            Row {
                GlassBox(modifier = Modifier.margin(all = 2.cssRem)) {
                    fun getAmountInGrid(): Int {
                        logger.trace("InnerWidth: ${window.innerWidth}")
                        logger.trace("OuterWidth: ${window.outerWidth}")

                        val needsTwoItemsWidth = 366
                        val needsOneItemsWidth = 281

                        if (window.innerWidth < needsOneItemsWidth) return 1
                        return if (window.innerWidth < needsTwoItemsWidth) 2 else 3
                    }

                    SimpleGrid(
                        modifier = Modifier.padding(0.5.cssRem),
                        numColumns = numColumns(base = getAmountInGrid(), sm = 3, md = 3, lg = 4)
                    ) {
                        for ((url, image) in area.imageLinks) {
                            logger.trace("\tCreating image for $image")

                            Box(modifier = Modifier.size(65.px).margin(all = 0.5.cssRem), contentAlignment = Alignment.Center) {
                                IconButtonWithHover(onClick = { }) {
                                    AppearanceAwareImage(
                                        src = image,
                                        noChange = true,
                                        modifier = Modifier.size(42.px).title(url).onClick { ctx.router.navigateTo(url) }.then(
                                            scaleOnHoverAnimation.toModifier()),
                                        dropShadow = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private val toolboxHeaderImageModifier = Modifier.width(24.px)

// USER TODO: Add your own boxes in this method if you want more than the 4 in the template.
@Composable
private fun getToolBoxAreas(): List<ToolsBoxArea> = listOf(
    ToolsBoxArea(
        "Programming Languages",
        PROGRAMMING_LANGUAGES
    ) { Image(src = Res.AnimatedEmojis.SMILING_FACE_WITH_SUNGLASSES, toolboxHeaderImageModifier) },
    ToolsBoxArea("IDEs", IDES) { Image(src = Res.AnimatedEmojis.DOTTED_LINE_FACE, toolboxHeaderImageModifier) },
    ToolsBoxArea("Software", SOFTWARE) { Image(src = Res.AnimatedEmojis.NERD_FACE, toolboxHeaderImageModifier) },
    ToolsBoxArea("Frameworks", FRAMEWORKS) { Image(src = Res.AnimatedEmojis.MAN_JUGGLING, toolboxHeaderImageModifier) },
)

private val IDES: Collection<LinkableImage> = listOf(
    LinkableImage("https://www.vscode.com/", Res.Software.VSCODE_LOGO),
)

private val FRAMEWORKS: Collection<LinkableImage> = listOf(
    LinkableImage("https://fastapi.tiangolo.com/", Res.Software.FASTAPI_LOGO),
    LinkableImage("https://flask.palletsprojects.com/", Res.Software.FLASK_LOGO),
    LinkableImage("https://gin-gonic.com/", Res.Software.GIN_LOGO),
    LinkableImage("https://openjfx.io/", Res.Software.JAVA_FX_LOGO),
)

private val SOFTWARE: Collection<LinkableImage> = listOf(
    LinkableImage("https://www.apachefriends.org/", Res.Software.XAMPP_LOGO),
)

private val PROGRAMMING_LANGUAGES: Collection<LinkableImage> = listOf(
    LinkableImage("https://en.wikipedia.org/wiki/C%2B%2B", Res.Languages.CPP_LOGO),
    LinkableImage("https://dotnet.microsoft.com/en-us/languages/csharp", Res.Languages.CSHARP_LOGO),
    LinkableImage("https://developer.mozilla.org/en-US/docs/Web/JavaScript", Res.Languages.JAVASCRIPT_LOGO),
    LinkableImage("https://dev.java/", Res.Languages.JAVA_LOGO),
    LinkableImage("https://www.python.org/", Res.Languages.PYTHON_LOGO),
    LinkableImage("https://go.dev/", Res.Languages.GOLANG_LOGO),
    LinkableImage("https://www.markdownguide.org/", Res.Languages.MARKDOWN_LOGO),
    LinkableImage("https://html.spec.whatwg.org/multipage/", Res.Languages.HTML_LOGO),
)

data class ToolsBoxArea(
    val name: String,
    val imageLinks: Iterable<LinkableImage> = mutableListOf(),
    val icon: @Composable () -> Unit
)

data class LinkableImage(val url: String, val imageSrc: String)