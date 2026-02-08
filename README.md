# Readme

Octane Crawler is a Java based web crawler. Octane Crawler follows polite web crawler rules. 
The goal of this project is to research content on the web.

## Quick start for octane crawler

 1. Run the database setup routines in docs/DATABASE.txt
 2. Launch the program org.berlin.crawl.OctaneCrawlMain


## README for twitter data collection

Note: OpenNLP natural language processing data from:

http://opennlp.sourceforge.net/models-1.5/

Also see:
http://opennlp.apache.org/documentation/1.5.2-incubating/manual/opennlp.html#tools.sentdetect

Resources:
http://norvig.com/spell-correct.html

----

Use this query to get a count per user:

select screen_name, count(message) as "Number" from bot_data_messages group by screen_name order by Number;


## Author and Contact

Berlin Brown (berlin dot brown at gmail.com)
http://berlin2research.com/

########################################
 License 
########################################

 Copyright (c) 2013 Berlin Brown (berlin2research.com)
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at 
      http://www.apache.org/licenses/LICENSE-2.0 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 
## Libraries used

commons-io-2.1.jar, commons-lang3-3.1.jar, commons-logging-1.1.1.jar, derby-10.8.3.0.jar, 
dom4j-1.6.1.jar, hibernate-core-4.0.1.Final.jar, hsqldb-1.8.0.10.jar, httpclient-4.2.3.jar, jackson-core-asl-1.9.12.jar, 
log4j-1.2.16.jar, mysql-connector-java-5.1.23.jar, opennlp-maxent-3.0.2-incubating.jar
opennlp-tools-1.5.2-incubating.jar, spring-core-3.1.1.RELEASE.jar, spring-web-3.1.1.RELEASE.jar, 
struts-core-1.3.10.jar, tagsoup-1.2.1.jar, tika-core-1.3.jar