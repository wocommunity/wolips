package tk.eclipse.plugin.htmleditor.gefutils;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Naoki Takezoe
 */
public class LabelArrowConnection extends PolylineConnection {
	
	private Label label = new Label();
	
	public LabelArrowConnection(){
		this(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	}
	
	public LabelArrowConnection(Color color){
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
		setConnectionRouter(new BendpointConnectionRouter());
		setTargetDecoration(new PolygonDecoration());
		if(color!=null){
			setForegroundColor(color);
		}
	}
	
	public Label getLabel(){
		return label;
	}
	
	public void setText(String text){
		label.setText(text);
	}
	
}
