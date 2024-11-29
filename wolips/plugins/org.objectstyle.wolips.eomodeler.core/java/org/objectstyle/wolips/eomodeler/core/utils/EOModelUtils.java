package org.objectstyle.wolips.eomodeler.core.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.objectstyle.wolips.baseforplugins.util.StringUtils;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EOModelReferenceFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.core.model.IEOEntityRelative;

import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagram;
import ch.rucotec.wolips.eomodeler.core.model.AbstractDiagramCollection;
import ch.rucotec.wolips.eomodeler.core.model.EOERDiagram;

public class EOModelUtils {
	public static String getOperationLabel(String operation, List objs) {
		StringBuffer operationLabel = new StringBuffer();
		operationLabel.append(operation);
		if (objs != null) {
			if (objs.size() == 1) {
				Object obj = objs.get(0);
				if (obj instanceof EOModelObject) {
					operationLabel.append(" ");
					operationLabel.append(((EOModelObject) obj).getName());
				}
			} else if (objs.size() > 1) {
				operationLabel.append(" ");
				operationLabel.append(objs.size());
				Class objType = null;
				for (Object obj : objs) {
					Class thisObjType = obj.getClass();
					if (objType == null) {
						objType = thisObjType;
					} else if (!objType.isAssignableFrom(thisObjType)) {
						objType = null;
						break;
					}
				}
				operationLabel.append(" ");
				if (objType == null) {
					operationLabel.append("Items");
				} else {
					operationLabel.append(StringUtils.toPlural(StringUtils.toShortPrettyClassName(objType.getName())));
				}
			}
		}
		return operationLabel.toString();
	}

	public static Set<EOModelObject> getRecommendedDeletions(Object[] selectedObjects) {
		Set<EOModelReferenceFailure> beforeReferenceFailures = EOModelUtils.getReferenceFailures(selectedObjects); 
		Set<EOModelObject> allRecommendedObjectsSet = new HashSet<EOModelObject>();
		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof EOModelObject) {
				EOModelObject modelObject = (EOModelObject) selectedObject;
				allRecommendedObjectsSet.add(modelObject);
			}
		}
		for (EOModelReferenceFailure referenceFailure : beforeReferenceFailures) {
			Set<EOModelObject> recommendedDeletions = referenceFailure.getRecommendedDeletions();
			for (EOModelObject recommendedDelete : recommendedDeletions) {
				allRecommendedObjectsSet.add(recommendedDelete);
			}
		}
		Object[] recommendedObjects = allRecommendedObjectsSet.toArray();
		Set<EOModelReferenceFailure> afterReferenceFailures = EOModelUtils.getReferenceFailures(recommendedObjects);
		if (beforeReferenceFailures.size() != afterReferenceFailures.size()) {
			allRecommendedObjectsSet = EOModelUtils.getRecommendedDeletions(recommendedObjects);
		}
		return allRecommendedObjectsSet;
	}

	public static Set<EOModelReferenceFailure> getReferenceFailures(Object[] selectedObjects) {
		Set<EOModelObject> deletedObjects = new HashSet<EOModelObject>();
		Set<EOModelReferenceFailure> referenceFailures = new HashSet<EOModelReferenceFailure>();
		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof EOModelObject) {
				EOModelObject modelObject = (EOModelObject) selectedObject;
				deletedObjects.add(modelObject);
				referenceFailures.addAll(modelObject.getReferenceFailures());
			}
		}
		
		for (EOModelObject deletedObject : deletedObjects) {
			for (EOModelReferenceFailure referenceFailure : new HashSet<EOModelReferenceFailure>(referenceFailures)) {
				if (referenceFailure.getReferencingObject().equals(deletedObject)) {
					referenceFailures.remove(referenceFailure);
				}
			}
		}

		return referenceFailures;
	}

	public static IUndoContext getUndoContext(Object obj) {
		EOModel model = EOModelUtils.getRelatedModel(obj);
		String label = model == null ? "No Model" : model.getName();
		return new ObjectUndoContext(model, label);
	}

	public static EOModelObject getRelated(Class<? extends EOModelObject> type, EOModelObject obj) {
		EOModelObject relatedObj;
		if (EOModel.class.isAssignableFrom(type)) {
			relatedObj = EOModelUtils.getRelatedModel(obj);
		} else if (EOEntity.class.isAssignableFrom(type)) {
			relatedObj = EOModelUtils.getRelatedEntity(obj);
		} else if (EOStoredProcedure.class.isAssignableFrom(type)) {
			if (obj instanceof EOStoredProcedure) {
				relatedObj = obj;
			} else if (obj instanceof EOArgument) {
				relatedObj = ((EOArgument) obj).getStoredProcedure();
			} else {
				relatedObj = null;
			}
		} else if (AbstractDiagramCollection.class.isAssignableFrom(type)) { //SAVAS somit weiss der Selector zu wem das selektierte objekt gehoert (wird beim contextmenu vom DiagramCollection gebraucht).
			if (obj instanceof AbstractDiagramCollection) {
				relatedObj = obj;
			} else if (obj instanceof AbstractDiagram) {
				relatedObj = ((AbstractDiagram) obj)._getModelParent(); 
			} else {
				relatedObj = null;
			}
		} else {
			relatedObj = null;
		}
		return relatedObj;
	}

	public static EOModel getRelatedModel(Object obj) {
		EOModel model = null;
		if (obj instanceof EOModel) {
			model = (EOModel) obj;
		} else if (obj instanceof IEOEntityRelative) {
			model = ((IEOEntityRelative) obj).getEntity().getModel();
		} else if (obj instanceof EOStoredProcedure) {
			model = ((EOStoredProcedure) obj).getModel();
		} else if (obj instanceof EOArgument) {
			model = ((EOArgument) obj).getStoredProcedure().getModel();
		} else if (obj instanceof EODatabaseConfig) {
			model = ((EODatabaseConfig) obj).getModel();
		} else if (obj instanceof Object[]) {
			Object[] objs = (Object[]) obj;
			for (Object arrayObj : objs) {
				model = EOModelUtils.getRelatedModel(arrayObj);
				if (model != null) {
					break;
				}
			}
		}
		return model;
	}

	public static EOEntity getRelatedEntity(Object obj) {
		EOEntity entity = null;
		if (obj instanceof IEOEntityRelative) {
			entity = ((IEOEntityRelative) obj).getEntity();
		}
		return entity;
	}

	public static EOStoredProcedure getRelatedStoredProcedure(Object obj) {
		EOStoredProcedure storedProcedure = null;
		if (obj instanceof EOStoredProcedure) {
			storedProcedure = (EOStoredProcedure) obj;
		} else if (obj instanceof EOArgument) {
			storedProcedure = ((EOArgument) obj).getStoredProcedure();
		}
		return storedProcedure;
	}
}
