# Project content
This is an example for a Java 8 delegation experiment using interfaces, classes, JPA and JAXB.
This project is also referenced from the stackoverflow question:
- http://stackoverflow.com/questions/26816650/java8-collections-sort-sometimes-does-not-sort-jpa-returned-lists

#What you need to compile/reproduce
- maven 3
- jdk 7 / jdk 8 (to show the differences)
- mysql

## preparation of mysql database
    create database testsqlstorage;
    grant all privileges on testsqlstorage to cm@localhost identified by 'secret';
    
#Model
Basically a 1:n relationship between Folder and Document is modelled for JAXB and JPA usage:

##class Folder 
- String name
- <List> Document documents

##class Document
- String name
- Folder parentFolder
   
##Tables
   
###Document
    +-------------------+--------------+------+-----+---------+-------+
    | Field             | Type         | Null | Key | Default | Extra |
    +-------------------+--------------+------+-----+---------+-------+
    | NAME              | varchar(255) | NO   | PRI | NULL    |       |
    | PARENTFOLDER_name | varchar(255) | YES  | MUL | NULL    |       |
    +-------------------+--------------+------+-----+---------+-------+
### Folder
    +-------+--------------+------+-----+---------+-------+
    | Field | Type         | Null | Key | Default | Extra |
    +-------+--------------+------+-----+---------+-------+
    | name  | varchar(255) | NO   | PRI | NULL    |       |
    +-------+--------------+------+-----+---------+-------+
   
## Testcases
- TestJPASorting.java in package com.bitplan.java8sorting has the simple test case for the Stackoverflow question
- TestEclipseLinkSortingWithDelegation.java holds the testcase for the delegation version of the problem

   
   
   
   