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
        String proxy = "79.137.73.163:8080";
        downloadAllProfiles(proxy);
        //convertAllProfilesToCsv();
    }

    /**
     * 1.
     * Метод выкачивания информации с сайта в виде профилей
     */
    private static void downloadAllProfiles(String proxy) throws CloudFlareBlockException {
        String cookieValue = "__cfduid=dddf0f182d88dab09e177c74e305912be1561296638; visitor-uuid=e0d35665-5502-43f5-b9f2-7c7bde188149; country_code=RU; continent_code=EU; __stripe_mid=330eb27a-f1ab-4537-883f-59e20b932ca8; PRIVATE-CSRF-TOKEN=ukxm2FnN4pO14pXbrHr%2BbqBpmF03OzBab8ikB32xzk0%3D; __cf_bm=cf226284b39d73ecdbc69be919a4bbc81df3b545-1561397568-1800-AQhYbdvLa7vwSN0EfSmR8ZuaY2JR1dxzrKA6grDkLDGcl+Xusg7xc6nSNPo0BwY+bBpOYOoVDSmhOoY1WOL+uAg=; _ArtStation_session=ZFhOemxxRU4vL3E2K01admtYMlJRVlF6U1FGamtISnNPd1IrazF2SzJPbVY2eTYwaEpCZUIxSGlqdFQraUowSWJYWDRsMmhPTERjclVZV1RndDVZUndkckNpM3E2TTgwNHl1Q3JBWk1leis0cktOR1UyTTdrSUhMdmlUVEg4QzVRR0tCQjYvSytZOFBaUzlVWEFqRElWeDdwU3hPT1lWZmxxU2psUVpqd2FnY1lOQ08zVXhUOXpYMm9vK1pYUVI2ZFdFRlVrVHNaL0ZSVHN2b0N2MjdqcEZaS1M2cU0zKzJ2VkwyaEFvT01LdmxJMERjaWNTTDNkOHUrakdiRzlNdC0tWU8vLzZobHJlYXRlaHdoN1ZBT0VlZz09--c14255fbd4707b343fc63284479581e6c563e771; __stripe_sid=ead68b33-5fe1-486d-9873-b757268f5a3a";
        String publicCsrfTokenValue = "2OaB7tyo6lA70S2yxa45cnxLd/w6bkEBpVFDDnFBFUViquc2hWUIw44zuGlp1Mcc3CLvoQ1VcVvKmecJDPDbCA==";
        Parser parser = new Parser(cookieValue, publicCsrfTokenValue, proxy);
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
