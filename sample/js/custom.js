// onloadにsaveWholeHtml()を仕込む。
function initSavingWholeOnLoad() {
    addEventListener('load', saveWholeHtml, false);
}

// JS実行後のHTMLをファイルに保存する。
function saveWholeHtml() {
    // #fileNameからファイル名を得る。
    var e = document.getElementById('fileName');
    if ( !e ) return;
    var fname = e.innerHTML;

    // #fileNameと#footerを削除。
    e.parentNode.removeChild(e);
    document.body.removeChild(document.getElementById('footer'));

    // 保存。
    var blob = new Blob(
            [document.documentElement.outerHTML],
            {type: "text/plain"});
    a = document.createElement('a');
    a.href = window.URL.createObjectURL(blob);
    a.download = fname;
    a.style = 'display: none;';
    document.body.appendChild(a);
    // appendしておかないとclick()が効かないみたい。
    // また、ダウンロードダイアログが裏に隠れることがあった。
    a.click();
}
