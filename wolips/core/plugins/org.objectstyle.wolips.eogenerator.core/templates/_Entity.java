// DO NOT EDIT.  Make changes to ${entity.classNameWithoutPackage}.java instead.
#if ($entity.packageName)
package $entity.packageName;

#end
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class ${entity.prefixClassNameWithoutPackage} extends #if ($entity.parentSet)${entity.parent.classNameWithDefault}#elseif ($EOGenericRecord)${EOGenericRecord}#else EOGenericRecord#end {
	public static final String ENTITY_NAME = "$entity.name";

	// Attributes
#foreach ($attribute in $entity.sortedClassAttributes)
	public static final String ${attribute.uppercaseUnderscoreName}_KEY = "$attribute.name";
#end

	// Relationships
#foreach ($relationship in $entity.sortedClassRelationships)
	public static final String ${relationship.uppercaseUnderscoreName}_KEY = "$relationship.name";
#end

  private static Logger LOG = Logger.getLogger(${entity.prefixClassNameWithoutPackage}.class);

  public $entity.classNameWithoutPackage localInstanceOf${entity.name}(EOEditingContext editingContext) {
    $entity.classNameWithoutPackage localInstance = ($entity.classNameWithoutPackage)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.inherited)
#if ($attribute.userInfo.ERXConstantClassName)
  public $attribute.userInfo.ERXConstantClassName ${attribute.name}() {
    Number value = (Number)storedValueForKey("$attribute.name");
    return ($attribute.userInfo.ERXConstantClassName)value;
  }

  public void set${attribute.capitalizedName}($attribute.userInfo.ERXConstantClassName value) {
    takeStoredValueForKey(value, "$attribute.name");
  }
#else
  public $attribute.javaClassName ${attribute.name}() {
    return ($attribute.javaClassName) storedValueForKey("$attribute.name");
  }

  public void set${attribute.capitalizedName}($attribute.javaClassName value) {
    if (${entity.prefixClassNameWithoutPackage}.LOG.isDebugEnabled()) {
    	${entity.prefixClassNameWithoutPackage}.LOG.debug( "updating $attribute.name from " + ${attribute.name}() + " to " + value);
    }
    takeStoredValueForKey(value, "$attribute.name");
  }
#end

#end
#end
#foreach ($relationship in $entity.sortedToOneRelationships)
#if (!$relationship.inherited) 
  public $relationship.destination.classNameWithDefault ${relationship.name}() {
    return ($relationship.destination.classNameWithDefault)storedValueForKey("$relationship.name");
  }

  public void set${relationship.capitalizedName}Relationship($relationship.destination.classNameWithDefault value) {
    if (${entity.prefixClassNameWithoutPackage}.LOG.isDebugEnabled()) {
      ${entity.prefixClassNameWithoutPackage}.LOG.debug("updating $relationship.name from " + ${relationship.name}() + " to " + value);
    }
    if (value == null) {
    	$relationship.destination.classNameWithDefault oldValue = ${relationship.name}();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "$relationship.name");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "$relationship.name");
    }
  }
  
#end
#end
#foreach ($relationship in $entity.sortedToManyRelationships)
#if (!$relationship.inherited) 
  public NSArray<${relationship.destination.classNameWithDefault}> ${relationship.name}() {
    return (NSArray<${relationship.destination.classNameWithDefault}>)storedValueForKey("${relationship.name}");
  }

#if (!$relationship.inverseRelationship)
  public NSArray<${relationship.destination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier) {
    return ${relationship.name}(qualifier, null);
  }
#else
  public NSArray<${relationship.destination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier) {
    return ${relationship.name}(qualifier, null, false);
  }

  public NSArray<${relationship.destination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier, boolean fetch) {
    return ${relationship.name}(qualifier, null, fetch);
  }
#end

  public NSArray<${relationship.destination.classNameWithDefault}> ${relationship.name}(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings#if ($relationship.inverseRelationship), boolean fetch#end) {
    NSArray<${relationship.destination.classNameWithDefault}> results;
#if ($relationship.inverseRelationship && !$relationship.flattened)
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(${relationship.destination.classNameWithDefault}.${relationship.inverseRelationship.uppercaseUnderscoreName}_KEY, EOQualifier.QualifierOperatorEqual, this);
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }
      results = ${relationship.destination.classNameWithDefault}.fetch${relationship.destination.pluralName}(editingContext(), fullQualifier, sortOrderings);
    }
    else {
#end
      results = ${relationship.name}();
      if (qualifier != null) {
        results = (NSArray<${relationship.destination.classNameWithDefault}>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<${relationship.destination.classNameWithDefault}>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
#if ($relationship.inverseRelationship && !$relationship.flattened)
    }
#end
    return results;
  }
  
  public void addTo${relationship.capitalizedName}Relationship($relationship.destination.classNameWithDefault object) {
    if (${entity.prefixClassNameWithoutPackage}.LOG.isDebugEnabled()) {
      ${entity.prefixClassNameWithoutPackage}.LOG.debug("adding " + object + " to ${relationship.name} relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "${relationship.name}");
  }

  public void removeFrom${relationship.capitalizedName}Relationship($relationship.destination.classNameWithDefault object) {
    if (${entity.prefixClassNameWithoutPackage}.LOG.isDebugEnabled()) {
      ${entity.prefixClassNameWithoutPackage}.LOG.debug("removing " + object + " from ${relationship.name} relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "${relationship.name}");
  }

  public $relationship.destination.classNameWithDefault create${relationship.capitalizedName}Relationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("${relationship.destination.name}");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "${relationship.name}");
    return ($relationship.destination.classNameWithDefault) eo;
  }

  public void delete${relationship.capitalizedName}Relationship($relationship.destination.classNameWithDefault object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "${relationship.name}");
#if (!$relationship.ownsDestination)
    editingContext().deleteObject(object);
#end
  }

  public void deleteAll${relationship.capitalizedName}Relationships() {
    Enumeration objects = ${relationship.name}().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      delete${relationship.capitalizedName}Relationship(($relationship.destination.classNameWithDefault)objects.nextElement());
    }
  }

#end
#end
  public static ${entity.classNameWithoutPackage} create${entity.name}(EOEditingContext editingContext#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)
#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys)#if ($attribute.name == $qualifierKey)#set ($restrictingQualifierKey = 'true')#end#end
#if ($restrictingQualifierKey == 'false')
#if ($attribute.userInfo.ERXConstantClassName), ${attribute.userInfo.ERXConstantClassName}#else, ${attribute.javaClassName}#end ${attribute.name}
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey)), ${relationship.destination.classNameWithDefault} ${relationship.name}#end
#end
) {
    ${entity.classNameWithoutPackage} eo = (${entity.classNameWithoutPackage})EOUtilities.createAndInsertInstance(editingContext, ${entity.prefixClassNameWithoutPackage}.ENTITY_NAME);
#foreach ($attribute in $entity.sortedClassAttributes)
#if (!$attribute.allowsNull)
#set ($restrictingQualifierKey = 'false')
#foreach ($qualifierKey in $entity.restrictingQualifierKeys) 
#if ($attribute.name == $qualifierKey)
#set ($restrictingQualifierKey = 'true')
#end
#end
#if ($restrictingQualifierKey == 'false')
		eo.set${attribute.capitalizedName}(${attribute.name});
#end
#end
#end
#foreach ($relationship in $entity.sortedClassToOneRelationships)
#if ($relationship.mandatory && !($relationship.ownsDestination && $relationship.propagatesPrimaryKey))
    eo.set${relationship.capitalizedName}Relationship(${relationship.name});
#end
#end
    return eo;
  }

  public static NSArray<${entity.classNameWithoutPackage}> fetchAll${entity.pluralName}(EOEditingContext editingContext) {
    return ${entity.prefixClassNameWithoutPackage}.fetchAll${entity.pluralName}(editingContext, null);
  }

  public static NSArray<${entity.classNameWithoutPackage}> fetchAll${entity.pluralName}(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return ${entity.prefixClassNameWithoutPackage}.fetch${entity.pluralName}(editingContext, null, sortOrderings);
  }

  public static NSArray<${entity.classNameWithoutPackage}> fetch${entity.pluralName}(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(${entity.prefixClassNameWithoutPackage}.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<${entity.classNameWithoutPackage}> eoObjects = (NSArray<${entity.classNameWithoutPackage}>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static ${entity.classNameWithoutPackage} fetch${entity.name}(EOEditingContext editingContext, String keyName, Object value) {
    return ${entity.prefixClassNameWithoutPackage}.fetch${entity.name}(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ${entity.classNameWithoutPackage} fetch${entity.name}(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<${entity.classNameWithoutPackage}> eoObjects = ${entity.prefixClassNameWithoutPackage}.fetch${entity.pluralName}(editingContext, qualifier, null);
    ${entity.classNameWithoutPackage} eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (${entity.classNameWithoutPackage})eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one ${entity.name} that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ${entity.classNameWithoutPackage} fetchRequired${entity.name}(EOEditingContext editingContext, String keyName, Object value) {
    return ${entity.prefixClassNameWithoutPackage}.fetchRequired${entity.name}(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static ${entity.classNameWithoutPackage} fetchRequired${entity.classNameWithoutPackage}(EOEditingContext editingContext, EOQualifier qualifier) {
    ${entity.classNameWithoutPackage} eoObject = ${entity.prefixClassNameWithoutPackage}.fetch${entity.classNameWithoutPackage}(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no ${entity.name} that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static ${entity.classNameWithoutPackage} localInstanceOf${entity.classNameWithoutPackage}(EOEditingContext editingContext, ${entity.classNameWithoutPackage} eo) {
    ${entity.classNameWithoutPackage} localInstance = (eo == null) ? null : (${entity.classNameWithoutPackage})EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
