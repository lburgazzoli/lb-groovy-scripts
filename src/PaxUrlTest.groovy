import org.ops4j.pax.url.mvn.internal.Parser

@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.ops4j.pax.url', module='pax-url-aether', version='2.5.2')

def mvnurl = 'mvn:org.ops4j.pax.url/pax-url-aether/2.5.2'
def parser = new Parser(mvnurl)
def group  = parser.group - ~/^mvn:/

println group
