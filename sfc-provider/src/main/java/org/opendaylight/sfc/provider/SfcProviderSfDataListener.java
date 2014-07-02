/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.opendaylight.sfc.provider;


import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140629.ServiceFunctions;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140629.service.functions.*;


import org.opendaylight.controller.md.sal.common.api.data.DataChangeEvent;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.data.DataChangeListener;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class gets called whenever there is a change to
 * the Service Functions data store.
 *
 * <p>
 * @author Reinaldo Penno (rapenno@gmail.com)
 * @version 0.1
 * @since       2014-06-30
 */

public class SfcProviderSfDataListener implements DataChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(SfcProviderSfDataListener.class);

    @Override
    public void onDataChanged(
            DataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        LOG.info("\n########## Start: {}", Thread.currentThread().getStackTrace()[1]);

        boolean b = change.getUpdatedConfigurationData().containsKey(OpendaylightSfc.sfIID);
        DataObject dataObject = change.getUpdatedConfigurationData().get( OpendaylightSfc.sfIID );
        if( dataObject instanceof  ServiceFunctions)
        {
            ServiceFunctions servicefunctions = (ServiceFunctions)dataObject;
            for (ServiceFunction servicefunction : servicefunctions.getServiceFunction()) {
                LOG.info("\n Service Function name: {}", servicefunction.getName());
            }
            LOG.info("\n########## Caught ServiceFunctions: {}", Thread.currentThread().getStackTrace()[1]);
        }
        // Debug and Unit Test. We trigger the unit test code by adding a service function to the datastore.
        if (SfcProviderDebug.ON) {
            SfcProviderDebug.ON = false;
            SfcProviderUnitTest.sfcProviderUnitTest(SfcProviderRpc.getSfcProviderRpc());
        }

        LOG.info("\n########## Stop: {}", Thread.currentThread().getStackTrace()[1]);
    }
}
