package jp.dip.gpsoft.mdpub;

import static java.util.stream.Collectors.joining;
import static org.pegdown.Extensions.FENCED_CODE_BLOCKS;
import static org.pegdown.Extensions.TABLES;

import java.nio.file.Path;
import java.util.stream.Stream;

import jp.dip.gpsoft.utils.Utils;

import org.pegdown.PegDownProcessor;

/**
 * HTMLビルダー。いわゆる「流れるような」インターフェイスを採用。
 * 使い方は、以下の部品をセットしてbuildAndSave()するだけ。
 * <ul>
 * <li>HTMLの前半部分。
 * <li>HTMLのボディ部分。
 * <li>HTMLの後半部分。
 * </ul>
 * ボディ部の前後に任意の文字列を指定可能。PegDownライブラリのオプションもセット可能。
 */
public class HtmlFileBuilder {
    private String topContent;    // 前半部分。
    private String bodyPrefix;    // ボディ部の前に付ける文字列。
    private String bodySuffix;    // ボディ部の後ろに付ける文字列。
    private String bottomContent; // 後半部分。
    private Path bodyPath;        // ボディ部の元になるMarkdownファイルのパス。
    private int pegDownOptions;   // PegDownライブラリのオプション。

    /**
     * コンストラクタ。
     */
    private HtmlFileBuilder() {
        // 以下3つはオプションなのでデフォルト値で初期化。
        pegDownOptions = TABLES | FENCED_CODE_BLOCKS;
        bodyPrefix = "";
        bodySuffix = "";
    }

    /**
     * ファクトリメソッド。
     * 
     * @return ビルダーインスタンス。
     */
    public static HtmlFileBuilder builder() {
        return new HtmlFileBuilder();
    }

    /**
     * HTML前半部をJarリソースパスで指定。
     * 
     * @param path Jarリソースパス。
     * @return ビルダーインスタンス。
     */
    public HtmlFileBuilder topFromResource(String path) {
        topContent = Utils.slurpResource(path);
        return this;
    }

    /**
     * ボディ部の前に味付け。
     * 
     * @param pref 味付け。
     * @return ビルダーインスタンス。
     */
    public HtmlFileBuilder bodyPrefix(String pref) {
        bodyPrefix = pref;
        return this;
    }

    /**
     * ボディ部の後ろに味付け。
     * 
     * @param suff 味付け。
     * @return ビルダーインスタンス。
     */
    public HtmlFileBuilder bodySuffix(String suff) {
        bodySuffix = suff;
        return this;
    }

    /**
     * HTML後半部をJarリソースパスで指定。
     * 
     * @param path Jarリソースパス。
     * @return ビルダーインスタンス。
     */
    public HtmlFileBuilder bottomFromResource(String path) {
        bottomContent = Utils.slurpResource(path);
        return this;
    }

    /**
     * ボディ部をMarkdownファイルパスで指定。
     * 
     * @param path Markdownファイルのパス。
     * @return ビルダーインスタンス。
     */
    public HtmlFileBuilder bodyFromMarkdownFile(Path path) {
        bodyPath = path;
        return this;
    }

    /**
     * PegDownライブラリのオプションを変更。
     * 
     * @param options オプションフラグマップ。
     * @return ビルダーインスタンス。
     */
    public HtmlFileBuilder alterPegDownOptions(int options) {
        pegDownOptions = options;
        return this;
    }

    /**
     * HTMLを作って保存。
     * 
     * @param dstDir HTMLファイルの保存先ディレクトリ。
     */
    public void buildAndSave(Path dstDir) {
        String in = bodyPath.toAbsolutePath().toString();
        Path fname =
            Utils.replaceExt(bodyPath.getFileName(), ".html");
        String out = dstDir.resolve(fname).toString();

        PegDownProcessor pdp =
            new PegDownProcessor(pegDownOptions);
        String snipet = pdp.markdownToHtml(Utils.slurp(in));
        String html =
            Stream.of(topContent, bodyPrefix, snipet, bodySuffix,
                    bottomContent).collect(joining());
        Utils.spit(out, html);
    }
}
