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

/**
 * = Warp10 Metrics
 *
 * This project is an implementation of the Vert.x Metrics Service Provider Interface (SPI): metrics built from Vert.x
 * events will be sent to Warp10 via Sensision agent , an http://www.warp10.io[open source platform dedicated to Time Series analysis].
 *
 * Collect Vert.x metrics with Warp10 goals is to automate analysis (capacity planing, anomaly detection, alerting) of a Vert.x cluster with
 * http://www.warp10.io/reference[WarpScript] a language dedicated to Time Series analysis.
 *
 * == Features
 *
 *  * Vert.x core tools monitoring: TCT/HTTP client and servers, {@link io.vertx.core.datagram.DatagramSocket}, {@link io.vertx.core.eventbus.EventBus} and pool metrics
 *  will be collected by https://github.com/cityzendata/sensision[a local sensision agent]
 *  * Sensision agent forward metrics periodically to http://www.warp10.io[Warp10 platform]
 *
 * == Getting started
 *
 * The _${maven.artifactId}_ module must be present in the classpath.
 *
 * Maven users should add this to their project POM file:
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>${maven.groupId}</groupId>
 *   <artifactId>${maven.artifactId}</artifactId>
 *   <version>${maven.version}</version>
 * </dependency>
 * ----
 *
 * And Gradle users, to their build file:
 *
 * [source,subs="+attributes"]
 * ----
 * compile '${maven.groupId}:${maven.artifactId}:${maven.version}'
 * ----
 *
 * Vert.x does not enable SPI implementations by default. You must enable metric collection in the Vert.x options:
 *
 * [source,$lang]
 * ----
 * {@link examples.MetricsExamples#setup()}
 * ----
 *
 * == Command line activation
 *
 * When running Vert.x from the command line interface, metrics can be activated via JVM system properties. System properties beginning with vertx.metrics.options. are transmitted to the metrics options.
 * The vertx.metrics.options.enabled is a standard Vert.x Core option for enabling the metrics implementations, this options must be set to true.
 *
 * == Configuration
 *
 * A Docker image containing a Warp10 platform is available on https://hub.docker.com/r/warp10io/warp10/[dockerhub]. This images contains a standalone version of Warp10
 * and a pre-configured sensision agent. Data folder must be accessible outside the container, so sensision lib can directly write inside.
 * On a "production deployment" metrics are periodically collected throw http by sensision agent.
 *
 * ----
 * docker run --volume=/var/warp10:/data -p 8080:8080 -p 8081:8081 -d -i warp10io/warp10:1.0.6
 * ----
 *
 * The configuration of sensision lib is given with JVM properties throw Vert.x's JVM. The configuration below force the internal scheduler to write periodically the
 * metrics on filesystem instead of http pooling (used in production).
 * Don't forget metric activation if you use the Vert.x command line interface.
 *
 * ----
 * -Dvertx.metrics.options.enabled=true
 * -Dsensision.home=/var/warp10/sensision/data
 * -Dsensision.events.dir=/var/warp10/sensision/data/metrics
 * -Dsensision.dump.period=60000
 * -Dsensision.dump.currentts=true
 * -Dsensision.dump.onexit=true
 * ----
 *
 * == Accessing your data
 *
 * === Generate the sensision read token (from Docker image)
 *
 * Tokens are generated with command line tool http://www.warp10.io/tools/worf[Worf]
 * By default a write token is generated at container's startup and saved in the configuration file /data/sensision/etc/sensision.conf
 *
 * First get the sensision write token
 * * from docker
 * ----
 * docker exec  -t -i e463278182a5 cat /data/sensision/etc/sensision.conf | grep sensision.qf.token.default
 * ----
 * * or on local file system
 * ----
 * cat /var/warp10/sensision/etc/sensision.conf | grep sensision.qf.token.default
 * ----
 *
 * You must start Worf on the same Warp10's container (secret keys are shared in the configuration file)
 *
 * ----
 * docker exec -t -i <WARP10's CONTAINER_ID> worf.sh
 *
 * warp10> decodeToken
 * decodeToken/token>XYES2vEZl.........(write token from sensision.conf)
 * decodeToken(decode | cancel)>decode
 * TOKEN INFO BEGIN
 * issuance date=2016-05-20T12:24:17.032 UTC
 * expiry date=2116-04-26T12:24:17.032 UTC
 * type=WRITE
 * application name=io.warp10.sensision
 * owner=446e681d-252e-43bd-ba1c-ec9c7a6ae629
 * producer=446e681d-252e-43bd-ba1c-ec9c7a6ae629
 * TOKEN INFO END
 * decodeTokenconvert to read token ? (yes)>yes
 * read token=fbRCRSimQ1a__Yqb1H05tw9xoAgriPzbnJfawVjj.cdWDD..... your sensision read token
 * ----
 *
 * You have now the sensision read token, you can starts playing with you metrics
 *
 * === Fetch a vertx metric with Quantum
 * The best way is use http://127.0.0.1:8081[Quantum] web components exposed by the container
 * The example below fetch the 10 lasts values with classnames matches the regexp '~io.vertx.metrics.warp10.*'
 *
 * ----
 * 'YOUR READ TOKEN' 'token' STORE
 * [
 *   $token
 *   '~io.vertx.metrics.warp10.*'
 *   {}
 *   NOW -10
 * ] FETCH
 * ----
 *
 * == Vert.x core metrics
 *
 * This section lists all the metrics generated by monitoring the Vert.x core measurable components.
 *
 * === Net Client
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Labels
 * |Description
 *
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.tcp.connected.count}
 * |default
 * |Total number of connected event
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.tcp.disconnected.count}
 * |default
 * |Total number of disconnected event
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.client.tcp.connections}
 * |default
 * |Number of connections currently opened.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.tcp.received.bytes}
 * |default
 * |Total number of bytes received.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.tcp.sent.bytes}
 * |default
 * |Total number of bytes sent.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.tcp.error.count}
 * |default
 * |Total number of errors.
 *
 * |===
 *
 * === Net Server
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Metric labels
 * |Description
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.tcp.connections.count}
 * |{'host':<host>,'port':<port>}
 * |Total number of connections accepted by the TCP server
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.server.tcp.connections.active}
 * |{'host':<host>,'port':<port>}
 * |Number of opened connections to the Net Server listening on the {@code <host>:<port>} address.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.tcp.connections.count}
 * |{'host':<host>,'port':<port>}
 * |Total number of connections accepted by the Net Server listening on the {@code <host>:<port>} address.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.tcp.error.count}
 * |{'host':<host>,'port':<port>}
 * |Total number of errors on the Net Server listening on the {@code <host>:<port>} address.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.tcp.received.bytes}
 * |{'host':<host>,'port':<port>}
 * |Total number of bytes received by the Net Server listening on the {@code <host>:<port>} address.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.tcp.sent.bytes}
 * |{'host':<host>,'port':<port>}
 * |Total number of bytes sent to the Net Server listening on the {@code <host>:<port>} address.
 *
 * |===
 *
 * === HTTP Client
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Metric labels
 * |Description
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.connected.count}
 * |default
 * |Total number of connected event.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.disconnected.count}
 * |default
 * |Total number of disconnected event.
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.client.http.connections}
 * |default
 * |Number of http connections currently opened.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.request.count}
 * |default
 * |Number of http requests sent.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.request.time}
 * |default
 * |Processing time (in nanoseconds) of sent http requests.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.websocket.connected.count}
 * |default
 * |Total number of websockets connected event.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.websocket.disconnected.count}
 * |default
 * |Total number of websockets disconnected event.
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.client.websockets}
 * |default
 * |Number of websockets currently opened.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.error.count}
 * |default
 * |Total number of errors.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.received.bytes}
 * |default
 * |Total number of bytes received from remote hosts.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.sent.bytes}
 * |default
 * |Total number of bytes sent to the remote host.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.reset.count}
 * |default
 * |Total number of resets
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.client.http.reset.time}
 * |default
 * |Time (accumulated) in nanoseconds before reset
 *
 * |===
 *
 * === HTTP Server
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Metric labels
 * |Description
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.connected.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of connected event.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.disconnected.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of disconnected event.
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.server.http.connections}
 * |{'server.host':<host>,'server.port':<port>}
 * |Number of requests being processed.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.request.count}
 * |{'server.host':<host>,'server.port':<port>,'http.status':<status>}
 * |Number of http requests processed.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.request.time}
 * |{'server.host':<host>,'server.port':<port>,'http.status':<status>}
 * |Processing time (in nanoseconds) of http requests.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.websocket.connected.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of websockets connected event.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.websocket.upgrade.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of websockets upgrade event.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.websocket.disconnected.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of websockets disconnected event.
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.server.websockets}
 * |{'server.host':<host>,'server.port':<port>}
 * |Number of websockets currently opened.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.error.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of errors.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.reset.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of resets.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.reset.time}
 * |{'server.host':<host>,'server.port':<port>}
 * |Time (accumulated) in nanoseconds before reset
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.received.bytes}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of bytes received by the HTTP Server listening on the {@code <host>:<port>} address.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.http.sent.bytes}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of bytes sent to the HTTP Server listening on the {@code <host>:<port>} address.
 *
 * |===
 *
 * === Datagram socket
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Metric labels
 * |Description
 *
 * |Event
 * |{@code io.vertx.metrics.warp10.server.datagram.listening.name}
 * |{'server.host':<host>,'server.port':<port>}
 * | listening event store with the local name
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.datagram.socket.received.bytes}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of bytes received on the {@code <host>:<port>} listening address.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.datagram.socket.sent.bytes}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of bytes sent to the remote host.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.server.datagram.socket.error.count}
 * |{'server.host':<host>,'server.port':<port>}
 * |Total number of errors.
 *
 * |===
 *
 * === Event Bus
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Metric labels
 * |Description
 *
 * |Event
 * |{@code io.vertx.metrics.warp10.eventbus.handler.registered}
 * |default
 * |Handler registered event on the event bus at a given {@code address}
 *
 * |Event
 * |{@code io.vertx.metrics.warp10.eventbus.handler.unregistered}
 * |default
 * |Handler unregistered event on the event bus at a given {@code address}
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.handle.message.count}
 * |{'address':<handler address>}
 * |Total number of messages handled by handlers listening to the {@code address}.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.handle.message.time}
 * |{'address':<handler address>}
 * |Cumulated processing time for handlers listening to the {@code address}.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.handle.message.error.count}
 * |{'address':<handler address>}
 * |Total number of errors for handlers listening to the {@code address}.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.sent.bytes}
 * |{'address':<handler address>}
 * |Total number of bytes (by address) sent while sending messages to event bus.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.received.bytes}
 * |{'address':<handler address>}
 * |Total number of bytes (by address) sent while sending messages to event bus.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.reply.error.count}
 * |{'address':<handler address>}
 * |Total number of message reply failures by address
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.producer.sent.local.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages sent (point-to-point) for local messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.producer.sent.remote.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages sent (point-to-point) for remote messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.producer.publish.local.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages published (publish / subscribe) for local messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.producer.publish.remote.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages published (publish / subscribe) for remote messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.consumer.received.local.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages received for local messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.consumer.received.remote.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages received for remote messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.consumer.delivered.local.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages delivered to handlers for local messages only.
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.eventbus.message.consumer.delivered.remote.count}
 * |{'address':<handler address>}
 * |Total number (by address) of messages delivered to handlers for remote messages only.
 *
 * |===
 *
 * === Pool
 *
 * [cols="15,50,35,35", options="header"]
 * |===
 * |Metric type
 * |Metric name
 * |Metric labels
 * |Description
 *
 * |Gauge
 * |{@code io.vertx.metrics.warp10.pool.maxSize}
 * |{'type':<pool type>,'name':<pool name>}
 * |Max size of the pool (normally stable value).
 *
 * |Counter
 * |{@code io.vertx.metrics.warp10.pool.submitted.count}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total number of tasks submitted to access the resource.
 *
 * |Counter
 * |{@code io.vertx.metrics.pool.rejected.count}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total number of tasks rejected
 *
 * |Counter
 * |{@code io.vertx.metrics.pool.rejected.time}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total time elapsed by tasks before been rejected
 *
 * |Counter
 * |{@code io.vertx.metrics.pool.queued.count}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total number of tasks waiting in queue
 *
 * |Counter
 * |{@code io.vertx.metrics.pool.queued.time}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total time elapsed by tasks waiting in queue
 *
 * |Counter
 * |{@code io.vertx.metrics.pool.working.count}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total number of tasks using a resource
 *
 * |Counter
 * |{@code io.vertx.metrics.pool.working.time}
 * |{'type':<pool type>,'name':<pool name>}
 * |Total time elapsed by working tasks
 *
 * |Gauge
 * |{@code io.vertx.metrics.pool.working}
 * |{'type':<pool type>,'name':<pool name>}
 * |Number of pool resources currently working.
 *
 * |===
 *
 *
 * Note that Warp10 platform understands all timestamps as microseconds since January 1, 1970, 00:00:00 UTC.
 */
@ModuleGen(name = "vertx-warp10", groupPackage = "io.vertx")
@Document(fileName = "index.adoc") package io.vertx.ext.warp10;

import io.vertx.codegen.annotations.ModuleGen;
import io.vertx.docgen.Document;