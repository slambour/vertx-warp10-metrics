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

import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.spi.metrics.EventBusMetrics;
import io.vertx.ext.warp10.impl.VertxMetricsImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sébastien lambour
 */
public class EventBusMetricsImpl extends AbstractMetricsImpl implements EventBusMetrics<EventBusHandlerMetrics> {

  public static String SENSISION_CLASS_EB_REGISTERED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.handler.registered";
  public static String SENSISION_CLASS_EB_UNREGISTERED = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.handler.unregistered";

  public static String SENSISION_CLASS_EB_MESSAGE_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.handle.message.error.count";
  public static String SENSISION_CLASS_EB_MESSAGE_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.handle.message.count";
  public static String SENSISION_CLASS_EB_MESSAGE_TIME = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.handle.message.time";

  public static String SENSISION_CLASS_EB_MESSAGE_SENT_LOCAL_COUNT     = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.producer.sent.local.count";
  public static String SENSISION_CLASS_EB_MESSAGE_SENT_REMOTE_COUNT    = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.producer.sent.remote.count";
  public static String SENSISION_CLASS_EB_MESSAGE_PUBLISH_LOCAL_COUNT  = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.producer.publish.local.count";
  public static String SENSISION_CLASS_EB_MESSAGE_PUBLISH_REMOTE_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.producer.publish.remote.count";

  public static String SENSISION_CLASS_EB_MESSAGE_RECEIVED_LOCAL_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.consumer.received.local.count";
  public static String SENSISION_CLASS_EB_MESSAGE_RECEIVED_REMOTE_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.consumer.received.remote.count";
  public static String SENSISION_CLASS_EB_MESSAGE_DELIVERED_LOCAL_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.consumer.delivered.local.count";
  public static String SENSISION_CLASS_EB_MESSAGE_DELIVERED_REMOTE_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.consumer.delivered.remote.count";

  public static String SENSISION_CLASS_EB_MESSAGE_SENT_BYTES = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.sent.bytes";
  public static String SENSISION_CLASS_EB_MESSAGE_RECEIVED_BYTES = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.received.bytes";
  public static String SENSISION_CLASS_EB_MESSAGE_REPLY_ERROR_COUNT = VertxMetricsImpl.SENSISION_CLASS_PREFIX + "eventbus.message.reply.error.count";

  public static String SENSISION_CLASS_EB_LABEL_ADDRESS = "address";

  public EventBusMetricsImpl(Map<String,String> defaults, Boolean enabled) {
    this.enabled = enabled;
    defaultLabels.putAll(defaults);
  }

  @Override
  public EventBusHandlerMetrics handlerRegistered(String address, String repliedAddress) {
    eventMetric(SENSISION_CLASS_EB_REGISTERED, defaultLabels, address);
    return new EventBusHandlerMetrics(address);
  }

  @Override
  public void handlerUnregistered(EventBusHandlerMetrics handler) {
    eventMetric(SENSISION_CLASS_EB_UNREGISTERED, defaultLabels, handler.getAddress());
  }

  @Override
  public void beginHandleMessage(EventBusHandlerMetrics handler, boolean local) {
    handler.resetTimer();
  }

  @Override
  public void endHandleMessage(EventBusHandlerMetrics handler, Throwable failure) {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_CLASS_EB_LABEL_ADDRESS, handler.getAddress());
    
    if (failure != null) {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_COUNT, labels);
      updateMetric(SENSISION_CLASS_EB_MESSAGE_TIME, labels, handler.elapsed());
    } else {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_ERROR_COUNT, labels);
    }
  }

  @Override
  public void messageSent(String address, boolean publish, boolean local, boolean remote) {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_CLASS_EB_LABEL_ADDRESS, address);

    if (publish && local) {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_PUBLISH_LOCAL_COUNT, labels);
    } else if (publish && remote)  {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_PUBLISH_REMOTE_COUNT, labels);
    } else if (!publish && local)  {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_SENT_LOCAL_COUNT, labels);
    } else if (!publish && remote)  {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_SENT_REMOTE_COUNT, labels);
    }
  }

  @Override
  public void messageReceived(String address, boolean publish, boolean local, int handlers) {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_CLASS_EB_LABEL_ADDRESS, address);

    if (local) {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_RECEIVED_LOCAL_COUNT, labels);
    } else {
      incrementMetric(SENSISION_CLASS_EB_MESSAGE_RECEIVED_REMOTE_COUNT, labels);
    }

    if (handlers > 0) {
      if (local) {
        incrementMetric(SENSISION_CLASS_EB_MESSAGE_DELIVERED_LOCAL_COUNT, labels);
      } else {
        incrementMetric(SENSISION_CLASS_EB_MESSAGE_DELIVERED_REMOTE_COUNT, labels);
      }
    }
  }

  @Override
  public void messageWritten(String address, int numberOfBytes) {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_CLASS_EB_LABEL_ADDRESS, address);
    updateMetric(SENSISION_CLASS_EB_MESSAGE_SENT_BYTES, labels, numberOfBytes);
  }

  @Override
  public void messageRead(String address, int numberOfBytes) {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_CLASS_EB_LABEL_ADDRESS, address);
    updateMetric(SENSISION_CLASS_EB_MESSAGE_RECEIVED_BYTES, labels, numberOfBytes);
  }

  @Override
  public void replyFailure(String address, ReplyFailure failure) {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels);
    labels.put(SENSISION_CLASS_EB_LABEL_ADDRESS, address);
    incrementMetric(SENSISION_CLASS_EB_MESSAGE_REPLY_ERROR_COUNT, labels);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void close() {

  }
}
