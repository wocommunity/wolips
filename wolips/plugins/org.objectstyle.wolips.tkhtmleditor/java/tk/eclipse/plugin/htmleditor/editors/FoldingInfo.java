package tk.eclipse.plugin.htmleditor.editors;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

/**
 * @author Naoki Takezoe
 */
public class FoldingInfo {
	
	private int start;
	private int end;
	private String type;
	
	public FoldingInfo(int start){
		this(start, -1, null);
	}
	
	public FoldingInfo(int start, int end){
		this(start, end, null);
	}
	
	public FoldingInfo(int start, String type){
		this(start, -1, type);
	}
	
	public FoldingInfo(int start, int end, String type){
		setStart(start);
		setEnd(end);
		setType(type);
	}
	
	
	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static int countUpLineDelimiter(String text,int pos){
		int count = 0;
		if(text.length()-1 > pos){
			char c1 = text.charAt(pos);
			if(c1=='\r' || c1=='\n'){
				count++;
				if(c1=='\r' && text.length()-1 > pos+1){
					if(text.charAt(pos+1)=='\n'){
						count++;
					}
				}
			}
		}
		return count;
	}
	
	public static void applyModifiedAnnotations(ProjectionAnnotationModel model, List list){
		// apply only modified annotations
		Iterator ite = model.getAnnotationIterator();
		while(ite.hasNext()){
			ProjectionAnnotation annotation = (ProjectionAnnotation)ite.next();
			Position pos = model.getPosition(annotation);
			boolean remove = true;
			for(int i=0;i<list.size();i++){
				FoldingInfo info = (FoldingInfo)list.get(i);
				if(info.getStart() == pos.offset && info.getEnd() == pos.offset + pos.length){
					remove = false;
					list.remove(info);
					break;
				}
			}
			if(remove){
				model.removeAnnotation(annotation);
			}
		}
		
		for(int i=0;i<list.size();i++){
			FoldingInfo info = (FoldingInfo)list.get(i);
			Position pos = new Position(info.getStart(),info.getEnd() - info.getStart());
			model.addAnnotation(new ProjectionAnnotation(), pos);
		}
	}

}
