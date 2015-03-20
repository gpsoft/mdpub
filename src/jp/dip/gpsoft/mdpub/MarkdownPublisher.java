package jp.dip.gpsoft.mdpub;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import jp.dip.gpsoft.mdpub.Application.AppException;
import jp.dip.gpsoft.utils.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Markdown処理の中核クラス。使い方は、
 * <ol>
 * <li>コンストラクタにコンフィグ情報を渡してnewし
 * <li>publish()を呼ぶだけ。
 * </ol>
 */
public class MarkdownPublisher {
    private Configurations cfgs;

    /**
     * コンフィグ情報を管理するクラス。フィールドはpublic。
     */
    static class Configurations {
        // TODO: PegDownライブラリのオプションも保持したいところ。
        public Path inDir;
        public Path outDir;
        public List<Path> subDirs;
        public String topRes;      // テンプレートHTMLリソース(前半)へのパス。
        public String bottomRes;   // テンプレートHTMLリソース(後半)へのパス。
        public boolean isForKindle = false;  // Kindle向けか?
    }

    /**
     * コンストラクタ。
     * 
     * @param cfgs コンフィグ情報。
     */
    public MarkdownPublisher(Configurations cfgs) {
        this.cfgs = cfgs;
    }

    /**
     * 出版する。
     */
    public void publish() {
        try {
            // OUT-dirを生成してsubdirたちをコピー。
            if (!cfgs.inDir.equals(cfgs.outDir)) {
                Files.createDirectories(cfgs.outDir);
                cfgs.subDirs.forEach(this::copySubDir);
            }

            // Markdownファイルを処理。
            markdownsToHtmls();
        } catch (IOException | UncheckedIOException e) {
            Logger logger = LogManager.getLogger();
            logger.fatal(e.getStackTrace());
            throw new AppException("an IOException: "
                    + e.getMessage());
        }
    }

    /**
     * subdirをOUT-dirへコピー。
     */
    private void copySubDir(Path subdir) {
        Utils.copyDirectory(subdir,
                cfgs.outDir.resolve(subdir.getFileName()));
    }

    /**
     * IN-dir直下のMarkdownファイルすべてを、HTMLファイルへ変換して保存。
     * 
     * @throws IOException
     */
    private void markdownsToHtmls() throws IOException {
        try (Stream<Path> seq =
            Files.find(cfgs.inDir,
                    1,  // 1階層だけ検索。
                    (path, attr) -> path.toString()
                            .endsWith(".md")
                            && attr.isRegularFile())) {
            seq.forEach(path -> buildHtml(path, cfgs.outDir));
        }
    }

    /**
     * MarkdownファイルをHTMLファイルへ変換して保存。
     * 
     * @param md Markdownファイルのパス。
     * @param htmlDir HTMLファイルの出力先ディレクトリ。
     */
    private void buildHtml(Path md, Path htmlDir) {
        String suffix =
            !cfgs.isForKindle ? "" : Stream.of(
                    "<a id=\"fileName\">",
                    Utils.replaceExt(md.getFileName(), ".html")
                            .toString(), "</a>")
                    .collect(joining());;
        HtmlFileBuilder.builder().topFromResource(cfgs.topRes)
                .bottomFromResource(cfgs.bottomRes)
                .bodyFromMarkdownFile(md).bodySuffix(suffix)
                .buildAndSave(htmlDir);
    }
}
