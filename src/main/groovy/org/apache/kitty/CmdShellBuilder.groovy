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
import jline.ConsoleReader
import org.apache.kitty.io.terminal.Terminal
import org.apache.kitty.command.Help
import org.apache.kitty.command.Connect
import org.apache.kitty.command.Disconnect

import org.apache.kitty.validation.ConnectValidation
import org.apache.kitty.command.ListDomains
import org.apache.kitty.listener.ChangeDomainListener
import org.apache.kitty.command.ChangeDomain
import org.apache.kitty.command.Ls

class CmdShellBuilder {

    CmdShell build() {
        IODevice terminal = new Terminal(new ConsoleReader())

        def commands = []

        final def help = new Help(terminal, commands)
        final def connect = new Connect(new ConnectValidation())
        connect.addConnectListener(terminal)

        def disconnect = new Disconnect()
        disconnect.addDisconnectListener(terminal)

        def listDomains = new ListDomains()
        listDomains.addListDomainListener(terminal)

        def changeDomain = new ChangeDomain()
        changeDomain.addChangeDomainListener(terminal)

        def ls = new Ls()
        ls.addLsListener(terminal)

        commands.add(help)
        commands.add(connect)
        commands.add(disconnect)
        commands.add(listDomains)
        commands.add(changeDomain)
        commands.add(ls)
//        commands.add(new Cd(terminal))
//        commands.add(new GetAttribute.groovy(terminal))
//        commands.add(new SetAttribute(terminal))
//        commands.add(new Invoke(terminal))
//        commands.add(new Pwd(terminal))
//        commands.add(new ChangeDomain())

        terminal.setup(commands)
        new CmdShell(commands, terminal, new Client())
    }
}
