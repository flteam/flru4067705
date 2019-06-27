package flteam.flru4067705;

import flteam.flru4067705.exception.CloudFlareBlockException;
import flteam.flru4067705.model.Country;
import flteam.flru4067705.model.Profile;
import flteam.flru4067705.parser.Parser;
import flteam.flru4067705.util.CountryUtil;
import flteam.flru4067705.util.CsvUtil;
import flteam.flru4067705.util.ProfileUtil;
import org.apache.http.HttpHost;

import java.io.File;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {

    private static final Queue<HttpHost> PROXIES = Stream.of(
            new HttpHost("158.69.59.125", 8888),
            new HttpHost("89.223.80.30", 8080),
            new HttpHost("3.86.97.156", 8888)
    ).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(PROXIES.size());

    public static void main(String[] args) {
        String cookieValue = "__cfduid=dddf0f182d88dab09e177c74e305912be1561296638; visitor-uuid=e0d35665-5502-43f5-b9f2-7c7bde188149; country_code=RU; continent_code=EU; __stripe_mid=330eb27a-f1ab-4537-883f-59e20b932ca8; PRIVATE-CSRF-TOKEN=vAr%2BLFU4V56o14lPC3csAj2yozobuVspnrzCv4cbzmc%3D; __cf_bm=2c226180e422fcb39cb46231931c8339e053891e-1561568940-1800-AcHK9DqRZz4Ea05TNv/NLjd9Yr9QjcsHfSEeRXrkAyPHO5eMyBeeMerjH8jJp2ng8Usz14xbZ6rK7/q9r+YHSjY=; __stripe_sid=90318407-c84d-4256-86ed-471e613fe167";
        String publicCsrfTokenValue = "f2yHHYHSON3w5cSnTHpkKJIqqaapSykQBwXt2fv4VUPDZnkx1OpvQ1gyTehHDUgqr5gKnLLycjmZuS9mfOObJA==";

        //parallelDownloadAllProfiles(cookieValue, publicCsrfTokenValue);
        //CountryUtil.checkThatAllCountriesArePresent();
        checkDownloadedProfiles(cookieValue, publicCsrfTokenValue);
        //convertAllProfilesToCsv();
    }

    /**
     * 1.
     * Метод выкачивания информации с сайта в виде профилей
     */
    private static void parallelDownloadAllProfiles(String cookieValue, String publicCsrfTokenValue) {
        Set<Country> countries = CountryUtil.getAllCountries();
        for (Country country : countries) {
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    HttpHost proxy = PROXIES.poll();
                    downloadAllProfilesForCountry(proxy, country, cookieValue, publicCsrfTokenValue);
                    PROXIES.offer(proxy);
                } catch (CloudFlareBlockException e) {
                    e.printStackTrace();
                }
            });
        }
        EXECUTOR_SERVICE.shutdown();
    }

    private static void downloadAllProfilesForCountry(HttpHost proxy,
                                                      Country country,
                                                      String cookieValue,
                                                      String publicCsrfTokenValue) throws CloudFlareBlockException {
        Parser parser = new Parser(cookieValue, publicCsrfTokenValue, proxy);
        System.out.println("Downloading profiles for " + country.name);
        try {
            Set<Profile> profiles = parser.searchAllByCountry(country);
            ProfileUtil.saveProfilesByCountry(profiles, country);
        } catch (CloudFlareBlockException e) {
            throw new CloudFlareBlockException("CloudFlare has blocked you on country " + country.name);
        }
    }

    /**
     * 2.
     * Метод конвертации выкачанных профилей в файл CSV с запросом дополнительных полей
     */
    private static void convertAllProfilesToCsv() {
        File file = new File("profiles");
        File[] jsons = Objects.requireNonNull(file.listFiles());
        int i = 1;
        for (File json : jsons) {
            System.out.println("Convert for " + json.getName() + ", " + i + "/" + jsons.length);
            CsvUtil.convertProfileToCsv(json.getName());
            i++;
        }
    }

    /**
     * 3.
     * Метод сверки выкачанного количества профилей
     */
    private static void checkDownloadedProfiles(String cookieValue, String publicCsrfTokenValue) {
        Set<Country> countries = CountryUtil.getAllCountries();
        for (Country country : countries) {
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    HttpHost proxy = PROXIES.poll();
                    ProfileUtil.checkDownloadedProfilesForCountry(country, proxy, cookieValue, publicCsrfTokenValue);
                    Thread.sleep(Parser.TIMEOUT);
                    PROXIES.offer(proxy);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        EXECUTOR_SERVICE.shutdown();
    }

}
