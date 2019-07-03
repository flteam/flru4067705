package flteam.flru4067705.parser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import flteam.flru4067705.exception.CloudFlareBlockException;
import flteam.flru4067705.exception.PreconditionFailedException;
import flteam.flru4067705.model.*;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.cookie.SM.COOKIE;

public class Parser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private static final String URL_FOR_USERS = "https://www.artstation.com/api/v2/search/users.json";
    private static final String PUBLIC_CSRF_TOKEN_HEADER_NAME = "public-csrf-token";
    public static final long TIMEOUT = TimeUnit.SECONDS.toMillis(11);

    private String cookieValue;
    private String publicCsrfTokenValue;
    private HttpHost proxyHost;

    public Parser(String cookieValue, String publicCsrfTokenValue, HttpHost proxyHost) {
        this.cookieValue = cookieValue;
        this.publicCsrfTokenValue = publicCsrfTokenValue;
        this.proxyHost = proxyHost;
    }

    public Parser(String cookieValue, String publicCsrfTokenValue) {
        this.cookieValue = cookieValue;
        this.publicCsrfTokenValue = publicCsrfTokenValue;
    }

    public Set<Profile> searchAllByCountryAndSkills(Country country, Set<Skill> skills) throws CloudFlareBlockException {
        Set<Profile> profiles = new HashSet<>();
        for (Skill skill : skills) {
            SearchBody searchBody = new SearchBody(1, Filter.buildCountryAndSkillFilters(country, skill));
            profiles.addAll(search(searchBody));
        }
        return profiles;
    }

    private Set<Profile> search(SearchBody searchBody) throws CloudFlareBlockException {
        Set<Profile> result = new HashSet<>();
        try {
            UsersResponse usersResponse = doRequest(searchBody);
            int count = usersResponse.totalCount;
            for (int i = 1; i <= count / searchBody.perPage + 1; i++) {
                searchBody.page = i;
                usersResponse = doRequest(searchBody);
                if (usersResponse.totalCount.equals(0)) {
                    break;
                }
                result.addAll(usersResponse.profiles);
                Thread.sleep(TIMEOUT);
            }
        } catch (CloudFlareBlockException e) {
            throw e;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(searchBody);
        }
        return result;
    }

    public Set<Profile> searchAllByCountry(Country country) throws CloudFlareBlockException {
        SearchBody searchBody = new SearchBody(1, Filter.buildCountryFilter(country));
        return search(searchBody);
    }

    public UsersResponse doRequest(SearchBody searchBody) throws IOException {
        Request request = Request.Post(URL_FOR_USERS)
                .addHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .addHeader(COOKIE, cookieValue)
                .addHeader(PUBLIC_CSRF_TOKEN_HEADER_NAME, publicCsrfTokenValue)
                .bodyString(OBJECT_MAPPER.writeValueAsString(searchBody), ContentType.APPLICATION_JSON);
        if (proxyHost != null) {
            request.viaProxy(proxyHost);
        }
        Response response = request.execute();
        HttpResponse httpResponse = response.returnResponse();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            return OBJECT_MAPPER.readValue(httpResponse.getEntity().getContent(), UsersResponse.class);
        } else if (statusCode == 412) {
            throw new PreconditionFailedException();
        } else if (statusCode == 429) {
            throw new CloudFlareBlockException("CloudFlare has blocked you for proxy " + proxyHost.toHostString() + "!");
        } else {
            throw new IOException("Status code is " + statusCode + " for proxy " + proxyHost.toHostString());
        }
    }

}
