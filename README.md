# jatha: a LISP Library in Java

## Overview
Jatha is a Java library that implements a large subset 
of Common LISP, including most of the datatypes 
(e.g. packages, bignums).  The API allows access to 
LISP from Java.  Jatha is useful as a fast, embedded 
LISP language, as a set of dynamically-typed data types,
or as a standalone LISP.

Jatha is governed by the Gnu LGPL 3.0 license.  Please submit
any modifications, bugs, and pull requests to the GitHub site.

See:  https://github.com/mhewett/jatha

## Compile 
Run 'ant' in the top-level Jatha directory.
You will need to add the jar files from
jatha/lib/ to your classpath in order to
compile the code.

## Deploy    
Run 'ant jar' in the top-level Jatha directory
to create jatha/build/lib/jatha.jar 
Copy it to your desired location.

## Run
java -jar build/lib/jatha.jar

This version will run in JDK 1.4 through JDK 1.8

## Example
```lisp
(defun factorial (n)
    (if (> n 1)
        (* n (factorial (1- n)))
        ;; else
        1)
    )
    
(factorial 10)

(factorial 100)
```


Micheal Hewett
