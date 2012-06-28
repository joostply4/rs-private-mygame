@echo off
"C:\Program Files\Java\jdk1.7.0_03/bin/javac" -cp lib/*; -d bin -sourcepath src src/client.java
"C:\Program Files\Java\jdk1.7.0_03/bin/javac" -cp lib/*; -d bin -sourcepath src src/Loader.java
@pause