import static groovy.io.FileType.FILES

def root = new File('.')
root.traverse(type: FILES, nameFilter: ~/log4j.properties/) {
    println "Processing: ${it}"

    def log4j2 = new LinkedHashMap<String,String>()
    def log4j1 = new Properties()

    it.withInputStream {
        log4j1.load(it)
    }

    log4j1.stringPropertyNames().sort().each { key ->

        if (key == 'log4j.rootLogger') {
            String[] items =log4j1[key].split(',');
            log4j2['rootLogger.level'] =  items[0].trim()

            (1..<items.length).each {
                String appenderRef = items[it].trim();
                log4j2["rootLogger.appenderRef.${appenderRef}.ref"] = appenderRef
            }
        } else if (key.startsWith('log4j.logger')) {
            String loggerName = key.substring('log4j.logger'.length() + 1)
            String loggerId = loggerName.substring(loggerName.lastIndexOf('.') + 1)
            String[] items = log4j1[key].split(',');

            log4j2["logger.${loggerId}.name"] = loggerName
            log4j2["logger.${loggerId}.level"] = items[0].trim()

            (1..<items.length).each {
                String appenderRef = items[it].trim();
                log4j2["logger.${loggerId}.appenderRef.${appenderRef}.ref"] = appenderRef
            }
        } else if (key.startsWith('log4j.appender')) {
            def matcher = (key =~ /^log4j\.appender\.([\w][\w]*)/)
            if (matcher.matches()) {
                def appenderName = matcher.group(1);
                switch (log4j1[key]) {
                    case 'org.apache.log4j.ConsoleAppender':
                        log4j2["appender.${appenderName}.type"] = 'Console'
                        log4j2["appender.${appenderName}.name"] = appenderName
                        log4j2["appender.${appenderName}.layout.type"] = 'PatternLayout'
                        log4j2["appender.${appenderName}.layout.pattern"] = log4j1["${key}.layout.ConversionPattern"]
                        break
                    case 'org.apache.log4j.FileAppender':
                        log4j2["appender.${appenderName}.type"] = 'File'
                        log4j2["appender.${appenderName}.name"] = appenderName
                        log4j2["appender.${appenderName}.fileName"] = log4j1["${key}.file"]
                        log4j2["appender.${appenderName}.layout.type"] = 'PatternLayout'
                        log4j2["appender.${appenderName}.layout.pattern"] = log4j1["${key}.layout.ConversionPattern"]
                        break
                }
            }
        }
    }

    def f = new File(it.getParent(), "log4j2.properties")
    println "Writing: ${f}"

    f.withWriter('utf-8') { writer ->
        writer.write '''
            ## ---------------------------------------------------------------------------
            ## Licensed to the Apache Software Foundation (ASF) under one or more
            ## contributor license agreements.  See the NOTICE file distributed with
            ## this work for additional information regarding copyright ownership.
            ## The ASF licenses this file to You under the Apache License, Version 2.0
            ## (the "License"); you may not use this file except in compliance with
            ## the License.  You may obtain a copy of the License at
            ##
            ## http://www.apache.org/licenses/LICENSE-2.0
            ##
            ## Unless required by applicable law or agreed to in writing, software
            ## distributed under the License is distributed on an "AS IS" BASIS,
            ## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
            ## See the License for the specific language governing permissions and
            ## limitations under the License.
            ## ---------------------------------------------------------------------------
        '''.stripIndent().trim()

        writer.writeLine "\n"
        log4j2.each {
            k, v -> writer.writeLine "${k} = ${v}"
        }
    }
}