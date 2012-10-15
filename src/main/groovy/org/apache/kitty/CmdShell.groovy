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
package org.apache.kitty

import org.apache.kitty.client.Client

class CmdShell {
    Client client
    IODevice ioDevice
    def commands

    CmdShell(def commands, IODevice ioDevice, Client client) {
        this.commands = commands
        this.ioDevice = ioDevice
        this.client = client
    }

    void startShell() {
        LOOP: while (true) {
            def input = ioDevice.read()

            if (!input.isEmpty()) {
                try {
                    inputHandler(input)
                }
                catch (Exception e) {
                    ioDevice.write(e.getMessage())
                }

                if (["exit", "quit"].contains(input.tokenize().get(0)))
                    break LOOP
            }
        }
    }

    void inputHandler(String input) {
        String[] params = []
        String currentCommand

        if (input.contains(" ")) {
            String[] parsedCommandLine = input.split(" ")
            currentCommand = parsedCommandLine[0]
            params = parsedCommandLine.tail()
        }
        else {
            currentCommand = input
        }

        def commandNames = commands*.commandName
        if (commandNames.contains(currentCommand)) {
            int commandIndex = commandNames.indexOf(currentCommand)
            Command command = commands.get(commandIndex)
            command.execute(client, params)
        }
        else {
            ioDevice.write "$input: command not found"
        }
    }
}
