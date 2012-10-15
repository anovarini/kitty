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
package com.kitty.command

import com.kitty.client.Client
import com.kitty.Command
import com.kitty.listener.ConnectListener
import com.kitty.Validation

final class Connect implements Command {

    static final String HOST = "localhost"
    static final String PORT = "1099"

    final String commandName = 'connect'
    final String commandArguments = '[host] [host port] [host port user password]'
    final String commandDescription = 'Opens a connection to a jmx server.'

    final Validation validation

    @groovy.beans.ListenerList
    List<ConnectListener> listeners

    Connect(Validation validation) {
        this.validation = validation
    }

    @Override
    void execute(Client client, String... args) {
        assert validation != null, 'Validation must not be null'
        try {
            doExecute client, args
        }
        catch (e) {
            fireConnectFailed e.message
        }
    }

    void doExecute(Client client, String[] args) {
        validation.validate(args)

        def host = getHostOrDefault(args)
        def port = getPortOrDefault(args)

        if (secureConnectionRequested(args)) {
            String user = args[2]
            String password = args[3]
            client.connect host, port, user, password
            fireConnectSucceeded host, port, user
        }
        else {
            client.connect host, port
            fireConnectSucceeded host, port
        }
        if (!client.connected) {
            fireConnectFailed "Couldn't connect to $host:$port"
        }
    }

    private boolean secureConnectionRequested(String... args) {
        args?.length == 4
    }

    private String getPortOrDefault(String... args) {
        args.length >= 2 ? args[1] : PORT
    }

    private String getHostOrDefault(String... args) {
        args.length > 1 ? args[0] : HOST
    }
}
