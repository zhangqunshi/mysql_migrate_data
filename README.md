# mysql_migrate_data
migrate data from one mysql to another mysql database.

How to use: <br>
$ javac Run.java <br>
$ java Run <br>

<p>
I want this tools to keep simple, only depends on mysql-driver by JDBC, no other dependence. You can easy to understand the code, and make your own version base on this.
Feature: <br>
<ul>
<li>Never delete any data, except you call deleteData method.</li>
<li>If destination database has data already, then the data can be merge. (use update SQL)</li>
<li>You can define a column data handler to change the column value of a table during the migrating.</li>
</ul>
</p>
