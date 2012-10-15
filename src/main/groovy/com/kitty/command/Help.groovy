/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package com.kitty.command

import com.kitty.IODevice
import com.kitty.Command
import com.kitty.client.Client

final class Help implements Command {

    final String commandName = 'help'
    final String commandArguments = ''
    final String commandDescription = 'Shows this help message.'

    final IODevice ioDevice
    final def commandDescriptions

    Help(IODevice ioDevice, def commandDescriptions) {
        this.ioDevice = ioDevice
        this.commandDescriptions = commandDescriptions
    }

	void execute(Client client, String... args)
	{
        def formattedHelpRow = commandDescriptions.collect {
            "$it.commandName $it.commandArguments".trim() + " - $it.commandDescription"
        }
        formattedHelpRow.sort().each { ioDevice.write it}
	}

}
