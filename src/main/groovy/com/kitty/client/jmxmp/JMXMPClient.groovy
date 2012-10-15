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


package com.kitty.client.jmxmp

import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import com.kitty.client.Client


class JMXMPClient extends Client {

	@Override
	void connect(def host, def port) {

		if (this.remote != null) {
			disconnect()
		}

		try {
			def serviceURL = "service:jmx:jmxmp://$host:$port"
			this.url = new JMXServiceURL(serviceURL)
			this.connector = JMXConnectorFactory.connect(this.url)
			this.remote = this.connector.getMBeanServerConnection()
		}
		catch(IOException e) {
			this.connector = null
			this.remote = null
		}
		finally {
			if(this.remote != null) {
				this.host = host
				this.port = port
			}
		}
	}
}
