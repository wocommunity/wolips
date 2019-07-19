package ch.rucotec.gef.diagram.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import ch.rucotec.gef.diagram.visuals.node.AbstractDiagramNodeVisual;
import ch.rucotec.gef.diagram.visuals.node.DiagramNodeVisualClassDiagram;
import ch.rucotec.gef.diagram.visuals.node.DiagramNodeVisualERD;
import ch.rucotec.wolips.eomodeler.core.gef.model.DiagramNode;
import ch.rucotec.wolips.eomodeler.core.gef.model.E_DiagramType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;

/**
 * The {@link DiagramNodePart} is responsible to create and update the
 * {@link AbstractDiagramNodeVisual} for a instance of the {@link DiagramNode}.
 * <br/>(documented by GEF)
 */
public class DiagramNodePart extends AbstractContentPart<AbstractDiagramNodeVisual>
implements ITransformableContentPart<AbstractDiagramNodeVisual>, IResizableContentPart<AbstractDiagramNodeVisual> {

	//---------------------------------------------------------------------------
	// ### Custom Methods and Accessors
	//---------------------------------------------------------------------------
	
	@Override
	protected AbstractDiagramNodeVisual doCreateVisual() {
		AbstractDiagramNodeVisual nodeVisual = null;
		DiagramNode node = getContent();
		if (node.getDiagramType() == E_DiagramType.ERDIAGRAM) {
			nodeVisual = new DiagramNodeVisualERD(node);
    	} else if (node.getDiagramType() == E_DiagramType.CLASSDIAGRAM) {
    		nodeVisual = new DiagramNodeVisualClassDiagram(node);
    	}
		return nodeVisual;
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		// Nothing to anchor to
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		// we don't have any children.
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(AbstractDiagramNodeVisual visual) {
		// updating the visual's texts
		DiagramNode node = getContent();
		visual.getTitle().setText(node.getTitle());
		visual.setColor(node.getColor());
		
		// use the IResizableContentPart API to resize the visual
		setVisualSize(getContentSize());
		
		// use the ITransformableContentPart API to position the visual
		setVisualTransform(getContentTransform());
	}

	@Override
	public DiagramNode getContent() {
		return (DiagramNode) super.getContent();
	}

	@Override
	public Dimension getContentSize() {
		return getContent().getBounds().getSize();
	}

	@Override
	public Affine getContentTransform() {
		Rectangle bounds = getContent().getBounds();
		return new Affine(new Translate(bounds.getX(), bounds.getY()));
	}

	@Override
	public void setContentSize(Dimension totalSize) {
		// storing the new size
		getContent().getBounds().setSize(totalSize);
	}

	@Override
	public void setContentTransform(Affine totalTransform) {
		// storing the new position
		Rectangle bounds = getContent().getBounds().getCopy();
		bounds.setX(totalTransform.getTx());
		bounds.setY(totalTransform.getTy());
		getContent().setBounds(bounds);
	}

	@Override
	public void setVisualSize(Dimension totalSize) {
		IResizableContentPart.super.setVisualSize(totalSize);
		// perform layout pass to apply size
		getVisual().layout();
	}

	@Override
	public String toString() {
		return "DiagramNodePart [ " + getContent().getTitle() + " ] [ " + getContent().getEntityDiagram().getEntity().getName() + " ] ";
	}
}