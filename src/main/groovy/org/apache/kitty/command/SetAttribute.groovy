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

import org.apache.kitty.IODevice
import org.apache.kitty.Command
import org.apache.kitty.utils.Constants
import org.apache.kitty.client.Client

final class SetAttribute implements Command {

    String commandName = 'set'
    String commandArguments = 'attribute value'
    String commandDescription = 'Changes the value of a given attribute'

    IODevice ioDevice

    SetAttribute(IODevice ioDevice) {
        this.ioDevice = ioDevice
    }

    @Override
    void execute(Client client, String... args) {
        if (client.connected) {
            String attr = args[0]
            String val = args[1]
            client.set(attr, val)
        }
        else {
            ioDevice.write Constants.ERROR_NOT_CONNECTED
        }
    }
}
