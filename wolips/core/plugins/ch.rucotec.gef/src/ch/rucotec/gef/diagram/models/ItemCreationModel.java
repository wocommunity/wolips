package ch.rucotec.gef.diagram.models;

import ch.rucotec.gef.diagram.parts.DiagramNodePart;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The {@link ItemCreationModel} is sued to store the creation state in the application.
 * 
 */
public class ItemCreationModel {

	public enum Type {
		None,
		Node,
		Connection
	};
	
	private ObjectProperty<Type> typeProperty = new SimpleObjectProperty<ItemCreationModel.Type>(Type.None);

	private ObjectProperty<DiagramNodePart> sourceProperty = new SimpleObjectProperty<>();

	public ObjectProperty<Type> getTypeProperty() {
		return typeProperty;
	}

	public Type getType() {
		return typeProperty.getValue();
	}

	public void setType(Type type) {
		this.typeProperty.setValue(type);
	}

	public void setSource(DiagramNodePart source) {
		this.sourceProperty.setValue(source);;
	}
	
	public DiagramNodePart getSource() {
		return sourceProperty.getValue();
	}
	
	public ObjectProperty<DiagramNodePart> getSourceProperty() {
		return sourceProperty;
	}
	
}