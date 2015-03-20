package jp.dip.gpsoft.utils;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jp.dip.gpsoft.mdpub.Application;

import org.apache.commons.io.FileUtils;

/**
 * ユーティリティ。汎用な便利メソッド群を定義。すべてstatic。
 * IOExceptionはUncheckedIOExceptionでラップして再throwする。
 */
public class Utils {
    /**
     * プラットフォームに応じた改行コードを得る。
     * 
     * @return 改行コード。
     */
    public static String eol() {
        return String.format("%n");
    }

    public static Path replaceExt(Path path, String ext) {
        String fname = path.getFileName().toString();
        return Paths.get(fname.replaceFirst("\\.[^\\.]+$", ext));
    }

    /**
     * ディレクトリ階層ごとコピーする。
     * 
     * @param src コピー元ディレクトリのパス。
     * @param dst コピー先ディレクトリのパス。
     */
    public static void copyDirectory(Path src, Path dst) {
        try {
            FileUtils.copyDirectory(new File(src.toString()),
                    new File(dst.toString()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * UTF-8のテキストファイルを丸ごと読み込んで文字列で返す。
     * 
     * @param fpath ファイルパス。
     * @return ファイル内容。
     */
    public static String slurp(String fpath) {
        Path path = Paths.get(fpath);
        String content = "";
        try {
            content =
                new String(Files.readAllBytes(path),
                        StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return content;
    }

    /**
     * Jarリソース内にあるUTF-8のテキストファイルを丸ごと読み込んで文字列で返す。
     * 
     * @param rpath リソースファイルパス。
     * @return ファイル内容。
     */
    public static String slurpResource(String rpath) {
        String content = "";
        try (BufferedReader br =
            new BufferedReader(new InputStreamReader(
                    Application.class.getResourceAsStream(rpath),
                    StandardCharsets.UTF_8))) {
            content = br.lines().collect(joining(eol()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return content;
    }

    /**
     * 文字列をUTF-8のテキストファイルに書き出す。
     * 
     * @param fpath ファイルパス。
     * @param content ファイル内容。
     */
    public static void spit(String fpath, String content) {
        Path path = Paths.get(fpath);
        try {
            Files.write(path,
                    content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
