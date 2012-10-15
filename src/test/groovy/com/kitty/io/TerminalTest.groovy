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
package com.kitty.io

import com.kitty.io.terminal.Terminal

class TerminalTest extends GroovyTestCase {

    void testShouldReturnDefaultPromptWhenNotConnected() {
        Terminal terminal = new Terminal(null)
        String prompt = terminal.prompt()

        assertEquals "Default prompt", "kitty> ", prompt
    }

    void testShouldReturnHostAndPortAfterSuccessfullyConnected() {
        Terminal terminal = new Terminal(null)
        terminal.connectSucceeded 'aHost', 'aPort'

        String prompt = terminal.prompt()

        assertEquals "Prompt", "@aHost:aPort> ", prompt
    }

    void testShouldReturnUserHostAndPortAfterSuccessfullyConnectedWithCredentials() {
        Terminal terminal = new Terminal(null)
        terminal.connectSucceeded 'aHost', 'aPort', 'anUser'

        String prompt = terminal.prompt()

        assertEquals "Prompt", "anUser@aHost:aPort> ", prompt
    }

    void testShouldReturnHostPortAndDomainAfterSuccessfullyConnectedAndDomainSet() {
        Terminal terminal = new Terminal(null)
        terminal.connectSucceeded 'aHost', 'aPort'
        terminal.domainChanged 'aDomain'

        String prompt = terminal.prompt()

        assertEquals "Prompt", "@aHost:aPort/aDomain> ", prompt
    }
}
