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

package org.apache.kitty.client

import javax.management.Attribute
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import org.apache.kitty.exceptions.*

class Client {

    def host
    def port
    def domain
    def domainList
    def mBeansPath
    def remote
    def connector
    def url
    def mbean

    void connect(def host, def port) {
        connect(host, port, null, null)
    }

    void connect(def host, def port, def username, def password) {

        def serviceURL
        def properties = new HashMap()

        if (remote != null) {
            disconnect()
        }

        if ((username != null) && (password != null)) {
            properties.put JMXConnector.CREDENTIALS, [username, password] as String[]
        }

        serviceURL = "service:jmx:rmi:///jndi/rmi://$host:$port/jmxrmi"
        this.url = new JMXServiceURL(serviceURL)

        // TODO add auth & credentials to properties
        this.connector = JMXConnectorFactory.connect(this.url, properties)
        this.remote = this.connector.getMBeanServerConnection()
        if (this.remote != null) {
            this.host = host
            this.port = port
        }
    }

    public disconnect() {
        try {
            if (this.remote != null) {
                connector.close()
            }
        }
        finally {
            this.host = null
            this.port = null
            this.remote = null
            this.connector = null
            this.domain = null
            this.mBeansPath = null
        }
    }

    public domains() {
        if (this.remote) {
            this.domainList = remote.getDomains()
        }
        else {
            println "The remote connection is null"
        }
    }

    public setDomain(def domain) {

        if (null == domain) {
            this.domain = null
            this.mBeansPath = []
        }
        else {
            if (this.remote) {

                this.domainList = remote.getDomains()

                def domainFound = domainList.findAll { it == domain }

                if (domainFound.size() == 1) {
                    this.domain = domain
                    this.mBeansPath = []
                }
                else {
                    println "domain $domain not found"
                }
            }
        }
    }

    public ls() {

        if (!this.remote) {
            println "No remote is set!"
            return
        }

        if (!this.domain) {
            println "No domain is set!"
            return
        }

        def objectName = this.domain + ":"
        def objectName2
        if (mBeansPath.size() > 0) {
            objectName += this.mBeansPath.join(',')  // make sure mBeansPath is a list, otherwise remove the .join() command
            objectName2 = objectName + ","
        }
        else {
            objectName2 = objectName
        }

        def pool = new ObjectName(objectName2 + "*")
        def paths = [:]
        println objectName
        println "-----"

        def qNames = this.remote.queryNames(pool, null)
        try {
            qNames.each { mbean ->
                def p = mbean.toString().split(objectName2)[1].split(',')[0]
                paths[p] = p
            }
            paths.each { p, dummy ->  println "M " + p }
        }
        catch (Exception e) {
            throw new DomainIsNoneException()
        }

        try {
            mbean = this.remote.getMBeanInfo(new ObjectName(objectName))
            for (attr in mbean.getAttributes()) {
                def readable
                def writable
                def value
                def valueStr

                try {
                    value = this.remote.getAttribute(new ObjectName(objectName), attr.getName())
                    valueStr = (String) value
                }
                catch (Exception e) {
                    valueStr = "-- " + attr.getType() + " --"
                }
                if (attr.isReadable()) {
                    readable = "r"
                }
                else {
                    readable = "-"
                }
                if (attr.isWritable()) {
                    writable = "w"
                }
                else {
                    writable = "-"
                }
                println "A " + readable + writable + " " + attr.getName() + " : " + valueStr
            }
        }
        catch (Exception e) {
            // ObjectName not found
        }

        try {
            mbean = this.remote.getMBeanInfo(new ObjectName(objectName))
            for (ops in mbean.getOperations()) {
                def params = []
                for (p in ops.getSignature()) {
                    params.append(p.getType())
                }
                println "O " + ops.getReturnType() + " " + ops.getName() + " ( " + ",".concat(params.join()) + ")"
            }
        }
        catch (Exception e) {
            // ObjectName not found
        }

    }

    public cd(String path) {
        if (this.remote) {
            if (this.domain) {
                if (path == "..") {
                    if (!this.mBeansPath.isEmpty()) {
                        this.mBeansPath.pop()
                    }
                }
                else {
                    for (p in path.split(',')) {
                        this.mBeansPath << p
                    }
                }
            }
        }
    }

    public get(att) {
        if (this.remote) {
            if (this.domain) {
                def objectName = this.domain + ":"
                def readable
                def writable
                def value
                def valueStr
                def attr = null

                if (this.mBeansPath.length > 0) {
                    objectName = objectName + ','.concat(this.mBeansPath.join())
                }

                mbean = this.remote.getMBeanInfo(new ObjectName(objectName))

                for (a in mbean.getAttributes()) {
                    if (a.getName() == att) {
                        attr = a
                        break
                    }
                }
                if (!attr) {
                    throw new MBeanAttributeNotFoundException()
                }
                try {
                    value = this.remote.getAttribute(new ObjectName(objectName), att)
                    valueStr = str(value)
                }
                catch (Exception e) {
                    valueStr = "-- " + attr.getType() + " --"
                }
                if (attr.isReadable()) {
                    readable = "Y"
                }
                else {
                    readable = "N"
                }
                if (attr.isWritable()) {
                    writable = "Y"
                }
                else {
                    writable = "N"
                }
                println "ObjectName : " + objectName
                println "Attribute  : " + attr.getName()
                println "Value      : " + valueStr
                println "isReadable : " + readable
                println "isWritable : " + writable
            }
        }
    }

    public set(att, val) {
        if (this.remote) {
            if (this.domain) {
                def objectName = this.domain + ":"
                def attr

                if (this.mBeansPath.length() > 0) {
                    objectName = objectName + ','.concat(this.mBeansPath.join())
                }

                mbean = this.remote.getMBeanInfo(new ObjectName(objectName))

                attr = null
                for (a in mbean.getAttributes()) {
                    if (a.getName() == att)
                        attr = a
                    break
                }
                if (!attr) {
                    throw new MBeanAttributeNotFoundException()
                }
                if (attr.isWritable()) {
                    def a = new Attribute(att, val)
                    this.remote.setAttribute(new ObjectName(objectName), a)
                }
                else {
                    throw new SetAttributeException()
                }
            }
        }
    }

    public invoke(op, params) {
        if (this.remote) {
            if (this.domain) {
                def objectName = this.domain + ":"
                if (this.mBeansPath.length()) {
                    objectName = objectName + ','.concat(this.mBeansPath.join())
                }

                mbean = this.remote.getMBeanInfo(new ObjectName(objectName))

                def ops = null
                for (o in mbean.getOperations()) {
                    if (o.getName() == op) {
                        ops = o
                        break
                    }
                }
                if (!ops) {
                    throw new OperationNotFoundException()
                }
                def sig = []
                for (s in ops.getSignature()) {
                    sig.append(s.getType())
                }

                this.remote.invoke(new ObjectName(objectName), op, params, sig)
            }
        }
    }

    String pwd() {
        if (this.domain) {
            return this.domain + ":" + this.mBeansPath.join(',')
        }
        null
    }

    boolean isConnected() {
        remote != null
    }
}
