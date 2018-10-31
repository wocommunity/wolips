package ch.rucotec.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectstyle.woenvironment.plist.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.model.DuplicateNameException;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModelMap;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.ISortableEOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.UserInfoableEOModelObject;

public abstract class AbstractDiagram<T extends AbstractDiagramGroup> extends UserInfoableEOModelObject<T> implements ISortableEOModelObject {

	public static final String NAME = "name";
	public static final String DIAGRAMS = "diagrams";
	
	private String myName;
	private Set<EOEntity> myEntities;
	private Set<AbstractEOEntityDiagram> myDiagramEntities;
	private T myDiagramGroup;
	
	public AbstractDiagram() {
		myDiagramEntities = new LinkedHashSet<AbstractEOEntityDiagram>();
		myEntities = new LinkedHashSet<EOEntity>();
	}
	
	public AbstractDiagram(String name) {
		this();
		myName = name;
	}
	
	public void addEntityDiagram(AbstractEOEntityDiagram entityDiagram) {
		myDiagramEntities.add(entityDiagram);
	}
	
	public Set<AbstractEOEntityDiagram> getDiagramEntities() {
		return myDiagramEntities;
	}
	
	protected abstract AbstractDiagram createDiagram(String name);
	
	public void saveToFile(File modelFolder) throws PropertyListParserException, IOException {
		for (AbstractEOEntityDiagram entityDiagram : myDiagramEntities) {
			entityDiagram.saveToFile(modelFolder);
		}
	}
	
	public void loadFromMap(EOModelMap _diagramMap, Set<EOModelVerificationFailure> _failures) {
		
	}
	
	protected AbstractDiagram cloneDiagram() {
		AbstractDiagram diagram = createDiagram(myName);
		cloneIntoDiagram(diagram);
		_cloneUserInfoInto(diagram);
		return diagram;
	}
	
	public void cloneIntoDiagram(AbstractDiagram diagram) {
		diagram.myDiagramEntities = myDiagramEntities; 
	}
	
	@SuppressWarnings("unused")
	public void setName(String name, boolean fireEvents) throws DuplicateNameException {
		String oldName = myName;
		for (AbstractEOEntityDiagram entityDiagram : myDiagramEntities) {
			if (entityDiagram.getDiagramDimensions().get(oldName) != null) {
				EOEntityDiagramDimension diagramDimension = entityDiagram.getDiagramDimensions().get(oldName);
				entityDiagram.getDiagramDimensions().remove(oldName);
				entityDiagram.getDiagramDimensions().put(name, diagramDimension);
			}
		}
		myName = name;
		if (fireEvents) {
			firePropertyChange(AbstractDiagram.NAME, oldName, myName);
		}
	}
	
	@Override
	public T _getModelParent() {
		return myDiagramGroup;
	}
	
	public void _setModelParent(T diagramGroup) {
		myDiagramGroup = diagramGroup;
	}
	
	@Override
	public String getName() {
		return myName;
	}

	public Set<EOEntity> getEntities() {
		return myEntities;
	}
}
