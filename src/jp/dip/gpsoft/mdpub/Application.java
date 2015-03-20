package jp.dip.gpsoft.mdpub;

import static java.util.stream.Collectors.toList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * アプリケーションクラス。エントリポイントを定義する。
 * <p>
 * 独自の実行時エラーとしてAppExceptionを定義し、下位レイヤからのエラーを受け取る。
 * AppExceptionをキャッチすると、設定されたメッセージを表示して終了する。
 */
public class Application {
    /**
     * アプリの実行時エラークラス。シリアライズには非対応。
     */
    @SuppressWarnings("serial")
    static class AppException extends RuntimeException {
        public AppException(String msg) {
            super(msg);
        }
    }

    /**
     * エントリポイント。
     * <p>
     * 起動オプションには、入力ディレクトリ(IN-dir)、サブディレクトリ(subdir)、出力ディレクトリ(OUT-
     * dir) を指定する。 subdirは複数指定可能。 subdirとOUT-dirはIN-dirからの相対パスで指定。
     * subdirとOUT-dirは省略可能。省略時は、subdirなし、OUT-dirはIN-dirと同じ、と解釈する。
     * <p>
     * 使用例: mdpub ~/md/proj1 img css js html
     * 
     * @param args 起動オプション。
     */
    public static void main(String[] args) {
        Logger logger = LogManager.getLogger();
        logger.info("The App started.");

        // usage表示。
        if (args.length == 0) {
            showUsage();
            return;
        }

        try {
            // 起動オプションに応じてコンフィグ。
            MarkdownPublisher.Configurations cfgs =
                configure(args);
            logger.info("  in: {}", cfgs.inDir.toString());
            logger.info("  out: {}", cfgs.outDir.toString());
            cfgs.subDirs.forEach(path -> logger.info("  sub: {}",
                    path.toString()));

            // 出版!!
            MarkdownPublisher pub = new MarkdownPublisher(cfgs);
            pub.publish();
            logger.info("Done.");
        } catch (AppException e) {
            logger.error("Aborted for: " + e.getMessage());
            return;
        }
    }

    /**
     * usageを表示する。
     */
    private static void showUsage() {
        System.out
                .println("usage: mdpub IN-dir [subdir...] [OUT-dir]¥n"
                        + "It processes markdown files in IN-dir and copy all subdirs under IN-dir.¥n"
                        + "Subdirs and OUT-dir may be either relative or absolute.¥n"
                        + "  Ex: mdpub ~/md/proj1 img css js html¥n"
                        + "In this case, html is the OUT-dir.");
    }

    /**
     * 起動オプションに応じてコンフィグする。
     * 
     * @param args 起動オプション。
     * @return コンフィグ結果。
     */
    private static MarkdownPublisher.Configurations configure(
            String[] args) {
        assert args.length > 0;

        MarkdownPublisher.Configurations cfgs =
            new MarkdownPublisher.Configurations();
        // HTMLテンプレート。
        // TODO: 現状、Jarリソースから取得。これも起動オプションで変えれるようにすべき。
        cfgs.topRes = "/top.html";
        cfgs.bottomRes = "/bottom.html";
        if (args[args.length - 1].equals("kin")) {  // スペシャル仕様!!
            // Kindle用のセットに変える。
            cfgs.topRes = "/topkin.html";
            cfgs.bottomRes = "/bottomkin.html";
            cfgs.isForKindle = true;
        }

        // IN-dir.
        cfgs.inDir = Paths.get(args[0]);
        existsOrAppException(cfgs.inDir);

        // OUT-dir.
        // 引数が1つならIN-dirと同じ。
        // 2つ以上なら最後の引数がOUT-dir。
        if (args.length <= 1) {
            cfgs.outDir = cfgs.inDir;
        } else {
            cfgs.outDir =
                cfgs.inDir.resolve(args[args.length - 1]);
        }

        // subdirs.
        // 最初と最後の引数以外がsubdir。
        if (args.length <= 2) {
            cfgs.subDirs = new ArrayList<Path>();
        } else {
            cfgs.subDirs =
                Arrays.stream(args, 1, args.length - 1)
                        .map(cfgs.inDir::resolve)
                        .peek(Application::existsOrAppException)
                        .collect(toList());
        }
        return cfgs;
    }

    /**
     * ファイルの有無を調べて、無ければ実行時エラーとするConsumer。
     * 
     * @param path ファイルパス。
     */
    private static void existsOrAppException(Path path) {
        if (Files.exists(path)) return;
        throw new AppException(path.toString()
                + " does not exist.");
    }
}
