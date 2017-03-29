# maz-db
The 'maz-db' is a custom designed person database application for the german MaZ Spiritaner organization.

## Reasons for implementing a person database from scratch
Of course there are many person/membership database applications available on the internet that could be modified and used within a few hours or days. But ...

.. there are many special attributes and interconnections between the model entities which should perfectly fit the needs of the organization. 
... also it was required to run the database without a server and sharing it via cloud storage even if this scenario excludes the possibility of concurrent database modifications (that are no likely to happen here anyways). There will be an option for opening a readonly copy of the database so that multiple people can in fact browse the database at the same time.
... last but not least this project should be fun for me to do and I wanted to learn developing a bigger project (as usual) with JavaFX technologies.

## Code disclosure
The code is yet not very clean, contains less comments about what is happening and there are many redundant lines spread over several classes. I wrote some unit tests in the beginning but stopped writing them and even the old ones may not work anymore. Also if you browse the pom.xml you will see some database encryption passwords lying around that are used while development, so they are pretty much useless.

What I really want to say here ... be polite if you ever inspect or use my dirty code provided here. Friendly feedback, issue reports or pull-requests are welcome! The code will improve over time but I can not give any support to someone else besides the organization itself. 

## Submodules
The project splits into two modules **database** and **database-updater**. The database program is a standalone JavaFX application that uses H2DB together with a lot of Hibernate features like Envers or full-text-search (lucence). The core database file will be encrypted by H2 itself. 

The updater, a second standalone JavaFX application, can pull updates from the release list via Github API. It will peform the update by backing up the old database files, migrating the database schema via liquibase files and running some additional cleanup if necessary.
