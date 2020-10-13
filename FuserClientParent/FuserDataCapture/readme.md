# Overview
---

The Fuser Data Capture is used to capturing MatmFlight messages.  All data is
received using the fuser-client-api.  Data may be stored in a variey of different
formats.  Supported formats include XML, CSV, or Database inserts.

## Execution
---

Scripts are available for starting the Fuser Data Capture.  The scripts expect
to receive a property indicating which environment properties to load.  The set
of environment properties can be found in the /env directory.

1. Linux: runFuserDataCapture [env property file]
2. Windows: runFuserDataCapture.bat [env property file]


## Configuration
---

The Fuser Data Capture has a set of core configurations that are used for
controlling the behavior of the fuser-client-api and behavior of the capture
protocols.

* **fuser-capture.sync.active** - Toggles whether message processing should be executed after program is initialized
* **fuser-client-api.jms.url** - Reference the JMS broker for fuser message
* **fuser-client-api.service.sync.url** - Reference to the fuser sync service url
* **fuser-client-api.service.sync.enabled** -Toggles whether flights should be synced from fuser sync service
* **fuser-client-api.service.sync.aircraft.enabled** -Toggles whether aircraft should be synced from fuser sync service
* **fuser-client-api.remover.window.hours** - Time window before received flights are removed
* **fuser-client-api.remover.timed.active** - Toggles whether flight removal is active
* **fuser-client-api.uri.flightType.source** - Source JMS endpoint for all fuser messages
* **fuser-client-api.uri.matmTransferEnvelope.source** - Source JMS endpoint for all fuser envelopes
* **fuser-client-api.uri.matmFlight.source** - Source JMS endpoint for raw fuser data
* **fuser-client-api.clocksync.active** - Toggles whether to use feeder time for fuser message
* **fuser-client-api.airportsOfInterest** - Comma separted liste of allowable airports (i.e. CLT,ATL)
* **fuser-client-api.filter.airport.active** - Toggles whether airport filtering will occur on the base fuser message
* **fuser-client-api.filter.asdex.airport.active** - Toggles whether airport filtering will occur on the surface airport of the base fuser message
* **fuser-capture.position.airportsOfInterest** - Comma separted liste of allowable airports (i.e. CLT,ATL)
* **fuser-capture.position.jms.url** - Reference the JMS broker for position message
* **fuser-capture.position.filter.airport.active** - Toggles whether airport filtering will occur on the base position message
* **fuser-capture.position.clocksync.enabled** -Toggles whether to use feeder time for position messages
* **fuser-capture.capture.database.position.enabled** - Toggles optional flow for capturing position message

### XML Configuration
---

Optional properties specific to the behavior of the XML protocol

* **fuser-capture.capture.path** - Directory path where files will be written
* **fuser-capture.capture.archive.path** - Director path where files will be archived
* **fuser-capture.capture.roll.period** - Frequency of which a file will roll
* **fuser-capture.capture.archive.threaded** - Toggles whether compression and archiving occurs on a separate thread
* **fuser-capture.capture.xml.enabled** - Toggles whether XML file capturing is enabled
* **fuser-capture.capture.xml.all.enabled** - Toggles whether XML file capturing for full state data is enabled
* **fuser-capture.capture.xml.file** - File name format of captured data
* **fuser-capture.capture.xml.all.file** - File name format of full state captured data

### CSV Configuration
---

Optional properties specific to the behavior of the CSV protocol.  The fuser
data is broken into several different files, these properties control the 
behaviors of the individual files.

* **fuser-capture.capture.csv.enabled** - Toggles whether CSV data capture is enabled
* **fuser-capture.capture.csv.all.enabled** - Toggles whether CSV full state data capture is enabled
* **fuser-capture.capture.path** - Directory path where files will be written
* **fuser-capture.capture.archive.path** - Director path where files will be archived
* **fuser-capture.capture.roll.period** - Frequency of which a file will roll
* **fuser-capture.capture.archive.threaded** - Toggles whether compression and archiving occurs on a separate thread  
* **fuser-capture.matmFlight.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.matmFlight.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.matmFlight.file** - File name format of captured data
* **fuser-capture.matmFlight.all.file** - File name format of full state captured data
* **fuser-capture.ext.adsb.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.adsb.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.adsb.file** - File name format of captured data
* **fuser-capture.ext.adsb.all.file** - File name format of full state captured data
* **fuser-capture.ext.asdex.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.asdex.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.asdex.file=** - File name format of captured data
* **fuser-capture.ext.asdex.all.file=** - File name format of full state captured data
* **fuser-capture.ext.cat11.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.cat11.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.cat11.file** - File name format of captured data
* **fuser-capture.ext.cat11.all.file** - File name format of full state captured data
* **fuser-capture.ext.cat62.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.cat62.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.cat62.file** - File name format of captured data
* **fuser-capture.ext.cat62.all.file** - File name format of full state captured data
* **fuser-capture.ext.derived.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.derived.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.derived.all.file** - File name format of full state captured data
* **fuser-capture.ext.derived.file** - File name format of captured data
* **fuser-capture.ext.tbfm.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.tbfm.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.tbfm.all.file** - File name format of full state captured data
* **fuser-capture.ext.tbfm.file** - File name format of captured data
* **fuser-capture.ext.tfm.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.tfm.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.tfm.file** - File name format of captured data
* **fuser-capture.ext.tfm.all.file** - File name format of full state captured data
* **fuser-capture.ext.tfmtfdm.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.tfmtfdm.header.enabled** - Toggles if CSV header files are written as part of the capture
* **fuser-capture.ext.tfmtfdm.file** - File name format of captured data
* **fuser-capture.ext.tfmtfdm.all.file=tfm-tfdm-extension** - File name format of full state captured data
* **fuser-capture.ext.matm-airline-message.all.file** - File name format of full state captured data
* **fuser-capture.ext.matm-airline-message.file** - File name format of captured data
* **fuser-capture.ext.matm-airline-message.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.matm-airline-message.header.enabled** - Toggles if CSV header files are written as part of the capture

* **fuser-capture.ext.idac.all.file=idac-extension** - File name format of full state captured data
* **fuser-capture.ext.idac.file** - File name format of captured data
* **fuser-capture.ext.idac.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.idac.header.enabled** - Toggles if CSV header files are written as part of the capture

* **fuser-capture.ext.surface-model.all.file=surface-model-extension** - File name format of full state captured data
* **fuser-capture.ext.surface-model.file** - File name format of captured data
* **fuser-capture.ext.surface-model.enabled** - Toggles whether CSV file capturing is enabled
* **fuser-capture.ext.surface-model.header.enabled** - Toggles if CSV header files are written as part of the capture

### Database Configuration
---

Optional properties specific to the behavior of the DATABASE protocol.

* **fuser-capture.capture.database.enabled** - Toggles whether the database capturing is enabled
* **fuser-capture.capture.database.all.enabled** - Toggles whether the full state database capturing is enabled
* **fuser-capture.capture.database.useDefaultBatchTime** - Toggles whether the system time should be used as the batch time 
       for messages with an invalid timestamp
* **fuser-capture.database.createTables** - Toggles if database tables are created when the application starts
* **fuser-capture.database.dropTables** - Toggles if database tables are dropped when the application starts
* **fuser-capture.database.driver** - JDBC driver
* **fuser-capture.database.url** - URL to the database
* **fuser-capture.database.username** - Database username
* **fuser-capture.database.password** - Database password
* **fuser-capture.database.daysToKeep** - Number of days that the database will store information
* **fuser-capture.database.batch.directory** - Directory to store batch files for bulk inserts
* **fuser-capture.database.batch.all.directory** - Directory to store full state batch files for bulk inserts
* **fuser-capture.database.batch.report.enabled** - Toggles whether the batch thread reports the current batch size
* **fuser-capture.database.batch.interval** - Interval at which the batch thread will report the current batch size
* **fuser-capture.database.mybatis.configuration** - File reference to the MyBatis configuration

## How to embed FuserDataCapture into other program using FuserDataCaptureApi?
---

1. create an instance of FuserDataCaptureConfiguration object and set desired configurable properties. (see class for all configurable properties)
2. create an instance of FuserDataCaptureLoaderImpl object and set the configuration using the object from 1.
3. run load() method from 2. and it will initialize all program properties and instances.
4. run getFuserDataCaptureApi() method from 2. and it will return an reference to the FuserDataCaptureApi interface.
5. from the api interface, run start() method and it will start all initialized camel routings.
