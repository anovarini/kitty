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

import org.apache.kitty.Command
import org.apache.kitty.utils.Constants
import org.apache.kitty.client.Client
import org.apache.kitty.listener.ChangeDomainListener

final class ChangeDomain implements Command {

    final String commandName = 'changedomain'
    final String commandArguments = '[domain]'
    final String commandDescription = 'Moves to the selected domain, or to the root when executed with no parameters.'

    @groovy.beans.ListenerList
    List<ChangeDomainListener> listeners

    @Override
    void execute(Client client, String... args) {
        String domain = null
        if (1 == args.size()) {
            domain = args[0]
        }
        if (client.connected) {
            client.domain = domain
            fireDomainChanged domain
        }
        else {
            fireChangeDomainFailed Constants.ERROR_NOT_CONNECTED
        }
    }
}
