/*
 * Copyright (c) 2014, 2017 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.sfc.sbrest.provider.listener;

import static org.opendaylight.sfc.provider.SfcProviderDebug.printTraceStart;
import static org.opendaylight.sfc.provider.SfcProviderDebug.printTraceStop;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.sfc.provider.api.SfcInstanceIdentifiers;
import org.opendaylight.sfc.sbrest.provider.task.RestOperation;
import org.opendaylight.sfc.sbrest.provider.task.SbRestSfgTask;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfg.rev150214.service.function.groups.ServiceFunctionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbRestSfgEntryDataListener extends SbRestAbstractDataListener<ServiceFunctionGroup> {
    private static final Logger LOG = LoggerFactory.getLogger(SbRestSfgEntryDataListener.class);
    protected static ExecutorService executor = Executors.newFixedThreadPool(5);

    public SbRestSfgEntryDataListener() {
        setInstanceIdentifier(SfcInstanceIdentifiers.SFG_ENTRY_IID);
    }

    public void setDataProvider(DataBroker dataBroker) {
        setDataBroker(dataBroker);
        registerAsDataChangeListener();
    }

    @Override
    public void onDataTreeChanged(Collection<DataTreeModification<ServiceFunctionGroup>> changes) {
        printTraceStart(LOG);
        for (DataTreeModification<ServiceFunctionGroup> change: changes) {
            DataObjectModification<ServiceFunctionGroup> rootNode = change.getRootNode();
            switch (rootNode.getModificationType()) {
                case SUBTREE_MODIFIED:
                case WRITE:
                    ServiceFunctionGroup updatedServiceFunctionGroup = rootNode.getDataAfter();
                    LOG.debug("\nModified Service Function Name: {}", updatedServiceFunctionGroup.getName());

                    executor.submit(new SbRestSfgTask(RestOperation.PUT, updatedServiceFunctionGroup, executor));
                    break;
                case DELETE:
                    ServiceFunctionGroup originalServiceFunctionGroup = rootNode.getDataBefore();
                    LOG.debug("\nDeleted Service Function Name: {}", originalServiceFunctionGroup.getName());

                    executor.submit(new SbRestSfgTask(RestOperation.DELETE, originalServiceFunctionGroup, executor));
                    break;
                default:
                    break;
            }
        }

        printTraceStop(LOG);
    }
}
