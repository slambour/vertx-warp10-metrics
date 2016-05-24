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
public class TCPClientMetricsImpl extends AbstractMetricsImpl implements TCPMetrics<SocketAddress> {

  public static String SENSISION_CLASS_CONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.tcp.connected.count";
  public static String SENSISION_CLASS_DISCONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.tcp.disconnected.count";
  public static String SENSISION_CLASS_CONNECTIONS = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.tcp.connections";
  public static String SENSISION_CLASS_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.tcp.error.count";

  public static String SENSISION_CLASS_BYTES_RECEIVED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.tcp.received.bytes";
  public static String SENSISION_CLASS_BYTES_SENT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.tcp.sent.bytes";

  private final AtomicLong connections = new AtomicLong(0);

  public TCPClientMetricsImpl(Map<String,String> defaults, Boolean enabled) {
    this.enabled = enabled;
    defaultLabels.putAll(defaults);
  }

  @Override
  public SocketAddress connected(SocketAddress remoteAddress, String remoteName) {
    long value = connections.incrementAndGet();
    incrementMetric(SENSISION_CLASS_CONNECTED_COUNT, defaultLabels);
    setMetric(SENSISION_CLASS_CONNECTIONS, defaultLabels, value);
    return null;
  }

  @Override
  public void disconnected(SocketAddress socketMetric, SocketAddress remoteAddress) {
    long value = connections.decrementAndGet();
    incrementMetric(SENSISION_CLASS_DISCONNECTED_COUNT, defaultLabels);
    setMetric(SENSISION_CLASS_CONNECTIONS, defaultLabels, value);
  }

  @Override
  public void bytesRead(SocketAddress socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
    updateMetric(SENSISION_CLASS_BYTES_RECEIVED, defaultLabels, numberOfBytes);
  }

  @Override
  public void bytesWritten(SocketAddress socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
    updateMetric(SENSISION_CLASS_BYTES_SENT, defaultLabels, numberOfBytes);
  }

  @Override
  public void exceptionOccurred(SocketAddress socketMetric, SocketAddress remoteAddress, Throwable t) {
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
