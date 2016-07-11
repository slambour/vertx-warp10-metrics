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

import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.WebSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.spi.metrics.HttpClientMetrics;
import io.vertx.ext.warp10.impl.VertxMetricsImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sébastien lambour
 */
public class HttpClientMetricsImpl extends AbstractMetricsImpl implements HttpClientMetrics<Long, SocketAddress, SocketAddress, Void, Void> {

  public static String SENSISION_CLASS_REQUEST_TIME = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.request.time";

  public static String SENSISION_CLASS_REQUEST_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.request.count";

  public static String SENSISION_CLASS_CONNECTED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.connected";
  public static String SENSISION_CLASS_DISCONNECTED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.disconnected";
  public static String SENSISION_CLASS_CONNECTIONS = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.connections";

  public static String SENSISION_CLASS_WEBSOCKET_CONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.websocket.connected.count";
  public static String SENSISION_CLASS_WEBSOCKETS_DISCONNECTED_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.websocket.disconnected.count";
  public static String SENSISION_CLASS_WEBSOCKETS = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.websockets";

  public static String SENSISION_CLASS_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.error.count";

  public static String SENSISION_CLASS_BYTES_RECEIVED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.received.bytes";
  public static String SENSISION_CLASS_BYTES_SENT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.sent.bytes";

  public static String SENSISION_CLASS_RESET_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.reset.count";
  public static String SENSISION_CLASS_RESET_TIME = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "client.http.reset.time";

  public static String SENSISION_LABEL_HOST = "server.host";
  public static String SENSISION_LABEL_PORT = "server.port";
  public static String SENSISION_LABEL_STATUS = "http.status";

  private final AtomicLong httpConnections = new AtomicLong(0);
  private final AtomicLong wsConnections = new AtomicLong(0);

  public HttpClientMetricsImpl(Map<String,String> defaults, Boolean enabled) {
    this.enabled = enabled;
    defaultLabels.putAll(defaults);
  }

  @Override
  public Void createEndpoint(String host, int port, int poolSize) {
    return null;
  }

  @Override
  public void closeEndpoint(String host, int port, Void aVoid) {
  }


  @Override
  public Void enqueueRequest(Void aVoid) {
    return null;
  }

  @Override
  public void dequeueRequest(Void aVoid, Void aVoid2) {

  }

  @Override
  public void endpointConnected(Void aVoid, SocketAddress socketAddress) {

  }

  @Override
  public void endpointDisconnected(Void aVoid, SocketAddress socketAddress) {

  }

  @Override
  public Long requestBegin(Void aVoid, SocketAddress socketAddress, SocketAddress socketAddress2, SocketAddress socketAddress1, HttpClientRequest httpClientRequest) {
    return System.nanoTime();
  }

  @Override
  public void requestEnd(Long aLong) {

  }

  @Override
  public void responseBegin(Long aLong, HttpClientResponse httpClientResponse) {

  }

  @Override
  public Long responsePushed(Void aVoid, SocketAddress socketMetric, SocketAddress localAddress, SocketAddress remoteAddress, HttpClientRequest request) {
    return null;
  }


  @Override
  public void requestReset(Long nanoStart) {
    long requestProcessingTime = System.nanoTime() - nanoStart;
    updateMetric(SENSISION_CLASS_RESET_TIME, defaultLabels, requestProcessingTime);
    incrementMetric(SENSISION_CLASS_RESET_COUNT, defaultLabels);
  }

  @Override
  public void responseEnd(Long nanoStart, HttpClientResponse response) {
    long requestProcessingTime = System.nanoTime() - nanoStart;

    Map<String,String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_LABEL_STATUS, Integer.toString(response.statusCode()));

    updateMetric(SENSISION_CLASS_REQUEST_TIME, defaultLabels, requestProcessingTime);
    incrementMetric(SENSISION_CLASS_REQUEST_COUNT, defaultLabels);
  }

  @Override
  public SocketAddress connected(Void aVoid, SocketAddress socketAddress, WebSocket webSocket) {
    long value = wsConnections.incrementAndGet();
    setMetric(SENSISION_CLASS_WEBSOCKETS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_WEBSOCKET_CONNECTED_COUNT, defaultLabels);

    return null;
  }

  @Override
  public void disconnected(SocketAddress webSocketMetric) {
    long value = wsConnections.decrementAndGet();
    setMetric(SENSISION_CLASS_WEBSOCKETS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_WEBSOCKETS_DISCONNECTED_COUNT, defaultLabels);
  }

  @Override
  public SocketAddress connected(SocketAddress remoteAddress, String remoteName) {
    long value = httpConnections.incrementAndGet();
    setMetric(SENSISION_CLASS_CONNECTIONS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_CONNECTED, defaultLabels);
    return remoteAddress;
  }

  @Override
  public void disconnected(SocketAddress socketMetric, SocketAddress remoteAddress) {
    long value = httpConnections.decrementAndGet();
    setMetric(SENSISION_CLASS_CONNECTIONS, defaultLabels, value);
    incrementMetric(SENSISION_CLASS_DISCONNECTED, defaultLabels);
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
