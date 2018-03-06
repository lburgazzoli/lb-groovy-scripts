@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('org.slf4j:slf4j-simple:1.7.25')
@Grab('org.apache.httpcomponents:httpclient:4.5.3')
@Grab('org.springframework:spring-jms:4.3.14.RELEASE')


import org.apache.http.client.utils.URIBuilder;

String base = 'www.google.com'
String path = '//something/'

def uri = new URIBuilder(base);
uri.setScheme("http4")
uri.setPath(uri.path + '/' + path)

println uri.build().normalize().toString()