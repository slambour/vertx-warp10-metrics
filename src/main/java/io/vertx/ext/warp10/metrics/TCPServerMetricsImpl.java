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
package io.vertx.ext.warp10.metrics;

import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.TCPMetrics;
import io.vertx.ext.warp10.impl.VertxMetricsImpl;
import io.warp10.sensision.Sensision;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sébastien lambour
 */
public class TCPServerMetricsImpl extends AbstractMetricsImpl implements TCPMetrics<Void> {

  public static String SENSISION_CLASS_CONNECTIONS_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.tcp.connections.count";
  public static String SENSISION_CLASS_ACTIVE_CONNECTIONS_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.tcp.connections.active";
  public static String SENSISION_CLASS_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.tcp.error.count";

  public static String SENSISION_CLASS_BYTES_RECEIVED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.tcp.received.bytes";
  public static String SENSISION_CLASS_BYTES_SENT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.tcp.sent.bytes";

  private final AtomicLong connections = new AtomicLong(0);

  public static String SENSISION_LABEL_HOST = "host";
  public static String SENSISION_LABEL_PORT = "port";

  public TCPServerMetricsImpl(SocketAddress localAddress, Map<String,String> defaults, Boolean enabled) {
    this.enabled = enabled;
    defaultLabels.putAll(defaults);
    defaultLabels.put(SENSISION_LABEL_HOST, localAddress.host());
    defaultLabels.put(SENSISION_LABEL_PORT, Integer.toString(localAddress.port()));
  }

  @Override
  public Void connected(SocketAddress remoteAddress, String remoteName) {
    long value = connections.incrementAndGet();
    incrementMetric(SENSISION_CLASS_CONNECTIONS_COUNT, defaultLabels);
    setMetric(SENSISION_CLASS_ACTIVE_CONNECTIONS_COUNT, defaultLabels, value);
    return null;
  }

  @Override
  public void disconnected(Void socketMetric, SocketAddress remoteAddress) {
    long value = connections.decrementAndGet();
    setMetric(SENSISION_CLASS_ACTIVE_CONNECTIONS_COUNT, defaultLabels, value);
  }

  @Override
  public void bytesRead(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
    updateMetric(SENSISION_CLASS_BYTES_RECEIVED, defaultLabels, numberOfBytes);
  }

  @Override
  public void bytesWritten(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
    updateMetric(SENSISION_CLASS_BYTES_SENT, defaultLabels, numberOfBytes);
  }

  @Override
  public void exceptionOccurred(Void socketMetric, SocketAddress remoteAddress, Throwable t) {
    incrementMetric(SENSISION_CLASS_ERROR_COUNT, defaultLabels);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void close() {
  }
}
