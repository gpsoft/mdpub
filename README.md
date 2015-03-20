# mdpub

Kindle本をMarkdownで書くためのソフト。執筆スタイルはこんな感じ。

- 文章はvimでMarkdownで書く
- [previm](https://github.com/kannokanno/previm)でプレビュー
- 図はInkScapeあたりで描く

書き上がった`*.md`をまとめてHTMLヘ変換できれば、あとは適当にcssを組み合わせてkindlegenに食わせればいいなぁ、と思って作ったソフトがmdpub。

    $ java -jar mdpub.jar ~/notes/jsl js css img html    # ブラウザ用。
    $ java -jar mdpub.jar ~/notes/jsl js css img kin     # Kindle用。

- ブラウザ用にはシンタックスハイライトとGitHub風CSSを組み合わせ
- Kindle用には自作CSS
- Markdown処理には[pegdown](https://github.com/sirthias/pegdown)を使用

実行するにはjs, css, imgを別途用意する必要あり。

- js/highlighht.pack.js
- css/github.css
- css/hljs.css
- img/para.png
- img/dirty-shade.png
- 要JRE8

