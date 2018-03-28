/**
 * (C) Copyright IBM Corp. 2018,2019
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.watsonhealth.fhir.persistence.jdbc.test;

import java.util.Properties;

import com.ibm.watsonhealth.fhir.persistence.FHIRPersistence;
import com.ibm.watsonhealth.fhir.persistence.jdbc.impl.FHIRPersistenceJDBCNormalizedImpl;
import com.ibm.watsonhealth.fhir.persistence.jdbc.test.util.DerbyInitializer;
import com.ibm.watsonhealth.fhir.persistence.test.common.AbstractQueryMultiResourceTest;


public class JDBCNormQueryMultiResourceTest extends AbstractQueryMultiResourceTest {
	
	private Properties testProps;
	
	public JDBCNormQueryMultiResourceTest() throws Exception {
		this.testProps = readTestProperties("test.normalized.properties");
	}

	@Override
	public void bootstrapDatabase() throws Exception {
		DerbyInitializer derbyInit;
		String dbDriverName = this.testProps.getProperty("dbDriverName");
		if (dbDriverName != null && dbDriverName.contains("derby")) {
			derbyInit = new DerbyInitializer(this.testProps);
			derbyInit.bootstrapDb(false);
		}
	}
	
    @Override
    public FHIRPersistence getPersistenceImpl() throws Exception {
    	return new FHIRPersistenceJDBCNormalizedImpl(this.testProps);
    }
}
