package jp.aonir.fuzzyxml.event;


/**
 * ドキュメントの更新通知を受け取るリスナのインターフェースです。
 * <p>
 * このリスナはDOMツリーの修正時に元になったXML文字列を同期することを目的としています。
 * このインターフェースを実装したクラスをFuzzyXMLDocumentに登録しておくと
 * ドキュメントが変更された場合に以下の情報が通知されます。
 * </p>
 * <ul>
 *   <li>置換するテキスト</li>
 *   <li>置換範囲の開始オフセット</li>
 *   <li>置換範囲の長さ</li>
 * </ul>
 */
public interface FuzzyXMLModifyListener {
    /**
     * DOMツリーの変更時に呼び出されます。
     * 
     * @param evt 更新イベント
     */
    public void modified(FuzzyXMLModifyEvent evt);
}
