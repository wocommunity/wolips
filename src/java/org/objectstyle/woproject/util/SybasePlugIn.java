package org.objectstyle.woproject.util;

/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
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

import java.sql.Connection;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOStoredProcedure;
import com.webobjects.foundation.*;
import com.webobjects.jdbcadaptor.*;

import org.apache.log4j.Logger;

/**
 * EOF Adaptor Plugin for Sybase. Fixes Sybase primary key generation procedure.
 * Note that "eo_pk_for_table" stored procedure must be included in the EOModel
 * that uses this plugin.
 * 
 * @author Antony Glover
 * @author Andrei Adamchik
 */
public class SybasePlugIn extends JDBCPlugIn {
	static Logger logger = Logger.getLogger(SybasePlugIn.class.getName());

	public static final String PK_TABLE_NAME = "eo_sequence_table";

	public static final String PK_SPROC_NAME = "EoPkForTable";

	public SybasePlugIn(JDBCAdaptor adaptor) {
		super(adaptor);
	}

	public String primaryKeyTableName() {
		// Note that the default value returned is "EO_PK_TABLE". This
		// is not what was created under 4.5.
		// The old table name is "eo_sequence_table".
		return PK_TABLE_NAME;
	}

	public NSArray newPrimaryKeys(int count, EOEntity entity, JDBCChannel channel) {

		// Retrieve the primary key attribute names of the entity and
		// verify that there is only one attribute. If not,
		// return null; otherwise, proceed

		NSArray pkAttributes = entity.primaryKeyAttributeNames();
		if (entity.primaryKeyAttributeNames().count() > 1) {
			return null;
		}

		//
		// At this point of execution, something has set the autoCommit
		// flag to false, i.e.chained transactions.This
		// will not allow us to execute the stored procedure for
		// generating primary keys;
		// therefore, set the flag to true.
		//

		// Retrieve the JDBC Connection

		JDBCContext myContext = (JDBCContext) channel.adaptorContext();
		Connection myConnection = myContext.connection();

		// Set the JDBC Connection's autoCommit to 'true', i.e.
		// unchained transactions.
		// QUESTION: Should the autoCommit flag be reset to its previous
		// value ?
		try {
			myConnection.setAutoCommit(true);
		} catch (java.sql.SQLException e) {
			logger.warn("Can't set AutoCommit to true.", e);
		}

		//
		// Now that autoCommit has been set to true, locate and execute
		// the stored procedure.
		//

		// Find the stored procedure used to generate primary keys
		EOStoredProcedure eoPkForTable = entity.model().storedProcedureNamed(PK_SPROC_NAME);

		// Set the parameters to the stored procedure
		NSDictionary spParameters = new NSDictionary(entity.externalName(), "tname");

		// Allocate an array for storing the primary keys
		NSMutableArray pkArray = new NSMutableArray();

		// Invoke the stored procedure for Count number of primary keys
		for (int i = 1; i <= count; i++) {

			// Execute the stored procedure
			channel.executeStoredProcedure(eoPkForTable, spParameters);

			// Fetch the rows returned from the call
			NSDictionary row = channel.fetchRow();

			// Re-package the output into a primary key dictionary
			NSMutableDictionary pkDictionary = new NSMutableDictionary();
			pkDictionary.takeValueForKey(row.valueForKey("COUNTER"), (String) (pkAttributes.objectAtIndex(0)));

			// Add the primary key dictionary to the array
			pkArray.addObject(pkDictionary);

			// Only one row of data would have been returned; however,
			// it is VERY important to perform an additional fetch.
			// When fetchRow returns a null dictionary, this indicates
			// to the supporting classes that the operation has terminated.
			// If this line is removed, then saves to the editing
			// contexts will result in a message about no open channels
			// available.
			// Do not remove the following line!
			row = channel.fetchRow();

		}

		return pkArray;

	}
}