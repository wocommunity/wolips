package org.objectstyle.wolips.componenteditor.actions;

import org.objectstyle.wolips.componenteditor.part.ComponentEditorPart;

public class ComponentInserter extends InsertComponentAction {
	private String _componentName;

	public ComponentInserter(ComponentEditorPart componentEditor, String componentName, boolean inline) {
		setComponentEditorPart(componentEditor);
		InsertComponentSpecification ics = new InsertComponentSpecification(componentName);
		ics.setInline(inline);
		setComponentSpecification(ics);
		_componentName = componentName;
	}

	@Override
	public String getComponentInstanceNameSuffix() {
		return "Component";
	}

	@Override
	public String getComponentName() {
		return _componentName;
	}

}
