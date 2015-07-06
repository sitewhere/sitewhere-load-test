
![SiteWhere] (http://www.sitewhere.org/sites/default/files/sitewhere.png)

The Open Platform for the Internet of Things™
-----------------------------------------------

# SiteWhere Load Testing Node
A standalone node that can be configured to load test SiteWhere Server by sending
realistic data streams that are ingested by the system. When deployed in large 
groups, the nodes can be used to stress test the system to find bottlenecks and
capture performance data.

### Building from Source
If you want to customize SiteWhere Load Testing Node or otherwise have a need to build it 
from source code, use the following steps.

**Note that this project has dependencies on SiteWhere 1.0.5 which is an unreleased
branch. Before building the load test node project, clone the 1.0.5 branch of 
SiteWhere Server and build it to generate artifacts into your local Maven repository.**

#### Required Tools #####
* [Apache Maven] (http://maven.apache.org/)
* A [GIT] (http://git-scm.com/) client

#### Clone and Build #####
Clone this repository locally using:

    git clone https://github.com/sitewhere/sitewhere-load-test.git
    
Navigate to the newly created directory and execute:

    mvn clean install

After the build completes, a file named **sitewhere-loadtest.war** will have been created in the **deploy** 
folder.

#### Building a Full Server #####
Once the **sitewhere-loadtest.war** file has been generated, you can create the full server distribution by using:

	mvn -P builderServer install
	
This will download a copy of Tomcat, copy the WAR to the webapps folder, and copy the default 
configuration files to the correct location. Two archives are generated; a zip archive and a 
tarred gzipped archive. To install the archive, move it to the machine where the load test node
should run, unzip the archive then execute either *bin/startup.bat* or *bin/startup.sh* to
start the node.

### Configuring the Load Testing Node ####
The configuration file for the load testing node is located at *conf/loadtest/sitewhere-loadtest.xml*. It is
an XML file that conforms to the Spring beans XML schema and contains custom schema elements used to
configure how load testing is performed. An example of a configuration file is shown below:

 ```XML
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context" xmlns:lt="http://www.sitewhere.com/schema/sitewhere/loadtest"
	xsi:schemaLocation="
           http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd
           http://www.sitewhere.com/schema/sitewhere/loadtest http://www.sitewhere.org/schema/sitewhere/loadtest/1.0.5/sitewhere-loadtest.xsd?a=a3">
           
	<!-- Load property values for substitution -->
	<context:property-placeholder location="file:${catalina.home}/conf/loadtest/loadtest.properties"
		ignore-resource-not-found="true"/>

	<!-- Defines load testing configuration -->
	<lt:load-test>

		<!-- SiteWhere connection information -->
		<lt:connection>

			<lt:sitewhere-server restApiUrl="http://localhost:8080/sitewhere/api/"
				restApiUsername="admin" restApiPassword="password"/>

		</lt:connection>

		<!-- List of load test agents -->
		<lt:agents>
	
			<!-- Generates traffic via an MQTT connection -->
			<lt:mqtt-agent agentId="mqtt" numThreads="20" hostname="localhost" port="1883"
				topic="SiteWhere/input/protobuf">
			
				<!-- Pool of devices to generate events from -->
				<lt:device-pool poolSize="25"/>

				<!-- Produces events at a linear rate with a throttle delay -->
				<lt:linear-event-producer throttleDelayMs="100"/>

				<!-- Produces events at an increasing rate over the duration of an hour -->
				<!--  
				<lt:progressive-event-producer initialThrottleDelayMs="500"
					finalThrottleDelayMs="10" durationInSec="3600"/>
				-->
		
				<!-- Encodes events in SiteWhere Google Protocol Buffer format -->
				<lt:protobuf-encoder/>

			</lt:mqtt-agent>

		</lt:agents>

	</lt:load-test>

</beans>
```

This configuration will start an MQTT agent that uses 20 threads to submit event traffic to
a SiteWhere server over the MQTT protocol. The events will be sent to a pool of 25 devices which
are loaded from the SiteWhere server via the REST services. Events will be produced in a linear
fashion with a delay of 100 milliseconds to throttle the transmission rate. Each event is encoded using the default 
[SiteWhere Google Protocol Buffers](https://github.com/sitewhere/sitewhere/blob/sitewhere-1.0.5/sitewhere-protobuf/proto/sitewhere.proto) 
format. 

**Note that your SiteWhere server should be configured to accept events in this same format.
The default SiteWhere server configuration will work with this load testing configuration.**

#### Agents #####
A SiteWhere load test node configuration may contain one or more **agents**. An agent is used
to send data to a SiteWhere server over a given protocol. The initial version of SiteWhere load
test node only supports sending events over MQTT. Future versions will add agents for Stomp, COAP,
REST and other formats.




