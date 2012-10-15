/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kitty.command

import org.apache.kitty.client.Client
import org.apache.kitty.listener.ConnectListener
import org.apache.kitty.Validation

class ConnectTest extends GroovyTestCase {

    Connect command
    String eventFired
    ConnectListener connectListener

    void setUp() {

        def noValidation = {} as Validation
        command = new Connect(noValidation)
        eventFired = ''

        connectListener = [
                'connectSucceeded': { host, port -> eventFired = "$host:$port" },
                'connectFailed': { eventFired = 'error' }
        ] as ConnectListener

        command.addConnectListener connectListener
    }

    void testShouldFireConnectionSucceededWhenConnectedToClient() {

        def client = new Client() {
            void connect(def host, def port, def user, def password) {}

            boolean isConnected() {
                true
            }
        }

        command.execute client, 'myHost', 'myPort'

        assertEquals "Event fired", "myHost:myPort", eventFired
    }

    void testShouldFireConnectionErrorOccurredWhenSomeErrorDuringConnectionHappens() {

        def client = new Client() {
            void connect(def host, def port, def user, def password) {}

            boolean isConnected() {
                false
            }
        }

        command.execute client, 'myHost', 'myPort'

        assertEquals "Event fired", "error", eventFired
    }
}
