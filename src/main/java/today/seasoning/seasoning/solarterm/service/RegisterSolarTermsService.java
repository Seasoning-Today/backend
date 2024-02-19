package today.seasoning.seasoning.solarterm.service;

import static java.net.URLEncoder.*;
import static java.nio.charset.StandardCharsets.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import today.seasoning.seasoning.common.aws.SnsService;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.domain.SolarTermRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterSolarTermsService {

    @Value("${OPEN_API_KEY}")
    private String API_KEY;

    private final SnsService snsService;
    private final SolarTermRepository solarTermRepository;

    @Scheduled(cron = "0 0 0 1 12 *")
    public void doService() {
        int nextYear = LocalDate.now().getYear() + 1;

        try {
            findAndRegisterSolarTermsOf(nextYear);
            snsService.publish("[시즈닝] " + nextYear + "년 절기 등록 완료");
        } catch (Exception e) {
            snsService.publish("[시즈닝] ERROR - " + nextYear + "년 절기 등록 실패");
        }
    }

    public void findAndRegisterSolarTermsOf(int year) {
        List<String> months = List.of("02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01");

        try {
            List<LocalDate> solarTermDates = new ArrayList<>(24);

            for (String month : months) {
                int findYear = month.equals("01") ? year + 1 : year;
                String xmlResponse = callAPI(String.valueOf(findYear), month);
                parseSolarTermDates(xmlResponse, solarTermDates);
            }

            for (int i = 0; i < 24; i++) {
                SolarTerm solarTerm = new SolarTerm(i + 1, solarTermDates.get(i));
                solarTermRepository.save(solarTerm);
            }

            log.info("{}년 절기 등록 완료", year);
        } catch (Exception e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            log.error("{}년 절기 등록 실패 : {}", year, stringWriter);

            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, year + "년 절기 등록 실패");
        }
    }

    private String callAPI(String year, String month) throws Exception {
        URL url = buildUrl(year, month);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) ?
            new BufferedReader(new InputStreamReader(conn.getInputStream())) :
            new BufferedReader(new InputStreamReader(conn.getErrorStream()));

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }

    private void parseSolarTermDates(String xmlString, List<LocalDate> terms) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes(UTF_8));
        Document doc = builder.parse(input);
        NodeList itemList = doc.getElementsByTagName("item");

        String locdate1 = ((Element) itemList.item(0)).getElementsByTagName("locdate").item(0).getTextContent();
        String locdate2 = ((Element) itemList.item(1)).getElementsByTagName("locdate").item(0).getTextContent();

        terms.add(LocalDate.parse(locdate1, DateTimeFormatter.ofPattern("yyyyMMdd")));
        terms.add(LocalDate.parse(locdate2, DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    private URL buildUrl(String year, String month) throws MalformedURLException {
        String url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/get24DivisionsInfo"

            // API 키
            + "?" + encode("serviceKey", UTF_8) + "=" + API_KEY

            // 조회 년도
            + "&" + encode("solYear", UTF_8) + "=" + encode(year, UTF_8)

            // 조회 월
            + "&" + encode("solMonth", UTF_8) + "=" + encode(month, UTF_8)

            // 고정값
            + "&" + encode("kst", UTF_8) + "=" + encode("0000", UTF_8)
            + "&" + encode("sunLongitude", UTF_8) + "=" + encode("285", UTF_8)
            + "&" + encode("numOfRows", UTF_8) + "=" + encode("10", UTF_8)
            + "&" + encode("pageNo", UTF_8) + "=" + encode("1", UTF_8)
            + "&" + encode("totalCount", UTF_8) + "=" + encode("210114", UTF_8);

        return new URL(url);
    }
}