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
package org.apache.kitty.io.terminal

import jline.History
import jline.SimpleCompletor
import jline.ConsoleReader
import org.apache.kitty.IODevice
import org.apache.kitty.listener.ConnectListener
import org.apache.kitty.listener.DisconnectListener
import org.apache.kitty.listener.ChangeDomainListener
import org.apache.kitty.listener.ListDomainListener

class Terminal implements IODevice, ChangeDomainListener, ConnectListener, DisconnectListener, ListDomainListener {

    ConsoleReader reader
    String domain
    String host
    String port
    String user

    Terminal(ConsoleReader reader) {
        this.reader = reader
    }

    void setup(def commands) {
        reader.setBellEnabled false
        reader.setUseHistory true

        def historyFile = new File(System.getProperty("user.home"), "kitty.history")
        historyFile.createNewFile()
        def history = new History(historyFile)
        reader.setHistory history
        def completionValues = commands*.commandName as String[]
        reader.addCompletor(new SimpleCompletor(completionValues))
    }

    String read() {
        reader?.readLine(prompt())?.trim()
    }

    String prompt() {
        String user = this.user ?: ''
        String host = this.host ? "@${this.host}" : ''
        String port = this.port ? ":${this.port}" : ''
        String domain = this.domain ? "/${this.domain}" : ''
        String prompt = "$user$host$port$domain" ?: 'kitty'
        "$prompt> "
    }

    void write(String message) {
        echo message
    }

    void close() {
        reader.flushConsole()
    }

    @Override
    void domainChanged(String domain) {
        this.domain = domain
    }

    @Override
    void changeDomainFailed(String description) {
        echo description
    }

    @Override
    void connectSucceeded(String host, String port) {
        this.host = host
        this.port = port
    }

    @Override
    void connectSucceeded(String host, String port, String user) {
        this.host = host
        this.port = port
        this.user = user
    }

    @Override
    void connectFailed(String reason) {
        echo reason
    }

    private void echo(String message) {
        reader.printString "$message\n"
    }

    @Override
    void disconnectSucceeded() {
        this.host = null
        this.port = null
        this.user = null
    }

    @Override
    void disconnectFailed(String reason) {
        echo reason
    }

    @Override
    void domainListed(Object domain) {
        println "$domain"
    }

    @Override
    void listDomainFailed(Object reason) {
        println "$reason"
    }
}
