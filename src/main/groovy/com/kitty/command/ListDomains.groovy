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

import com.kitty.Command
import com.kitty.client.Client
import com.kitty.listener.ListDomainListener
import com.kitty.utils.Constants

final class ListDomains implements Command {

    final String commandName = 'listdomains'
    final String commandArguments = ''
    final String commandDescription = 'Lists domains.'

    @groovy.beans.ListenerList
    List<ListDomainListener> listeners

    @Override
    void execute(Client client, String... args) {
        if (client.connected) {
            def domains = client.domains()
            if (domains) {
                domains.eachWithIndex() { obj, i -> fireDomainListed("${i}: ${obj}") }
            }
        }
        else {
            fireListDomainFailed Constants.ERROR_NOT_CONNECTED
        }
    }
}
