import fs from 'fs';
import path from 'path';
import { marked } from 'marked';
import fm from 'front-matter';

const SRC_DIR = './src';
const DIST_DIR = './dist';
const ARTICLES_DIR = `${SRC_DIR}/articles`;
const TEMPLATES_DIR = `${SRC_DIR}/templates`;

function ensureDir(dir) {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
    }
}

function readTemplate(name) {
    return fs.readFileSync(`${TEMPLATES_DIR}/${name}.html`, 'utf-8');
}

function applyTemplate(template, data) {
    let result = template;
    for (const [key, value] of Object.entries(data)) {
        result = result.replaceAll(`{{${key}}}`, value ?? '');
    }
    return result;
}

function parseMarkdown(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    const { attributes, body } = fm(content);
    const html = marked(body);
    return { meta: attributes, html, raw: body };
}

function getMarkdownFiles(dir) {
    if (!fs.existsSync(dir)) return [];
    return fs.readdirSync(dir)
        .filter(f => f.endsWith('.md'))
        .map(f => ({
            name: f.replace('.md', ''),
            path: path.join(dir, f)
        }));
}

function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
}

function buildArticles() {
    const files = getMarkdownFiles(ARTICLES_DIR);
    const baseTemplate = readTemplate('base');
    const articleTemplate = readTemplate('article');

    const articles = [];

    for (const file of files) {
        const { meta, html } = parseMarkdown(file.path);

        articles.push({
            slug: file.name,
            title: meta.title || file.name,
            date: meta.date,
            description: meta.description || '',
            tags: meta.tags || []
        });

        const articleHtml = applyTemplate(articleTemplate, {
            title: meta.title || file.name,
            date: formatDate(meta.date),
            content: html,
            description: meta.description || ''
        });

        const pageHtml = applyTemplate(baseTemplate, {
            title: `${meta.title || file.name} - SKevo`,
            content: articleHtml,
            bodyClass: 'article-page'
        });

        ensureDir(`${DIST_DIR}/articles`);
        fs.writeFileSync(`${DIST_DIR}/articles/${file.name}.html`, pageHtml);
    }

    articles.sort((a, b) => new Date(b.date || 0) - new Date(a.date || 0));

    return articles;
}

function buildCV() {
    const cvPath = `${SRC_DIR}/cv.md`;
    if (!fs.existsSync(cvPath)) return;

    const { meta, html } = parseMarkdown(cvPath);
    const baseTemplate = readTemplate('base');
    const cvTemplate = readTemplate('cv');

    const cvHtml = applyTemplate(cvTemplate, {
        content: html,
        name: meta.name || 'CV',
        title: meta.title || ''
    });

    const pageHtml = applyTemplate(baseTemplate, {
        title: `CV - ${meta.name || 'SKevo'}`,
        content: cvHtml,
        bodyClass: 'cv-page'
    });

    fs.writeFileSync(`${DIST_DIR}/cv.html`, pageHtml);
}

function buildIndex(articles) {
    const indexPath = `${SRC_DIR}/index.md`;
    const baseTemplate = readTemplate('base');
    const indexTemplate = readTemplate('index');

    let introHtml = '';
    if (fs.existsSync(indexPath)) {
        const { html } = parseMarkdown(indexPath);
        introHtml = html;
    }

    const articleListHtml = articles.map(a => `
        <article class="article-preview">
            <a href="/articles/${a.slug}.html">
                <h3>${a.title}</h3>
                ${a.date ? `<time datetime="${new Date(a.date).toISOString().split('T')[0]}">${formatDate(a.date)}</time>` : ''}
                ${a.description ? `<p>${a.description}</p>` : ''}
            </a>
        </article>
    `).join('\n');

    const indexHtml = applyTemplate(indexTemplate, {
        intro: introHtml,
        articles: articleListHtml,
        hasArticles: articles.length > 0 ? 'true' : ''
    });

    const pageHtml = applyTemplate(baseTemplate, {
        title: 'SKevo',
        content: indexHtml,
        bodyClass: 'home-page'
    });

    fs.writeFileSync(`${DIST_DIR}/index.html`, pageHtml);
}

function copyStatic() {
    const staticDir = `${SRC_DIR}/static`;
    if (!fs.existsSync(staticDir)) return;

    function copyRecursive(src, dest) {
        ensureDir(dest);
        const entries = fs.readdirSync(src, { withFileTypes: true });

        for (const entry of entries) {
            const srcPath = path.join(src, entry.name);
            const destPath = path.join(dest, entry.name);

            if (entry.isDirectory()) {
                copyRecursive(srcPath, destPath);
            } else {
                fs.copyFileSync(srcPath, destPath);
            }
        }
    }

    copyRecursive(staticDir, DIST_DIR);
}

function build() {
    console.log('Building site...');

    if (fs.existsSync(DIST_DIR)) {
        fs.rmSync(DIST_DIR, { recursive: true });
    }
    ensureDir(DIST_DIR);

    copyStatic();

    const articles = buildArticles();
    buildCV();
    buildIndex(articles);

    console.log(`Built ${articles.length} articles`);
    console.log('Done!');
}

if (process.argv.includes('--watch')) {
    const chokidar = await import('chokidar');

    build();

    console.log('Watching for changes...');
    chokidar.default.watch(SRC_DIR).on('change', () => {
        console.log('Change detected, rebuilding...');
        build();
    });
} else {
    build();
}
