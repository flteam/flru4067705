package flteam.flru4067705.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED;

public class TranslateUtil {

    private static final String API_KEY = "";

    private static final Map<Integer, String> RESPONSE_CODES = new HashMap<>();

    static {
        RESPONSE_CODES.put(200, "Операция выполнена успешно");
        RESPONSE_CODES.put(401, "Неправильный API-ключ");
        RESPONSE_CODES.put(402, "API-ключ заблокирован");
        RESPONSE_CODES.put(404, "Превышено суточное ограничение на объем переведенного текста");
        RESPONSE_CODES.put(413, "Превышен максимально допустимый размер текста");
        RESPONSE_CODES.put(422, "Текст не может быть переведен");
        RESPONSE_CODES.put(501, "Заданное направление перевода не поддерживается");
    }

    public static String translateFromEngToRus(String text) throws IOException {
        NameValuePair nameValuePair = new BasicNameValuePair("text", text);
        Response response = Request.Post("https://translate.yandex.net/api/v1.5/tr.json/translate?lang=en-ru&key=" + API_KEY)
                .addHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString())
                .bodyForm(nameValuePair)
                .execute();
        HttpResponse httpResponse = response.returnResponse();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String content = IOUtils.toString(httpResponse.getEntity().getContent());
        if (statusCode == 200) {
            JSONTokener jsonTokener = new JSONTokener(content);
            JSONObject jsonObject = new JSONObject(jsonTokener);
            int code = jsonObject.optInt("code");
            if (code == 200) {
                return jsonObject.getJSONArray("text").getString(0);
            } else {
                throw new IOException(String.format("Code is %d! Reason: %s", code, RESPONSE_CODES.getOrDefault(code, "unknown")));
            }
        } else {
            throw new IOException("Status code is not 200! Reason:\n" + content);
        }
    }

}
