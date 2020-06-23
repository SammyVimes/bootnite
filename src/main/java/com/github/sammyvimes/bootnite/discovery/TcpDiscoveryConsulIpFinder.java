package com.github.sammyvimes.bootnite.discovery;

import com.ecwid.consul.v1.agent.model.NewService;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinderAdapter;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpDiscoveryConsulIpFinder extends TcpDiscoveryIpFinderAdapter {

    /**
     * Default service name for service registrations.
     */
    public static final String SERVICE_NAME = "ignite";

    /**
     * Key for retrieving Consul address from system properties
     */
    public static final String PROP_CONSUL_ADDRESS_KEY = "IGNITE_CONSUL_ADDRESS";

    /**
     * Key for retrieving Consul port from system properties
     */
    public static final String PROP_CONSUL_PORT_KEY = "IGNITE_CONSUL_PORT";

    public static final int DEFAULT_CONSUL_PORT = 8500;

    /**
     * Init guard.
     */
    @GridToStringExclude
    private final AtomicBoolean initGuard = new AtomicBoolean();

    /**
     * Close guard.
     */
    @GridToStringExclude
    private final AtomicBoolean closeGuard = new AtomicBoolean();

    /**
     * Logger.
     */
    @LoggerResource
    private IgniteLogger log;

    private String serviceName = SERVICE_NAME;

    /**
     * Consul client
     */
    private final ConsulDiscoveryClient consul;

    private final ConsulServiceRegistry registry;

    private final ConsulDiscoveryProperties consulDiscoveryProperties;
    private final String host;
    private final int port;

    private ConsulRegistration registration;

    /**
     * All addresses registered by this node
     */
    private HashSet<InetSocketAddress> ourInstances = new HashSet<>();

    /**
     * Registered addresses for this node
     */
    private HashSet<InetSocketAddress> myAddresses = new HashSet<>();

    /**
     * Constructor.
     * @param consul
     * @param registry
     * @param host
     * @param port
     */
    public TcpDiscoveryConsulIpFinder(final ConsulDiscoveryClient consul,
                                      final ConsulServiceRegistry registry,
                                      final ConsulDiscoveryProperties consulDiscoveryProperties, final String host, final int port) {
        this.consul = consul;
        this.registry = registry;
        this.consulDiscoveryProperties = consulDiscoveryProperties;
        this.host = host;
        this.port = port;
        setShared(true);
    }

    /**
     * Initializes this IP Finder by creating the appropriate Curator objects.
     */
    private void init() {
        if (!initGuard.compareAndSet(false, true)) {
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("Initializing Consul IP Finder.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSpiContextDestroyed() {
        if (!closeGuard.compareAndSet(false, true)) {
            U.warn(log, "Consul IP Finder can't be closed more than once.");

            return;
        }

        if (log.isInfoEnabled()) {
            log.info("Destroying Consul IP Finder.");
        }

        super.onSpiContextDestroyed();
        unregisterSelf();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InetSocketAddress> getRegisteredAddresses() throws IgniteSpiException {
        init();

        if (log.isDebugEnabled()) {
            log.debug("Getting registered addresses from Consul IP Finder.");
        }

        final List<ServiceInstance> instances = consul.getInstances(serviceName);

        Collection<InetSocketAddress> registeredAddresses = new HashSet<>();

        for (ServiceInstance node : instances) {
            registeredAddresses.add(new InetSocketAddress(node.getHost(), node.getPort()));
        }

        if (log.isInfoEnabled()) {
            log.info("Cosnul IP Finder resolved addresses: " + registeredAddresses);
        }

        return registeredAddresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAddresses(Collection<InetSocketAddress> addrs) throws IgniteSpiException {
        init();

        if (log.isInfoEnabled()) {
            log.info("Registering addresses with Cosnul IP Finder: " + addrs + " Addresses that are already registered will be ignored");
        }

        Collection<InetSocketAddress> alreadyRegistered = getRegisteredAddresses();

        addrs.removeAll(alreadyRegistered);

        for (InetSocketAddress addr : addrs) {
            String serviceUid = inetAddrToUid(addr);

            final NewService newService = new NewService();
            newService.setName(serviceName);
            newService.setAddress(host);
            newService.setPort(port);
            newService.setId(serviceUid);

            registry.register(new ConsulRegistration(newService, consulDiscoveryProperties));

            ourInstances.add(addr);

            log.info("registered service " + serviceUid);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterAddresses(Collection<InetSocketAddress> addrs) throws IgniteSpiException {
        init();

        if (log.isInfoEnabled()) {
            log.info("Unregistering addresses with Consul IP Finder: " + addrs);
        }

        for (InetSocketAddress addr : addrs) {
            String serviceUid = inetAddrToUid(addr);
            final NewService service = new NewService();
            service.setId(serviceUid);

            registry.deregister(new ConsulRegistration(service, consulDiscoveryProperties));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeLocalAddresses(Collection<InetSocketAddress> addrs) throws IgniteSpiException {
        registerSelf(addrs);
    }

    private String inetAddrToUid(InetSocketAddress addr) {
        final InetAddress address = addr.getAddress();
        String hostAddress = "";
        if (address != null) {
            hostAddress = address.getHostAddress();
        }
        return String.format("%s:%s:%d", addr.getHostName(), hostAddress, addr.getPort());
    }

    private void registerSelf(Collection<InetSocketAddress> addrs) {
        registerAddresses(addrs);
        myAddresses.addAll(addrs);
    }

    private void unregisterSelf() {
        unregisterAddresses(myAddresses);
    }

}
