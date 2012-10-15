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

package com.kitty.client;

import static org.junit.Assert.*;

import javax.management.MBeanServerConnection
import javax.management.ObjectName
import groovy.mock.interceptor.MockFor
import com.kitty.exceptions.DomainIsNoneException

class ClientTest extends GroovyTestCase {

    public void testShouldCorrectlySetNewDomain() {
        def final mbeanServerConnection = {
            ['a', 'b', 'c']as String[]
        } as MBeanServerConnection
        Client client = new Client()
        client.remote = mbeanServerConnection

        assert client.domain == null
        assert client.mBeansPath == null

        client.setDomain 'a'

        assert client.domain == 'a'
        assert client.mBeansPath == []
    }

    public void testShouldKeepActualDomainWhenSettingWithInvalidValue() throws Exception {
        def final mbeanServerConnection = {
            ['a', 'b', 'c'] as String[]
        } as MBeanServerConnection
        Client client = new Client()
        client.remote = mbeanServerConnection
        client.domain = 'a'
        client.mBeansPath = []

        client.setDomain 'invalid_value'

        assert client.domain == 'a'
    }

    public void testShouldListNamesForDomain() throws Exception {
       Client client = new Client()

       // Mock the remote connection
       def mockForRemote = new MockFor(MBeanServerConnection)
       mockForRemote.ignore.asBoolean {
            true
       }
       mockForRemote.ignore.getDomains {
           ['a_domain']
       }

       // Prepare the names list
       mockForRemote.ignore.queryNames { a,b ->
           def set = new HashSet()
           set.add(new ObjectName("a_domain", "test1", "1"))
           set.add(new ObjectName("a_domain", "test2", "2"))
           set.add(new ObjectName("a_domain", "test3", "3"))
           return set
       }

       def theProxyInstance = mockForRemote.proxyInstance()

       // Verify "ls" is working
       mockForRemote.use {
           client.remote = theProxyInstance
           client.setDomain('a_domain')
           try {
             client.ls()
           } catch(DomainIsNoneException dine) {
             fail("DomainIsNoneException thrown")
           }
       }
    }

    public void testShouldChangeDirectoryToParent() throws Exception {

        def client = new Client()
        def final mbeanServerConnection = {
            ['a', 'b', 'c'] as String[]
        } as MBeanServerConnection
        client.remote = mbeanServerConnection
        client.domain = 'a'

        client.mBeansPath = ["a" , "b" , "c"]

        client.cd ".."

        assertEquals (["a" , "b"] , client.mBeansPath)
    }
}
