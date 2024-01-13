package today.seasoning.seasoning.solarterm.service.scheduled;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import today.seasoning.seasoning.common.aws.SnsService;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.domain.SolarTermRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterNextYearSolarTermsService {

    @Value("${OPEN_API_KEY}")
    private String API_KEY;

    private final SnsService snsService;
    private final SolarTermRepository solarTermRepository;

    @Scheduled(cron = "0 0 0 1 12 *")
    public void findAndRegisterNextYearSolarTerms() {
        String nextYear = String.valueOf(LocalDate.now().getYear() + 1);
        List<String> months = List.of("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

        try {
            List<LocalDate> nextYearSolarTermDates = new ArrayList<>(24);

            for (String month : months) {
                findAndAddSolarTermDate(nextYear, month, nextYearSolarTermDates);
            }

            for (int i = 0; i < 24; i++) {
                SolarTerm solarTerm = new SolarTerm(i + 1, nextYearSolarTermDates.get(i));
                solarTermRepository.save(solarTerm);
            }

            snsService.publish("[시즈닝] " + nextYear + "년 절기 등록 완료");
            log.info("{}년 절기 등록 완료", nextYear);
        } catch (Exception e) {
            snsService.publish("[시즈닝] ERROR - " + nextYear + "년 절기 등록 실패");

            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            log.error("{}년 절기 등록 실패 : {}", nextYear, stringWriter);
        }
    }

    private void findAndAddSolarTermDate(String year, String month, List<LocalDate> terms) throws Exception {
        String xmlString = getXmlString(year, month);
        parseAndAddTerms(xmlString, terms);
    }

    private String getXmlString(String year, String month) throws Exception {
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

    private void parseAndAddTerms(String xmlString, List<LocalDate> terms) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(
            xmlString.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(input);
        NodeList itemList = doc.getElementsByTagName("item");

        String locdate1 = ((Element) itemList.item(0)).getElementsByTagName("locdate").item(0).getTextContent();
        String locdate2 = ((Element) itemList.item(1)).getElementsByTagName("locdate").item(0).getTextContent();

        terms.add(LocalDate.parse(locdate1, DateTimeFormatter.ofPattern("yyyyMMdd")));
        terms.add(LocalDate.parse(locdate2, DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    private URL buildUrl(String year, String month) throws MalformedURLException {

        String urlBuilder = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/get24DivisionsInfo"

            // API 키
            + "?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8) + "=" + API_KEY

            // 조회 년도
            + "&" + URLEncoder.encode("solYear", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(year,
            StandardCharsets.UTF_8)

            // 조회 월
            + "&" + URLEncoder.encode("solMonth", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(month,
            StandardCharsets.UTF_8)

            // 고정값
            + "&" + URLEncoder.encode("kst", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("0000",
            StandardCharsets.UTF_8)
            + "&" + URLEncoder.encode("sunLongitude", StandardCharsets.UTF_8) + "="
            + URLEncoder.encode("285", StandardCharsets.UTF_8)
            + "&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(
            "10", StandardCharsets.UTF_8)
            + "&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("1",
            StandardCharsets.UTF_8)
            + "&" + URLEncoder.encode("totalCount", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(
            "210114", StandardCharsets.UTF_8);

        return new URL(urlBuilder);
    }
}
