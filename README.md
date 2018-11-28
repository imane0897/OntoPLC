OntoPLC
=======

A Protégé Desktop plugin for importing PLC XML data into OWL ontologies. This project is based on [Cellfie](https://github.com/protegeproject/cellfie-plugin).

### Building and Installing

To build and install this plugin you must have the following items installed:

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- A tool for checking out a [Git](http://git-scm.com/) repository
- Apache's [Maven](http://maven.apache.org/index.html)
- A Protégé (5.0.0 Beta 21 or higher) distribution. Download [here](http://protege.stanford.edu/products.php#desktop-protege).

Get a copy of the latest code:

```
git clone https://github.com/imane0897/OntoPLC.git
```

Change into the cellfie-plugin directory:

```
cd OntoPLC
```

Build with Maven:

```
mvn clean package  
```

On build completion the ```target``` directory will contain a OntoPLC-${version}.jar file. 
The JAR is generated in the OSGi bundle format required by Protégé's plugin-in mechanism.

To install in your local Protégé, copy this JAR file to the ```plugins``` subdirectory of your Protégé installation (e.g., /Applications/Protege-5.0.0/Contents/Java/plugins/).  

User's Guide
------------

To start working with Cellfie, first load your working ontology in Protégé and find the plugin in menu selection **Tools > OntoPLC**
