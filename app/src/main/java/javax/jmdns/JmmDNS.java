/**
 *
 */
package javax.jmdns;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.jmdns.impl.JmmDNSImpl;

/**
 * <p>
 * Java Multihomed Multicast DNS
 * </p>
 * Uses an underlying {@link javax.jmdns.JmDNS} instance for each {@link java.net.InetAddress} found on this computer.<br/>
 * This class will monitor network topology changes, and will create or destroy JmDNS instances as required. It is your responsibility to maintain services registration (hint: use a {@link NetworkTopologyListener}).<br/>
 * Most of this class methods have no notion of transaction: if an Exception is raised in the middle of execution, you may be in an incoherent state.
 * <p>
 * <b>Note:</b> This API is experimental and may change in the future please let us know what work and what does not work in you application.
 * </p>
 *
 * @author C&eacute;drik Lime, Pierre Frisch
 */
public interface JmmDNS extends Closeable {

    /**
     * JmmDNS.Factory enable the creation of new instance of JmmDNS.
     */
    public static final class Factory {
        private static volatile JmmDNS _instance;

        /**
         * This interface defines a delegate to the EOClassDescriptionRegister class to enable subclassing.
         */
        public static interface ClassDelegate {

            /**
             * Allows the delegate the opportunity to construct and return a different JmmDNS.
             *
             * @return Should return a new JmmDNS.
             * @see #classDelegate()
             * @see #setClassDelegate(javax.jmdns.JmmDNS.Factory.ClassDelegate anObject)
             */
            public JmmDNS newJmmDNS();

        }

        private static final AtomicReference<ClassDelegate> _databaseClassDelegate = new AtomicReference<ClassDelegate>();

        private Factory() {
            super();
        }

        /**
         * Assigns <code>delegate</code> as JmmDNS's class delegate. The class delegate is optional.
         *
         * @param delegate
         *            The object to set as JmmDNS's class delegate.
         * @see #classDelegate()
         * @see javax.jmdns.JmmDNS.Factory.ClassDelegate
         */
        public static void setClassDelegate(ClassDelegate delegate) {
            _databaseClassDelegate.set(delegate);
        }

        /**
         * Returns JmmDNS's class delegate.
         *
         * @return JmmDNS's class delegate.
         * @see #setClassDelegate(javax.jmdns.JmmDNS.Factory.ClassDelegate anObject)
         * @see javax.jmdns.JmmDNS.Factory.ClassDelegate
         */
        public static ClassDelegate classDelegate() {
            return _databaseClassDelegate.get();
        }

        /**
         * Returns a new instance of JmmDNS using the class delegate if it exists.
         *
         * @return new instance of JmmDNS
         */
        protected static JmmDNS newJmmDNS() {
            JmmDNS dns = null;
            ClassDelegate delegate = _databaseClassDelegate.get();
            if (delegate != null) {
                dns = delegate.newJmmDNS();
            }
            return (dns != null ? dns : new JmmDNSImpl());
        }

        /**
         * Return the instance of the Multihommed Multicast DNS.
         *
         * @return the JmmDNS
         */
        public static JmmDNS getInstance() {
            if (_instance == null) {
                synchronized (Factory.class) {
                    if (_instance == null) {
                        _instance = Factory.newJmmDNS();
                    }
                }
            }
            return _instance;
        }
    }

    /**
     * Return the names of the JmDNS instances.
     *
     * @return list of name of the JmDNS
     * @see JmDNS#getName()
     */
    public abstract String[] getNames();

    /**
     * Return the list HostName associated with this JmmDNS instance.
     *
     * @return list of host names
     * @see JmDNS#getHostName()
     */
    public abstract String[] getHostNames();

    /**
     * Return the list of addresses of the interface to which this instance of JmmDNS is bound.
     *
     * @return list of Internet Address
     * @exception java.io.IOException
     * @see JmDNS#getInterface()
     */
    public abstract InetAddress[] getInterfaces() throws IOException;

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: Do not call this method from the AWT event dispatcher thread. You will make the user interface unresponsive.
     *
     * @param type
     *            fully qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @return list of service info. If no service info is found the list is empty.
     * @see JmDNS#getServiceInfo(String, String)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name);

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: If you call this method from the AWT event dispatcher thread, use a small timeout, or you will make the user interface unresponsive.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 5s.
     * @return list of service info. If no service info is found the list is empty.
     * @see JmDNS#getServiceInfo(String, String, long)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name, long timeout);

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: If you call this method from the AWT event dispatcher thread, use a small timeout, or you will make the user interface unresponsive.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @return list of service info. If no service info is found the list is empty.
     * @see JmDNS#getServiceInfo(String, String, boolean)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name, boolean persistent);

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: If you call this method from the AWT event dispatcher thread, use a small timeout, or you will make the user interface unresponsive.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 5s.
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @return list of service info. If no service info is found the list is empty.
     * @see JmDNS#getServiceInfo(String, String, boolean, long)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name, boolean persistent, long timeout);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @see JmDNS#requestServiceInfo(String, String)
     */
    public abstract void requestServiceInfo(String type, String name);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @see JmDNS#requestServiceInfo(String, String, boolean)
     */
    public abstract void requestServiceInfo(String type, String name, boolean persistent);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param timeout
     *            timeout in milliseconds
     * @see JmDNS#requestServiceInfo(String, String, long)
     */
    public abstract void requestServiceInfo(String type, String name, long timeout);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @param timeout
     *            timeout in milliseconds
     * @see JmDNS#requestServiceInfo(String, String, boolean, long)
     */
    public abstract void requestServiceInfo(String type, String name, boolean persistent, long timeout);

    /**
     * Listen for service types.
     *
     * @param listener
     *            listener for service types
     * @exception java.io.IOException
     * @see JmDNS#addServiceTypeListener(javax.jmdns.ServiceTypeListener)
     */
    public abstract void addServiceTypeListener(ServiceTypeListener listener) throws IOException;

    /**
     * Remove listener for service types.
     *
     * @param listener
     *            listener for service types
     * @see JmDNS#removeServiceTypeListener(javax.jmdns.ServiceTypeListener)
     */
    public abstract void removeServiceTypeListener(ServiceTypeListener listener);

    /**
     * Listen for services of a given type. The type has to be a fully qualified type name such as <code>_http._tcp.local.</code>.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code>.
     * @param listener
     *            listener for service updates
     * @see JmDNS#addServiceListener(String, javax.jmdns.ServiceListener)
     */
    public abstract void addServiceListener(String type, ServiceListener listener);

    /**
     * Remove listener for services of a given type.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code>.
     * @param listener
     *            listener for service updates
     * @see JmDNS#removeServiceListener(String, javax.jmdns.ServiceListener)
     */
    public abstract void removeServiceListener(String type, ServiceListener listener);

    /**
     * Register a service. The service is registered for access by other jmdns clients. The name of the service may be changed to make it unique.<br>
     * <b>Note</b> the Service info is cloned for each network interface.
     *
     * @param info
     *            service info to register
     * @exception java.io.IOException
     * @see JmDNS#registerService(javax.jmdns.ServiceInfo)
     */
    public abstract void registerService(ServiceInfo info) throws IOException;

    /**
     * Unregister a service. The service should have been registered.
     *
     * @param info
     *            service info to remove
     * @see JmDNS#unregisterService(javax.jmdns.ServiceInfo)
     */
    public abstract void unregisterService(ServiceInfo info);

    /**
     * Unregister all services.
     *
     * @see JmDNS#unregisterAllServices()
     */
    public abstract void unregisterAllServices();

    /**
     * Register a service type. If this service type was not already known, all service listeners will be notified of the new service type. Service types are automatically registered as they are discovered.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code>.
     * @see JmDNS#registerServiceType(String)
     */
    public abstract void registerServiceType(String type);

    /**
     * Returns a list of service infos of the specified type.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @return An array of service instance.
     * @see JmDNS#list(String)
     */
    public abstract ServiceInfo[] list(String type);

    /**
     * Returns a list of service infos of the specified type.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 6s.
     * @return An array of service instance.
     * @see JmDNS#list(String, long)
     */
    public abstract ServiceInfo[] list(String type, long timeout);

    /**
     * Returns a list of service infos of the specified type sorted by subtype. Any service that do not register a subtype is listed in the empty subtype section.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @return A dictionary of service info by subtypes.
     * @see JmDNS#listBySubtype(String)
     */
    public abstract Map<String, ServiceInfo[]> listBySubtype(String type);

    /**
     * Returns a list of service infos of the specified type sorted by subtype. Any service that do not register a subtype is listed in the empty subtype section.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 6s.
     * @return A dictionary of service info by subtypes.
     * @see JmDNS#listBySubtype(String, long)
     */
    public abstract Map<String, ServiceInfo[]> listBySubtype(String type, long timeout);

    /**
     * Listen to network changes.
     *
     * @param listener
     *            listener for network changes
     */
    public abstract void addNetworkTopologyListener(NetworkTopologyListener listener);

    /**
     * Remove listener for network changes.
     *
     * @param listener
     *            listener for network changes
     */
    public abstract void removeNetworkTopologyListener(NetworkTopologyListener listener);

    /**
     * Returns list of network change listeners
     *
     * @return list of network change listeners
     */
    public abstract NetworkTopologyListener[] networkListeners();

}
