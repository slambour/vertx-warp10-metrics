//
//   Copyright 2016  Cityzen Data
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//
package io.vertx.ext.warp10.impl;

import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.metrics.impl.DummyVertxMetrics;
import io.vertx.core.net.*;
import io.vertx.core.spi.metrics.*;
import io.vertx.ext.warp10.MetricsType;
import io.vertx.ext.warp10.VertxWarp10Options;
import io.vertx.ext.warp10.metrics.*;
import io.warp10.sensision.Sensision;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sébastien lambour
 */
public class VertxMetricsImpl extends DummyVertxMetrics {

  private final Vertx vertx;
  private final VertxWarp10Options options;
  private final EventBusMetricsImpl eventBusMetrics;

  public static final String SENSISION_CLASS_PREFIX = "io.vertx.metrics.warp10.";

  public static final String SENSISION_CLASS_VERTICLE_DEPLOYED = SENSISION_CLASS_PREFIX + "verticle.deployed";
  public static final String SENSISION_CLASS_VERTICLE_UNDEPLOYED = SENSISION_CLASS_PREFIX + "verticle.undeployed";

  public static final String SENSISION_LABEL_CLUSTER_NAME = "cluster";


  public final Map<String, String> defaultLabels = new HashMap<>();
  /**
   * @param vertx   the {@link Vertx} managed instance
   * @param options Vertx Warp10 options
   */
  public VertxMetricsImpl(Vertx vertx, VertxWarp10Options options) {
    this.vertx = vertx;
    this.options = options;

    String clusterName = options.getClusterName();
    if (clusterName!=null && !clusterName.isEmpty()) {
      this.defaultLabels.put(SENSISION_LABEL_CLUSTER_NAME, clusterName);
    }

    eventBusMetrics = new EventBusMetricsImpl(defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.HTTP_SERVER));
  }

  @Override
  public void eventBusInitialized(EventBus bus) {
    // Finish setup
  }

  @Override
  public void close() {
  }

  @Override
  public void verticleDeployed(Verticle verticle) {
    Sensision.event(SENSISION_CLASS_VERTICLE_DEPLOYED, defaultLabels, verticle.getClass().getSimpleName());
  }

  @Override
  public void verticleUndeployed(Verticle verticle) {
    Sensision.event(SENSISION_CLASS_VERTICLE_UNDEPLOYED, defaultLabels, verticle.getClass().getSimpleName());
  }

  @Override
  public HttpServerMetrics<Long, Void, Void> createMetrics(HttpServer server, SocketAddress localAddress, HttpServerOptions serverOtions) {
    return new HttpServerMetricsImpl(localAddress, defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.HTTP_SERVER));
  }

  @Override
  public HttpClientMetrics createMetrics(HttpClient client, HttpClientOptions options) {
    return new HttpClientMetricsImpl(defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.HTTP_CLIENT));
  }

  @Override
  public TCPMetrics createMetrics(NetServer server, SocketAddress localAddress, NetServerOptions options) {
    return new TCPServerMetricsImpl(localAddress, defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.NET_SERVER));
  }

  @Override
  public TCPMetrics createMetrics(NetClient client, NetClientOptions options) {
    return new TCPClientMetricsImpl(defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.NET_CLIENT));
  }

  @Override
  public DatagramSocketMetrics createMetrics(DatagramSocket socket, DatagramSocketOptions options) {
    return new DatagramSocketMetricsImpl(defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.DATAGRAM_SOCKET));
  }

  @Override
  public <P> PoolMetrics<?> createMetrics(P pool, String poolType, String poolName, int maxPoolSize) {
    return new PoolMetricsImpl(poolType, poolName, maxPoolSize, defaultLabels, !this.options.isMetricsTypeDisabled(MetricsType.POOL));
  }
}