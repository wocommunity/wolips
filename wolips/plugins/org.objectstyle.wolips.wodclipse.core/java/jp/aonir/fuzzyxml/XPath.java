package jp.aonir.fuzzyxml;

import java.util.List;

import jp.aonir.fuzzyxml.xpath.FuzzyXMLNodePointerFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;

/**
 * FuzzyXMLのノードツリーをXPathで検索するためのユーティリティクラス。
 */
public class XPath {
    
    static {
    	JXPathContextReferenceImpl.addNodePointerFactory(new FuzzyXMLNodePointerFactory());
    }
    
    public static FuzzyXMLNode selectSingleNode(FuzzyXMLElement element,String xpath){
        JXPathContext ctx = JXPathContext.newContext(element);
        return (FuzzyXMLNode)ctx.selectSingleNode(xpath);
    }

    @SuppressWarnings("unchecked")
    public static FuzzyXMLNode[] selectNodes(FuzzyXMLElement element,String xpath){
        JXPathContext ctx = JXPathContext.newContext(element);
        List<FuzzyXMLNode> list = ctx.selectNodes(xpath);
        return list.toArray(new FuzzyXMLNode[list.size()]);
    }
    
    public static Object getValue(FuzzyXMLElement element,String xpath){
        JXPathContext ctx = JXPathContext.newContext(element);
        return ctx.getValue(xpath);
    }
}
