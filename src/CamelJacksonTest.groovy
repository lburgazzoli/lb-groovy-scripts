import com.fasterxml.jackson.databind.ObjectMapper

@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.0')
@Grab(group='org.apache.camel', module='camel-jackson', version='2.19.0')


Class<?> type = String.class;
ObjectMapper mapper = new ObjectMapper();
Object result = mapper.readValue("", type);
