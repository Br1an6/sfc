/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.sfc.sbrest.provider.task;

import java.util.concurrent.ExecutorService;
import org.opendaylight.sfc.sbrest.json.SfExporterFactory;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SfName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sf.rev140701.service.functions.ServiceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbRestSfTask extends SbRestAbstractTask {

    private static final String SF_REST_URI = "/config/service-function:service-functions/service-function/";
    private static final Logger LOG = LoggerFactory.getLogger(SbRestSfTask.class);

    public SbRestSfTask(RestOperation restOperation, ServiceFunction dataObject, ExecutorService odlExecutor) {
        super(restOperation, new SfExporterFactory(), dataObject, odlExecutor);
        setRestUriList(dataObject);
    }

    private void setRestUriList(ServiceFunction obj) {
        if (obj.getRestUri() != null) {
            SfName sfName = obj.getName();
            if (sfName != null) {
                String restUri = obj.getRestUri().getValue() + SF_REST_URI + sfName.getValue();
                addRestUri(restUri);
                LOG.info("SF will be send to REST URI {}", restUri);
            }
        }
    }
}
