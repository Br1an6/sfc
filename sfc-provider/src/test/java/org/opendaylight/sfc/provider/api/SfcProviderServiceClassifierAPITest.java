/*
 * Copyright (c) 2015 Pantheon Technologies s.r.o. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.sfc.provider.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.sfc.provider.AbstractDataStoreManager;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.ServiceFunctionClassifiers;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.ServiceFunctionClassifiersState;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.ServiceFunctionClassifier;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.ServiceFunctionClassifierBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.ServiceFunctionClassifierKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.service.function.classifier.SclServiceFunctionForwarder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.service.function.classifier.SclServiceFunctionForwarderBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.service.function.classifier.SclServiceFunctionForwarderKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.state.ServiceFunctionClassifierState;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.state.ServiceFunctionClassifierStateKey;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.state.service.function.classifier.state.SclRenderedServicePath;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.scf.rev140701.service.function.classifiers.state.service.function.classifier.state.SclRenderedServicePathKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class SfcProviderServiceClassifierAPITest extends AbstractDataStoreManager {

    @Before
    public void setUp() throws Exception {
        setOdlSfc();
    }

    @Test
    public void addRenderedPathToServiceClassifierStateTest() throws Exception {
        final String clsfName = "clsfName";
        final String rspName1 = "rspName1";
        final String rspName2 = "rspName2";

        assertClsfStateDoesNotExist(clsfName);
        assertTrue("Failed to update clsf state.",
                SfcProviderServiceClassifierAPI.addRenderedPathToServiceClassifierState(clsfName, rspName1));
        readAndAssertClsfState(clsfName, rspName1);
        // update classifier state with another rendered service path
        assertTrue("Failed to update clsf state.",
                SfcProviderServiceClassifierAPI.addRenderedPathToServiceClassifierState(clsfName, rspName2));
        readAndAssertClsfState(clsfName, rspName2);
    }

    private static void assertClsfStateDoesNotExist(String clsfName) {
        ServiceFunctionClassifierStateKey clsfStateKey = new ServiceFunctionClassifierStateKey(clsfName);
        InstanceIdentifier<ServiceFunctionClassifierState> rspId = InstanceIdentifier.builder(ServiceFunctionClassifiersState.class)
                .child(ServiceFunctionClassifierState.class, clsfStateKey).build();
        ServiceFunctionClassifierState clsfState = SfcDataStoreAPI.readTransactionAPI(rspId, LogicalDatastoreType.OPERATIONAL);
        assertNull("Unexpected clsf state found.", clsfState);
    }

    private static void readAndAssertClsfState(String clsfName, String rspName) {
        ServiceFunctionClassifierStateKey clsfStateKey = new ServiceFunctionClassifierStateKey(clsfName);
        SclRenderedServicePathKey rspKey = new SclRenderedServicePathKey(rspName);
        InstanceIdentifier<SclRenderedServicePath> rspId = InstanceIdentifier.builder(ServiceFunctionClassifiersState.class)
                .child(ServiceFunctionClassifierState.class, clsfStateKey)
                .child(SclRenderedServicePath.class, rspKey).build();
        SclRenderedServicePath rsp = SfcDataStoreAPI.readTransactionAPI(rspId, LogicalDatastoreType.OPERATIONAL);
        assertNotNull("Scl rendered service path not found.", rsp);
        assertEquals("Unexpected scl rendered service path name.", rspName, rsp.getName());
    }

    @Test
    public void addRenderedPathToServiceClassifierStateExecutorTest() throws Exception {
        final String clsfName = "clsfName";
        final String rspName1 = "rspName1";
        final String rspName2 = "rspName2";

        assertClsfStateDoesNotExist(clsfName);
        assertTrue("Failed to update clsf state.",
                SfcProviderServiceClassifierAPI.addRenderedPathToServiceClassifierStateExecutor(clsfName, rspName1));
        readAndAssertClsfState(clsfName, rspName1);
        // update classifier state with another rendered service path
        assertTrue("Failed to update clsf state.",
                SfcProviderServiceClassifierAPI.addRenderedPathToServiceClassifierStateExecutor(clsfName, rspName2));
        readAndAssertClsfState(clsfName, rspName2);
    }

    @Test
    public void readServiceClassifierExecutorTest() throws Exception {
        ServiceFunctionClassifier clsf1 = createClassifier("clsfName1", "accessList1", "sffName1");
        ServiceFunctionClassifier clsf2 = createClassifier("clsfName2", "accessList2", "sffName2");

        assertClassifierDoesNotExists(clsf1.getName());
        writeClassifierToStore(clsf1);
        readAndAssertClassifier(clsf1);

        assertClassifierDoesNotExists(clsf2.getName());
        writeClassifierToStore(clsf2);
        readAndAssertClassifier(clsf1);
        readAndAssertClassifier(clsf2);
    }

    private void writeClassifierToStore(ServiceFunctionClassifier clsf) {
        InstanceIdentifier<ServiceFunctionClassifier> clsfId = InstanceIdentifier.builder(ServiceFunctionClassifiers.class)
                .child(ServiceFunctionClassifier.class, new ServiceFunctionClassifierKey(clsf.getName())).build();
        boolean result = SfcDataStoreAPI.writePutTransactionAPI(clsfId, clsf, LogicalDatastoreType.CONFIGURATION);
        assertTrue("Failed to write classifier to data store.", result);
    }

    private static ServiceFunctionClassifier createClassifier(String clsfName, String accessList, String sffName) {
        SclServiceFunctionForwarderBuilder sffBuilder = new SclServiceFunctionForwarderBuilder();
        sffBuilder.setName(sffName);
        sffBuilder.setKey(new SclServiceFunctionForwarderKey(sffName));
        List<SclServiceFunctionForwarder> sfForwarders = new ArrayList<SclServiceFunctionForwarder>();
        sfForwarders.add(sffBuilder.build());

        ServiceFunctionClassifierBuilder clsfBuilder = new ServiceFunctionClassifierBuilder();
        clsfBuilder.setName(clsfName);
        clsfBuilder.setKey(new ServiceFunctionClassifierKey(clsfName));
        clsfBuilder.setAccessList(accessList);
        clsfBuilder.setSclServiceFunctionForwarder(sfForwarders);
        return clsfBuilder.build();
    }

    private static void assertClassifierDoesNotExists(String clsfName) {
        ServiceFunctionClassifier clsf = SfcProviderServiceClassifierAPI.readServiceClassifierExecutor(clsfName);
        assertNull("Unexpected classifier found.", clsf);
    }

    private static void readAndAssertClassifier(ServiceFunctionClassifier expectedClsf) {
        ServiceFunctionClassifier clsf = SfcProviderServiceClassifierAPI.readServiceClassifierExecutor(expectedClsf.getName());
        assertNotNull("Classifier not found.", clsf);
        assertEquals(expectedClsf.getName(), clsf.getName());
        assertEquals(expectedClsf.getKey().getName(), clsf.getKey().getName());
        assertEquals(expectedClsf.getAccessList(), clsf.getAccessList());
        assertEquals(expectedClsf.getSclServiceFunctionForwarder(), clsf.getSclServiceFunctionForwarder());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getReadTest() throws Exception {
        Object[] params = {"funcParam0", "funcParam1"};
        Class[] paramTypes = {String.class, String.class};
        SfcProviderServiceClassifierAPI api = SfcProviderServiceClassifierAPI.getRead(params, paramTypes);
        assertApiInitialization(api, params, paramTypes);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getAddRenderedPathToServiceClassifierStateExecutorTest() throws Exception {
        Object[] params = {"funcParam0", "funcParam1"};
        Class[] paramTypes = {String.class, String.class};
        SfcProviderServiceClassifierAPI api = SfcProviderServiceClassifierAPI.getAddRenderedPathToServiceClassifierStateExecutor(params, paramTypes);
        assertApiInitialization(api, params, paramTypes);
    }

    @SuppressWarnings("rawtypes")
    private void assertApiInitialization(SfcProviderServiceClassifierAPI api,
                                         Object[] expectedParams, Class[] expectedParamTypes) {
        assertArrayEquals(expectedParams, api.getParameters());
        assertArrayEquals(expectedParamTypes, api.getParameterTypes());
        assertFalse(api.getMethodName().isEmpty());
    }
}