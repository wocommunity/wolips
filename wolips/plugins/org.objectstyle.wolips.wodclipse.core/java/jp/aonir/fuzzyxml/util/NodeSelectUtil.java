package jp.aonir.fuzzyxml.util;

import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;

/**
 * ノードを選択するためのユーティリティメソッドを提供します。
 */
public class NodeSelectUtil {
	
	/**
	 * 子ノードの中からフィルタにマッチしたものを配列で返却します。
	 * 検索対象は引数で渡した要素の直下のノードのみです。
	 * 
	 * @param element 検索対象の要素
	 * @param filter フィルタ
	 * @return フィルタにマッチした要素の配列
	 */
	public static FuzzyXMLNode[] getChildren(FuzzyXMLElement element, NodeFilter filter){
		ArrayList result = new ArrayList();
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
			if(filter.filter((FuzzyXMLNode)children[i])){
				result.add((FuzzyXMLNode)children[i]);
			}
		}
		return (FuzzyXMLNode[])result.toArray(new FuzzyXMLNode[result.size()]);
	}
	
	/**
	 * 子孫のノードの中からフィルタにマッチしたものを配列で返却します。
	 * 引数で渡した要素から再帰的に検索を行います。
	 * 
	 * @param element 検索対象の要素
	 * @param filter フィルタ
	 * @return フィルタにマッチした要素の配列
	 */
	public static FuzzyXMLNode[] getNodeByFilter(FuzzyXMLElement element, NodeFilter filter){
		ArrayList result = new ArrayList();
		if(filter.filter(element)){
			result.add(element);
		}
		searchNodeByFilter(element, filter, result);
		return (FuzzyXMLElement[])result.toArray(new FuzzyXMLElement[result.size()]);
	}
	
	private static void searchNodeByFilter(FuzzyXMLElement element, NodeFilter filter,List result){
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
			if(filter.filter(children[i])){
				result.add(children[i]);
			}
			if(children[i] instanceof FuzzyXMLElement){
				searchNodeByFilter((FuzzyXMLElement)children[i], filter, result);
			}
		}
	}
	
	/**
	 * 引数で渡した要素を再帰的に検索し、id属性がマッチする要素を返却します。
	 * 要素が見つからない場合はnullを返却します。
	 * また、同じid属性を持つ要素が複数存在した場合は最初に発見した要素を返却します。
	 * 
	 * @param element 検索対象の要素
	 * @param id 検索するid属性の値
	 * @return id属性の値がマッチした要素
	 */
	public static FuzzyXMLElement getElementById(FuzzyXMLElement element, String id){
		FuzzyXMLElement[] elements = getElementByAttribute(element, "id", id);
		if(elements.length==0){
			return null;
		} else {
			return elements[0];
		}
	}
	
	/**
	 * 引数で渡した要素を再帰的に検索し、属性がマッチする要素を返却します。
	 * 
	 * @param element 検索対象の要素
	 * @param name 検索する属性名
	 * @param value 検索する属性値
	 * @return 属性名と属性値がマッチした要素の配列
	 */
	public static FuzzyXMLElement[] getElementByAttribute(FuzzyXMLElement element, String name, String value){
		ArrayList result = new ArrayList();
		searchElementByAttribute(element, name, value, result);
		return (FuzzyXMLElement[])result.toArray(new FuzzyXMLElement[result.size()]);
	}
	
	private static void searchElementByAttribute(FuzzyXMLElement element, String name, String value, List result){
		if(value.equals(element.getAttributeValue(name))){
			result.add(element);
		}
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof FuzzyXMLElement){
				searchElementByAttribute(element, name, value, result);
			}
		}
	}
	
	/**
	 * 引数で渡した要素を再帰的に検索し、タグ名がマッチする要素を返却します。
	 * 
	 * @param element 検索対象の要素
	 * @param name タグ名
	 * @return タグ名がマッチした要素の配列
	 */
	public static FuzzyXMLElement[] getElementByTagName(FuzzyXMLElement element, String name){
		ArrayList result = new ArrayList();
		searchElementByTagName(element, name, result);
		return (FuzzyXMLElement[])result.toArray(new FuzzyXMLElement[result.size()]);
	}
	
	private static void searchElementByTagName(FuzzyXMLElement element, String name, List result){
		if(element.getName().equals(name)){
			result.add(element);
		}
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof FuzzyXMLElement){
				searchElementByTagName(element, name, result);
			}
		}
	}

}
