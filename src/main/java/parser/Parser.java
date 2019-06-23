package parser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import exception.CloudFlareBlockException;
import model.Profile;
import model.SearchBody;
import model.UsersResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.cookie.SM.COOKIE;

public class Parser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule());
    private static final String URL = "https://www.artstation.com/api/v2/search/users.json";
    private static final String PUBLIC_CSRF_TOKEN_HEADER_NAME = "public-csrf-token";

    private String cookieValue;
    private String publicCsrfTokenValue;

    public Parser(String cookieValue, String publicCsrfTokenValue) {
        this.cookieValue = cookieValue;
        this.publicCsrfTokenValue = publicCsrfTokenValue;
    }

    public Set<Profile> searchAll(long limit) {
        Set<Profile> result = new HashSet<>();
        try {
            for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE && result.size() < limit; i++) {
                String query = String.valueOf(i);
                result.addAll(searchAllByQuery(query));
            }
        } catch (CloudFlareBlockException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Set<Profile> searchAll() {
        return searchAll(Long.MAX_VALUE);
    }

    public Set<Profile> searchAllByQuery(String query) throws CloudFlareBlockException {
        Set<Profile> result = new HashSet<>();
        try {
            SearchBody searchBody = new SearchBody(query, 1L);
            UsersResponse usersResponse = doRequest(searchBody);
            if (usersResponse != null) {
                long count = usersResponse.totalCount;
                for (long i = 1; i <= count / SearchBody.MAX_PER_PAGE + 1; i++) {
                    searchBody.page = i;
                    usersResponse = doRequest(searchBody);
                    Optional.ofNullable(usersResponse).ifPresent(ur -> result.addAll(ur.profiles));
                }
            }
        } catch (CloudFlareBlockException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Nullable
    private UsersResponse doRequest(SearchBody searchBody) throws IOException {
        Response response = Request.Post(URL)
                .addHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .addHeader(COOKIE, cookieValue)
                .addHeader(PUBLIC_CSRF_TOKEN_HEADER_NAME, publicCsrfTokenValue)
                .bodyString(OBJECT_MAPPER.writeValueAsString(searchBody), ContentType.APPLICATION_JSON)
                .execute();
        HttpResponse httpResponse = response.returnResponse();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            return OBJECT_MAPPER.readValue(httpResponse.getEntity().getContent(), UsersResponse.class);
        } else if (statusCode == 429 || statusCode == 412) {
            throw new CloudFlareBlockException();
        }
        return null;
    }

}
