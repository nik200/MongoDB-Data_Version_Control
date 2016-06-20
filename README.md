# MongoDB Data Version Control - MongoVC

This project is aimed at creating a Version Control System using most widely used noSQL dataBase, MongoDB. MongoDB gaurantees atomicity only at document level, i.e. a command which updates multiple documents may not gaurantee atomicity accross the dataset. To implement version control system and to enforce atomicity over a set of transactions, MongoVC server is created which provides restricted access to MondoDB and synchronizes checkin/checkout of documents. MongoVC client provides simple API's to checkin/checkout documents from MongoDB via MongoVC Server.

## Features

1. Provides simple client API to checkin/checkout documents. Documents to be checked out can be specified with the query syntax identical to the mongodb read operation query syntax.
2. Maintains old documents in a seperate archive collection with actual collection storing only the latest version.
3. Creates new version of document only if the document is changed. Checking in the unchanged document does not creates an archive copy, thus saving memory.
4. Version management metadata hidden from client application. MongoVC ensures document structure is not altered.  
5. MongoVC server provides notification service to notify all registered clients whenever any registered user performs check-in.
6. Read API to query on old state/version of MongoDB collection. 

## Use case

MongoVC can be used when versions of a dataset in a collection needs to be maintained. Client application can query (only `find` query) on old version of dataset in a mongoDB collection. It provides concurrancy control for shared database.
Eg. For a simple Blog collection MongoVC can be used to allow bloggers to update their blogs while still maintaining to old versions for future references.

## MongoVC Client APIs

### Basic version control operations

Class `com.plantiss.mongoadapter.client.QueryHandler` provides API to perform all version control operations. Client application needs to intantiate QueryHandler with correct collection name and MongoVC server connection details. QueryHandler implements following operations - 

1. Checkout current version of documents from the collection. 
2. Checkin modified documents and remove documents.
3. Find latest documents from collection.
4. Find old versions documents by querying on any previous state of collection.
5. Get checkin history of a collection.
6. Get checkin history of a particular document in collection.

### Notification service

Client application can subscribe to MongoVC notification service to get notifications of the latest checkins performed in the db by other users. Application needs to implement `com.plantiss.mongoadapter.objects.NotificationReciever` interface and provide implementation to `notify(Notification)` method. This method will be invoked by MongoVC client when notification is recieved.

To subscribe for notification service, client applications needs to instantiate `com.plantiss.mongoadapter.client.NotificationListener` class and invoke it's register(NotificationReciever) method.

## Setting up MongoVC Server

MongoVC server needs to be setup and started. A mongoVC service instance manages data version in all the collections of a single db. Class `com.plantiss.mongoadapter.server.RequestHandler` needs to be executed to start the server. Following JRE varaibles can be specified during th startup - 

- `mongovc.service.port` - port on which MongoVC server needs to be started. Default value - 25000
- `mongovc.db.name` - MongoDB database name on which version control needs to be implemented. Default - repo
- `mongovc.db.port` - MongoDB port. Default - 27017
- `mongovc.db.host` - MongoDB host. Default - localhost
  
## Constraints

Documents in version controlled collection need to have string type `_id` field as unique identifier of the mongoDB document. Also below fields are reserved for storing version metadata - `version`, `chkoutBy`, `chckoutTime`. These fields must not be used in the documents.

## Future scope

1. Support for aggregation on old version of dataset in collection
2. Support for userID authentication.
