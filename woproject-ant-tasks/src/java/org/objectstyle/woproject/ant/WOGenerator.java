/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 - 2006 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.woproject.ant;

import java.io.File;

import org.objectstyle.cayenne.gen.AntClassGenerator;
import org.objectstyle.cayenne.gen.ClassGenerationInfo;
import org.objectstyle.cayenne.gen.ClassGenerator;
import org.objectstyle.cayenne.gen.DefaultClassGenerator;
import org.objectstyle.cayenne.gen.MapClassGenerator;
import org.objectstyle.cayenne.gen.WOClassGenerationInfoDelegate;
import org.objectstyle.cayenne.map.DataMap;
import org.objectstyle.cayenne.map.ObjEntity;
import org.objectstyle.cayenne.tools.CayenneGenerator;

/**
 * Ant task to generate EOEnterpriseObjects from EOModel. For detailed
 * instructions go to the <a href="../../../../../ant/wogen.html">manual page</a>.
 * 
 * @ant.task category="packaging"
 * @author Andrei Adamchik
 */
public class WOGenerator extends CayenneGenerator {
	protected boolean useValueType;

	/**
	 * Wrapper of the superclass <code>setMap</code> method to provide
	 * WebObjects-friendly name.
	 */
	public void setModel(File model) {
		super.setMap(model);
	}

	/**
	 * Ant task attribute defining whether generated class attributes should use
	 * the lowest possible subclass determined from the "valueType" parameter in
	 * EOModel. If false (default) a common superclass will be used. E.g. for
	 * "NSNumber", java.lang.Number attributes will be genertaed.
	 * 
	 * <p>
	 * Note that this is only implemented for numeric value types.
	 * </p>
	 */
	public void setUseValueType(boolean flag) {
		this.useValueType = flag;
	}

	/**
	 * Overrides superclass implementation to create DataMap from EOModel
	 * instead of Cayenne DataMap XML file.
	 */
	protected DataMap loadDataMap() throws Exception {
		return new EOModelReader(useValueType).loadEOModel(map.getCanonicalPath());
	}

	protected DefaultClassGenerator createGenerator() {
		WOAntClassGenerator gen = new WOAntClassGenerator();
		gen.setParentTask(this);
		gen.setVersionString(ClassGenerator.VERSION_1_1);
		return gen;
	}

	final class WOAntClassGenerator extends AntClassGenerator {
		private final String WOGEN_SUPERCLASS_TEMPLATE = "wogen/superclass.vm";

		private final String WOGEN_SUBCLASS_TEMPLATE = "wogen/subclass.vm";

		private final String WOGEN_SINGLE_CLASS_TEMPLATE = "wogen/singleclass.vm";

		private final String[] RESERVED_CLASS_NAMES = new String[] { "EOGenericRecord", "EOCustomObject" };

		protected String defaultSingleClassTemplate() {
			return WOGEN_SINGLE_CLASS_TEMPLATE;
		}

		protected String defaultSubclassTemplate() {
			return WOGEN_SUBCLASS_TEMPLATE;
		}

		protected String defaultSuperclassTemplate() {
			return WOGEN_SUPERCLASS_TEMPLATE;
		}

		protected File fileForClass(String packageName, String className) throws Exception {

			if (isReservedName(className)) {
				return null;
			}

			return super.fileForClass(packageName, className);
		}

		protected File fileForSuperclass(String packageName, String className) throws Exception {

			if (isReservedName(className)) {
				return null;
			}
			return super.fileForSuperclass(packageName, className);
		}

		protected boolean isReservedName(String className) {
			String classNameWithoutSuperClassPrefix = className;
			if (className.startsWith(MapClassGenerator.SUPERCLASS_PREFIX)) {
				classNameWithoutSuperClassPrefix = className.substring(MapClassGenerator.SUPERCLASS_PREFIX.length());
			}

			for (int i = 0; i < RESERVED_CLASS_NAMES.length; i++) {
				if (RESERVED_CLASS_NAMES[i].equals(classNameWithoutSuperClassPrefix)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Fixes some Classgenerator defaults assumed by Cayenne.
		 */
		protected void initClassGenerator_1_1(ClassGenerationInfo gen, ObjEntity entity, boolean superclass) {
			super.initClassGenerator_1_1(gen, entity, superclass);

			// fix default superclass
			if (gen.getSuperClassName() == null || gen.getSuperClassName().indexOf("org.objectstyle.cayenne") >= 0) {
				//XXX ClassGenerationInfo.setSuperClassName() isn't visible
				WOClassGenerationInfoDelegate.setSuperClassName(gen, "EOGenericRecord");
			}
		}

	}
}
