package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Comparator;
import java.util.Set;

/**
 * Compares two entities based on entity dependencies.  So if entity 1 
 * references entity 2, then entity 1 is sorted after entity 2.
 * 
 * @author mschrag
 */
public class EOEntityComparator implements Comparator<EOEntity> {
	public int compare(EOEntity e1, EOEntity e2) {
		int comparison;
		if (e1 == e2) {
			comparison = 0;
		}
		else {
			Set<EOEntity> referencedEntities1 = e1.getReferencedEntities(true);
			Set<EOEntity> referencedEntities2 = e2.getReferencedEntities(true);
			if (referencedEntities1.contains(e2)) {
				if (referencedEntities2.contains(e1)) {
					// Circular reference
					comparison = 0;
				}
				else {
					comparison = 1;
				}
			}
			else if (referencedEntities2.contains(e1)) {
				comparison = -1;
			}
			else {
				comparison = 0;
			}
		}
		return comparison;
	}
}