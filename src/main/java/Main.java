import model.Profile;
import parser.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;


public class Main {

    public static void main(String[] args) {
        try (FileWriter fileWriter = new FileWriter(new File("testData.txt"))) {
            Parser parser = new Parser(
                    "__cfduid=dddf0f182d88dab09e177c74e305912be1561296638; __cf_bm=534e7f95df16017c85c130584d96af3910d022db-1561296639-1800-ARixdsOrTiMczX2qs3QeLCNdKgVVaMK69XydqXq/oY80HYL21TwHoTYmWSyAXdXeNwpywfb6Hnkes46YL2mzjjE=; visitor-uuid=e0d35665-5502-43f5-b9f2-7c7bde188149; PRIVATE-CSRF-TOKEN=wVsW%2FzMo5uKzJSkDSHuKAJFH5AccWzRSIV%2FyowDPCuk%3D; country_code=RU; continent_code=EU; _ArtStation_session=T2VhblBTOHFocG05aS81Q3h3QTNxTU1UNERnZmx3MmpCY3JKVGh5a0UzS0pYSUhzbzlBWWRvNE8zcXJIUVgwTGxPRm1CZkEzazcxOVc3WFlxdnR3SkIxYTA3cUZKUWZFWENFQ0g0cWZYV0dkZ0pnTDBtNXlBMkE0OCtKN1JsZWpQdURDRStEbVVXTnBPVTl0UmlnUHMveEdjRTN5WElmNEdnWDBOazJqbzJZcTliZ3MzMW1qNVIxbG1hMFpQYS9yd1B5QW9lM01EUmxYSUVHZTZIZ3p3UT09LS1wVnpVRERuZTdkN3IvM0xzaXlNS2JRPT0%3D--3445e763ac9134c131c585fcf7cb621a570f8aba",
                    "Rc/I7GjtY2DCcWS5xy7HxwWb9KmXNCl+UMNZbwRef/FbgUz41uEa7nqV2ICIEzNIlgHS1EYzRXojaZcUI1zU7Q=="
            );
            Set<Profile> profiles = parser.searchAll(10_000);
            for (Profile profile : profiles) {
                fileWriter.write(profile.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
