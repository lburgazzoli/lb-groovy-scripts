@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='com.squareup.retrofit2', module='retrofit', version='2.1.0')
@Grab(group='com.squareup.retrofit2', module='converter-jackson', version='2.1.0')
@Grab(group='com.squareup.okhttp3', module='logging-interceptor', version='3.3.0')

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

def userName = System.getenv('jboss-fuse-jenkins')
def password = System.getenv('8xdLM9i4bxsgetdwyhJxVedAk-Kve7tk')
def baseUrl  = System.getenv('SERVICENOW_API_URL') + '/'

interface ImportSet {
    @Headers([ 'Accept: application/json', 'Produces: application/json' ])
    @POST('/api/now/import/{table}')
    Call<Map<String, Object>> create(@Path('table') String table, @Body Map<String, Object> incident)
}

def client = new Retrofit.Builder()
    .addConverterFactory(JacksonConverterFactory.create())
    .client(new OkHttpClient.Builder()
        .authenticator({
            route, response -> response.request().newBuilder()
                .header("Authorization", Credentials.basic(userName, password))
                .build()
        })
        .build())
    .baseUrl(baseUrl)
    .build()
    .create(ImportSet.class)

def result = client.create('u_imp_incident', [
    short_description: 'Test incident',
    impact: 1,
    contact_type: 'email'
])

def response = result.execute()
if (response.isSuccessful()) {
    println response.body()
} else {
    println response.errorBody().string()
}