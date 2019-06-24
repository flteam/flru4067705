package flteam.flru4067705;

import flteam.flru4067705.exception.CloudFlareBlockException;
import flteam.flru4067705.model.Country;
import flteam.flru4067705.model.Profile;
import flteam.flru4067705.parser.Parser;
import flteam.flru4067705.util.CountryUtil;
import flteam.flru4067705.util.CsvUtil;
import flteam.flru4067705.util.ProfileUtil;

import java.io.File;
import java.util.List;
import java.util.Set;

public class App {

    public static void main(String[] args) throws CloudFlareBlockException {
        downloadAllProfiles();
        //convertAllProfilesToCsv();
    }

    /**
     * 1.
     * Метод выкачивания информации с сайта в виде профилей
     */
    private static void downloadAllProfiles() throws CloudFlareBlockException {
        String cookieValue = "__cfduid=dcb046d96030d4c6f0b3a929309b26d8e1561356569; __cf_bm=a6be066819653a1a78bb5db5d667e7c88bb67b65-1561356569-1800-AWipXkZn7tzPu3+KdLp0Oll3AUYErSeIuX4qa+PPYtHnJ61cCjNm58M+MxEIL6zCRLJUBA2Iut1VB7YboUOHEck=; cf_clearance=e8b50a7fc9ed4cca72b6fba34b64bd88382de683-1561356587-1800-250; visitor-uuid=c7adabce-5ad5-47f9-a457-cb9e61673286; PRIVATE-CSRF-TOKEN=47IKwHzFhC0gtgfLm%2FRMCixuNVyCKwkqp%2FdToiyectQ%3D; country_code=US; continent_code=NA; _ArtStation_session=TWpUbHY2dWpkcklIUUR0OENIdDJuM3VkRjRYSHNUUzRUTjRSVVVuSHVVYXNIYmIxUDRKSWJEYkk4ZXRGbnA2dko2YzQxTEQwUTBhNk5GYmNMaHhDWGtwbU5YVGRtQWRFT0ZzaVVuakJkM1BSQ0N5V0ZUTFVlbnBhb1VRaEtsYUEyRXBTSndkWU9ya2NrRmY3YnVDbEJkcmh1R2xaVExlM2Nkc21NbHB2RG9LNlRGSU1HZHhYV1BsS2lZZlhZdUFHcWRNZi93UCtkZG1qb3pFTjRPeERXZz09LS05Nm44d29hWVREYlNVK0dudzdhWEhRPT0%3D--4d9ebbfdb62b5075a196d2b54c4a96d88f7d48f5; __asc=95f1709116b881ad9ce7a5a00d7; __auc=95f1709116b881ad9ce7a5a00d7; _pk_id.2.119b=0279da724db0a17e.1561356591.1.1561356592.1561356591.; _pk_ses.2.119b=1; _fbp=fb.1.1561356592014.850090409; __stripe_mid=b8556914-70ed-499d-a043-de8a9136436e; __stripe_sid=45c12d91-c16d-4340-ae0a-895544cfa272; _ga=GA1.2.703703350.1561356716; _gid=GA1.2.1657926166.1561356716; _gat_gtag_UA_29038430_1=1; _hjIncludedInSample=1";
        String publicCsrfTokenValue = "Hmx57Fm9Rvv680PxMYMMgGBbsbSkbV9lJni4s9MxxIL93nMsJXjC1tpFRDqqd0CKTDWE6CZGVk+Bj+sR/6+2Vg==";
        Parser parser = new Parser(cookieValue, publicCsrfTokenValue);
        List<Country> countries = CountryUtil.getAllCountries();
        int i = 1;
        for (Country country : countries) {
            System.out.println("Download profiles for " + country.name + ", " + i + "/" + countries.size());
            try {
                Set<Profile> profiles = parser.searchAllByCountry(country);
                ProfileUtil.saveProfilesByCountry(profiles, country);
            } catch (CloudFlareBlockException e) {
                throw new CloudFlareBlockException("CloudFlare has blocked you on country " + country.id);
            }
            i++;
        }
    }

    /**
     * 2.
     * Метод конвертации выкачанных профилей в файл CSV с запросом дополнительных полей
     */
    private static void convertAllProfilesToCsv() {
        File file = new File("profiles");
        File[] jsons = file.listFiles();
        int i = 1;
        for (File json : jsons) {
            System.out.println("Convert for " + json.getName() + ", " + i + "/" + jsons.length);
            CsvUtil.convertProfileToCsv(json.getName());
            i++;
        }
    }

}
