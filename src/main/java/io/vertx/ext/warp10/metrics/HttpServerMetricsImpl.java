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

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpServerMetrics;
import io.vertx.ext.warp10.impl.VertxMetricsImpl;
import io.warp10.sensision.Sensision;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author Sébastien lambour
 */
public class HttpServerMetricsImpl extends AbstractMetricsImpl implements HttpServerMetrics<Long, Void, Void> {

  public static String SENSISION_CLASS_REQUEST_TIME = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.request.time";

  public static String SENSISION_CLASS_REQUEST_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.request.count";

  public static String SENSISION_CLASS_CONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.connected.count";
  public static String SENSISION_CLASS_DISCONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.disconnected.count";
  public static String SENSISION_CLASS_CONNECTIONS = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.connections";

  public static String SENSISION_CLASS_WEBSOCKET_CONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.websocket.connected.count";
  public static String SENSISION_CLASS_WEBSOCKET_DISCONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.websocket.disconnected.count";
  public static String SENSISION_CLASS_WEBSOCKET_UPGRADE_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.websocket.upgrade.count";
  public static String SENSISION_CLASS_WEBSOCKETS = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.websockets";

  public static String SENSISION_CLASS_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.error.count";

  public static String SENSISION_CLASS_RESET_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.reset.count";
  public static String SENSISION_CLASS_RESET_TIME = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.reset.time";

  public static String SENSISION_CLASS_BYTES_RECEIVED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.received.bytes";
  public static String SENSISION_CLASS_BYTES_SENT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "server.http.sent.bytes";

  public static String SENSISION_LABEL_HOST = "server.host";
  public static String SENSISION_LABEL_PORT = "server.port";
  public static String SENSISION_LABEL_STATUS = "http.status";

  private final AtomicLong httpConnections = new AtomicLong(0);
  private final AtomicLong wsConnections = new AtomicLong(0);

  public HttpServerMetricsImpl(SocketAddress localAddress, Map<String,String> defaults, Boolean enabled) {
    this.enabled = enabled;
    defaultLabels.putAll(defaults);
    defaultLabels.put(SENSISION_LABEL_HOST, localAddress.host());
    defaultLabels.put(SENSISION_LABEL_PORT, Integer.toString(localAddress.port()));
  }

  @Override
  public Long requestBegin(Void socketMetric, HttpServerRequest request) {
    return System.nanoTime();
  }

  @Override
  public void requestReset(Long nanoStart) {
    long requestProcessingTime = System.nanoTime() - nanoStart;
    updateMetric(SENSISION_CLASS_RESET_TIME, defaultLabels, requestProcessingTime);
    incrementMetric(SENSISION_CLASS_RESET_COUNT, defaultLabels);
  }

  @Override
  public Long responsePushed(Void socketMetric, HttpMethod method, String uri, HttpServerResponse response) {
    return null;
  }

  @Override
  public void responseEnd(Long nanoStart, HttpServerResponse response) {
    long requestProcessingTime = System.nanoTime() - nanoStart;

    Map<String,String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_LABEL_STATUS, Integer.toString(response.getStatusCode()));

    updateMetric(SENSISION_CLASS_REQUEST_TIME, labels, requestProcessingTime);
    incrementMetric(SENSISION_CLASS_REQUEST_COUNT, labels);

  }

  @Override
  public Void upgrade(Long requestMetric, ServerWebSocket serverWebSocket) {
    incrementMetric(SENSISION_CLASS_WEBSOCKET_UPGRADE_COUNT, defaultLabels);
    return null;
  }

  @Override
  public Void connected(Void socketMetric, ServerWebSocket serverWebSocket) {
    long value = wsConnections.incrementAndGet();
    setMetric(SENSISION_CLASS_WEBSOCKETS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_WEBSOCKET_CONNECTED_COUNT, defaultLabels);
    return null;
  }

  @Override
  public void disconnected(Void serverWebSocketMetric) {
    long value = wsConnections.decrementAndGet();
    setMetric(SENSISION_CLASS_WEBSOCKETS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_WEBSOCKET_DISCONNECTED_COUNT, defaultLabels);
  }

  @Override
  public Void connected(SocketAddress remoteAddress, String remoteName) {
    long value = httpConnections.incrementAndGet();
    setMetric(SENSISION_CLASS_CONNECTIONS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_CONNECTED_COUNT, defaultLabels);
    return null;
  }

  @Override
  public void disconnected(Void socketMetric, SocketAddress remoteAddress) {
    long value = httpConnections.decrementAndGet();
    setMetric(SENSISION_CLASS_CONNECTIONS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_DISCONNECTED_COUNT, defaultLabels);
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
