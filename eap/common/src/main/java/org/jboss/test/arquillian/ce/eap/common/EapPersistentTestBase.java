package org.jboss.test.arquillian.ce.eap.common;

import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

import org.jboss.arquillian.ce.api.OpenShiftHandle;
import org.arquillian.cube.openshift.impl.enricher.RouteURL;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;

/**
 * @author Jonh Wendell
 * @author Marko Luksa
 */
public abstract class EapPersistentTestBase {

    private final static Logger log = Logger.getLogger(org.jboss.test.arquillian.ce.eap.common.EapPersistentTestBase.class.getName());

    protected abstract String[] getRCNames();

    @RouteURL("eap-app")
    private URL url;

    @RouteURL("secure-eap-app")
    private URL secureUrl;

    @ArquillianResource
    OpenShiftHandle adapter;

    private final static String summary1 = UUID.randomUUID().toString();
    private final static String summary2 = UUID.randomUUID().toString();
    private final static String description1 = UUID.randomUUID().toString();
    private final static String description2 = UUID.randomUUID().toString();

    @Test
    @RunAsClient
    public void insertRestartAndVerify() throws Exception {
        insertItems();
        restartPods();
        verifyItems();
    }

    private void insertItems() throws Exception {
        TodoList.insertItem(url.toString(), summary1, description1);
        TodoList.insertItem(secureUrl.toString(), summary2, description2);
    }

    private void restartPod(String name) throws Exception {
        log.info("Scaling down " + name);
        adapter.scaleDeployment(name, 0);
        log.info("Scaling up " + name);
        adapter.scaleDeployment(name, 1);
    }

    private void restartPods() throws Exception {
        for (String rc : getRCNames()) {
            restartPod(rc);
        }
    }

    private void verifyItems() throws Exception {
        TodoList.checkItem(url.toString(), summary1, description1);
        TodoList.checkItem(secureUrl.toString(), summary2, description2);
    }
}
