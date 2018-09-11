# WMRGenerator
Tool for generating random map rotaitions for World of Padman.
### Usage:
`java -jar WMRGenerator.jar [OPTION]...`

|short| full option|attribute  | description |
| --- | ---------- | --------- | ----------- |
|-a   | --append   | FILE      | set in- and output file to FILE*
|-f   | --full     |           | print out the full resulting map sequence, from the beginning to the end of the longest rotation over all game types |
|-h   | --help     |           | give this help |
|-i   | --in       | FILE      | set input file to FILE*|
|-k   | --seedkey  | SEED      | parse a random seed from SEED|
|-l   | --length   |           | print out the length of the sequence until the first map appears for the second time|
|-m   | --mode     | MODE      | set the mode for generating rotation: <0 - use all available maps, but only for one game type each, 0 - use all available maps for each game type (default), >0 - set the fix length for the rotation of each game type resulting in the same sequence if the rotation starts over|
|-o   | --out      | FILE      | set output file to FILE*|
|-s   | --separate |           | print out the rotation separated by game types|
|-t   | --types    |           | print out the order of game types|

*If an input file is set WMRGenerator will read the input, append the rotation and write to
 the output file. Therefore setting input and output file separately to the same file is the 
 same as using --append. If no output file is set WMRGenerator will neither read nor write
 any file.


Alternatively all options can be set by editing "wmrgconfig.json" and running `java -jar WMRGenerator.jar`. If the config file does not exists, WMRGenerator will create it using default values.
### License
[Apache License](http://www.apache.org/licenses/LICENSE-2.0). The release uses [json-simple](https://github.com/fangyidong/json-simple) which is also licensed under Apache License.
