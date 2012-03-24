# Introduction

Atreus is a high-level client for the [Apache Cassandra](http://cassandra.apache.org/ "Apache Cassandra") NoSQL database. The goal of Atreus is to provide a client with an intuitive API that reduces the difficulty of adopting Cassandra. 

Atreus is still under development and should be considered _alpha_ at the moment.

## Features

Atreus has the following features:

* Uses a session-based abstract layer similar to ORM tools such as Hibernate
* Type converters to handle serialization of Objects and primitives to and from Cassandra
* Configurable to perform writes in batch or immediately against a Cassandra Cluster
* Configurable to read rows and columns eagerly or lazily
* Configurable to perform session-level caching of reads and writes
* Internally managed connection pooling, load balancing and failover
* Spring Framework integration similar to ORM tools such as Hibernate (i.e. transaction aware)

## Requirements

Atreus is designed to work with the JDK 1.6 or higher.

# Quick Start

The following example demonstrates the powerful simplicity of Atreus.

	// Create an AtreusSessionFactory and connect to a Cassandra cluster
	AtreusConfiguration config = new AtreusConfiguration("localhost", 9171, "MyKeyspace");
	AtreusSessionFactory factory = AtreusSessionFactoryBuilder.buildFactory(config);
	
	// Start an AtreusSession
	AtreusSession session = factory.openSession();
	
	// Write some data
	session.setFamilyAndKey("MyColFamily", "MyRowKey1");
	session.writeColumn("MyColumn1", 12345);
	session.writeColumn("MyColumn2", "foobar");
	session.writeColumn("MyColumn3", new Date());
	
	// Write some data to a different Column Family same row key
	session.setColumnFamily("MySuperColFamily");
	session.setRowKey("MyRowKey2");
	session.writeColumn("MyColumn1", "MySubColumn1", 12345);
	session.writeColumn("MyColumn1", "MySubColumn2", "foobar");
	session.writeColumn("MyColumn1", "MySubColumn3", new Date());
	
	// Flush write data to the Cassandra cluster
	session.flush();
	
	// Read some data
	session.setColumnFamily("MyColFamily");
	session.setRowKey("MyRowKey2");
	int myColumn1 = session.readColumn("MyColumn1", Integer.class);
	String myColumn2 = session.readColumn("MyColumn2", String.class);
	Date myColumn3 = session.readColumn("MyColumn3", Date.class);
	
	// Read some data with sub columns
	session.setFamilyAndKey("MySuperColumnFamily", "MyRowKey2");
	int mySubColumn1 = session.readColumn("MyColumn1", "MySubColumn1", Integer.class);
	String mySubColumn2 = session.readColumn("MyColumn1", "MySubColumn2", String.class);
	Date mySubColumn3 = session.readColumn("MyColumn1", "MySubColumn3", Date.class);
	
	// Another way to read data using the AtreusColumnMap class
	session.setFamilyAndKey("MyColFamily", "MyRowKey1");
	AtreusColumnMap map = session.readColumns();
	myColumn1 = map.get("MyColumn1", Integer.class);
	myColumn2 = map.get("MyColumn2", String.class);
	myColumn3 = map.get("MyColumn3", Date.class);
	
	// Close the session
	session.close();
	