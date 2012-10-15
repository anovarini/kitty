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

import com.kitty.IODevice
import com.kitty.Command
import com.kitty.client.Client
import com.kitty.utils.Constants

final class Cd implements Command {

    String commandName = 'cd'
    String commandArguments = 'path'
    String commandDescription = 'Change location'

    IODevice ioDevice

    Cd(IODevice ioDevice) {
        this.ioDevice = ioDevice
    }

    @Override
    void execute(Client client, String... args) {
        if (client.connected) {
            client.cd(args[0])
        }
        else {
            ioDevice.write Constants.ERROR_NOT_CONNECTED
        }
    }
}
