package org.objectstyle.wolips.eomodeler.doc;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.thirdparty.velocity.WOLipsVelocityUtils;
import org.osgi.framework.Bundle;

public class EOModelGraffleGenerator {
	private int _id;

	private Map<Object, Integer> _ids;

	private VelocityEngine _velocityEngine;

	private VelocityContext _context;

	private EOModelGroup _modelGroup;

	private File _outputFile;

	private FileWriter _outputWriter;

	public EOModelGraffleGenerator(boolean insideEclipse, EOModelGroup modelGroup, File templatePath, File outputFile) throws Exception {
		Bundle templateBundle = insideEclipse ? Activator.getDefault().getBundle() : null;

		_id = 2;

		_velocityEngine = new VelocityEngine();
		_velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, org.apache.velocity.runtime.log.NullLogSystem.class.getName());
		StringBuffer templatePaths = new StringBuffer();
		templatePaths.append(".");
		if (templatePath != null) {
			templatePaths.append(",");
			templatePaths.append(templatePath.getAbsolutePath());
		}
		_velocityEngine.setProperty("resource.loader", "file,class");
		_velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		_velocityEngine.setProperty("file.resource.loader.path", templatePaths.toString());
		_velocityEngine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

		_velocityEngine.init();
		_context = new VelocityContext();
		_ids = new HashMap<Object, Integer>();
		_modelGroup = modelGroup;
		_outputFile = outputFile;
	}

	public Integer nextID() {
		return Integer.valueOf(++_id);
	}

	protected void generateConnections() throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		for (EOModel model : _modelGroup.getModels()) {
			for (EOEntity entity : model.getEntities()) {
				if (entity.isPrototype()) {
					continue;
				}
				EOEntity parentEntity = entity.getParent();
				if (parentEntity != null) {
					_context.put("id", _ids.get(entity.getName() + "Parent"));
					_context.put("fromID", _ids.get(entity));
					_context.put("toID", _ids.get(parentEntity));
					_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/subEntity.vm"));
				}
				for (EORelationship relationship : entity.getRelationships()) {
					_context.put("relationship", relationship);
					_context.put("id", _ids.get(relationship.getFullyQualifiedName() + "Line"));
					_context.put("fromID", _ids.get(relationship));
					_context.put("toID", _ids.get(relationship.getDestination()));
					if (relationship.isToOne()) {
						_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/toOne.vm"));
					} else if (relationship.isToMany()) {
						_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/toMany.vm"));
					}
				}
			}
		}
	}

	protected void generateNodes() throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		for (EOModel model : _modelGroup.getModels()) {
			for (EOEntity entity : model.getEntities()) {
				if (entity.isPrototype()) {
					continue;
				}
				_context.put("id", _ids.get(entity.getName() + "Group"));
				_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/groupHeader.vm"));

				int entityX = (int) (Math.random() * 2000);
				int entityY = (int) (Math.random() * 2000);

				int marginTop = 26;
				int attributeHeight = 20;
				int relationshipHeight = 20;
				int verticalSpacer = 20;
				int marginBottom = 20;
				int width = 220;

				int attributeNum = 0;
				for (EOAttribute attribute : entity.getAttributes()) {
					_context.put("attribute", attribute);
					_context.put("id", _ids.get(attribute));
					_context.put("x", entityX);
					_context.put("y", entityY + marginTop + attributeHeight * attributeNum);
					_context.put("width", width);
					_context.put("height", attributeHeight);
					_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/attribute.vm"));
					attributeNum++;
				}

				int relationshipNum = 0;
				for (EORelationship relationship : entity.getRelationships()) {
					_context.put("relationship", relationship);
					_context.put("id", _ids.get(relationship));
					_context.put("x", entityX);
					_context.put("y", entityY + marginTop + attributeHeight * attributeNum + verticalSpacer + relationshipHeight * relationshipNum);
					_context.put("width", width);
					_context.put("height", relationshipHeight);
					_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/relationship.vm"));
					relationshipNum++;
				}

				int height = marginTop + entity.getAttributes().size() * attributeHeight + verticalSpacer + entity.getRelationships().size() * relationshipHeight + marginBottom;
				_context.put("entity", entity);
				_context.put("id", _ids.get(entity));
				_context.put("x", entityX);
				_context.put("y", entityY);
				_context.put("width", width);
				_context.put("height", height);
				_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/entity.vm"));

				_context.put("id", _ids.get(entity.getName() + "Group"));
				_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/groupFooter.vm"));
			}
		}
	}

	public void generate() throws Exception {
		_outputWriter = new FileWriter(_outputFile);
		try {
			for (EOModel model : _modelGroup.getModels()) {
				_ids.put(model, nextID());
				for (EOEntity entity : model.getEntities()) {
					if (entity.isPrototype()) {
						continue;
					}
					_ids.put(entity, nextID());
					_ids.put(entity.getName() + "Group", nextID());
					EOEntity parentEntity = entity.getParent();
					if (parentEntity != null) {
						_ids.put(entity.getName() + "Parent", nextID());
					}
					for (EOAttribute attribute : entity.getAttributes()) {
						_ids.put(attribute, nextID());
					}
					for (EORelationship relationship : entity.getRelationships()) {
						_ids.put(relationship, nextID());
						_ids.put(relationship.getFullyQualifiedName() + "Line", nextID());
					}
				}
			}

			_context.put("id", nextID());
			_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/header.vm"));
			generateNodes();
			generateConnections();
			_outputWriter.append(WOLipsVelocityUtils.writeTemplateToString(_velocityEngine, _context, "graffle/footer.vm"));
		} finally {
			_outputWriter.close();
		}
	}
}
