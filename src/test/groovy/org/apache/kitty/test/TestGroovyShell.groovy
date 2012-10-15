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


package org.apache.kitty.test

import org.apache.kitty.command.Help;

/**
 * @version $Id: TestGroovyShell.groovy 1299311 2012-03-10 23:56:20Z anovarini $
 *
 */
class TestGroovyShell {
	
	def static final PROMPT = "kitty>"
	static InputStreamReader inReader;
	static BufferedReader bReader;
	static commands = ["help", "connect", "disconnect", "exit", "ls", "echo"]

	/**
	 * 
	 */
	public TestGroovyShell() {
		// TODO Auto-generated constructor stub
	}


	static main(args) {
		startShell()
	}
	
	static startShell()
	{
		def input
		this.inReader = new InputStreamReader(System.in)
		this.bReader = new BufferedReader(inReader)
		
		while(!(input.equals("exit")))
		{
			print PROMPT
			input = getUserInput()
			inputHandler(input)
		}
		this.inReader.close()
		this.bReader.close()
		
	}
	
	static inputHandler(String input)
	{
		//print input
		
		Integer index = 0
		
		if(this.commands.contains(input))
		{
			index = commands.indexOf(input)
			//println "The index is" + index
			
			switch(index)
			{
				case 0:
					cmdHelp()
					break;
				case 1:
					cmdConnect()
					break;
				case 2:
					cmdDisconnect()
					break;
				case 3:
					cmdExit()
					break;
				case 4:
					cmdLs()
					break;
				case 5:
					cmdEcho(input)
					break;
				default:
					break;
			}
		}
		else
		{
			println input + " is not a valid command"
		}
		
		
	}
	
	static String getUserInput()
	{
		def userInput
		userInput = bReader.readLine()
		return userInput;
	}
	
	static cmdHelp()
	{
		Help h = new Help()
		println h.toString()
	}
	
	static cmdConnect()
	{
		println "connecting...."
	}
	
	static cmdDisconnect()
	{
		println "disconnecting..."
	}
	
	static cmdExit()
	{
		println "exiting..."
	}
	
	static cmdLs()
	{
		println "listing files and directories..."
	}
	
	static cmdEcho(def input)
	{
		println input
	}
	
}
