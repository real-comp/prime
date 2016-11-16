# Prime
A Java library and suite of command-line tools that facilitate data transformations.


This readme serves as a brief introduction to Prime and its high-level features.  You are encouraged
to consult the wiki for more complete documentation.  (TODO)

There are many tools for parsing and manipulating data from files.  If you simply need to extract the 5th and 10th
fields from a CSV file, Prime is likely _not_ the best tool.  However, if you need to read values from a file that
arrives every week and has undergone 3 layout changes in the past year, then Prime is a very good choice.


## Data Types

Prime supports the following data types:

* String
* Float
* Integer
* Double
* Long
* Boolean
* List
* Map


## Record

A Record is the internal data model for Prime and is a fancy Java Map<String,Object>.  Values in Records are retrieved by
_name_ and the type of the stored values can be coerced.

Here is an example of a Record with two String fields.  Notice the age field is being coerced from a String to an Integer.
```java
Record record = new Record();
record.put("name", "Bob");
record.put("age", "21");
assertEquals(21, record.getInt("age"));
```

Using internal Lists and Maps, a Record can represent a relational data model.  Here we have a simple Map of address
information that is added to a Record.  Using the _fieldname.fieldname_ convention, values can be resolved from deep
within a Record.

```java
Map<String,String> address = new HashMap<>();
address.put("street", "13284 Pond Springs Rd Ste 101");
address.put("city", "Austin");
address.put("state", "TX");

Record record = new Record();
record.put("name, "Bob");
record.put("location", address);
assertEquals("Austin", record.get("location.city");

```



## Schema


## Converters


## Validators