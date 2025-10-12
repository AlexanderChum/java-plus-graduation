package client;

import lombok.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stat.dto.EndpointHitDto;
import stat.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static stat.constant.Const.DATE_TIME_FORMAT;

@Service
@SuppressWarnings("unused")
public class StatsClient {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    private String baseUrl;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public StatsClient(DiscoveryClient discoveryClient, RestTemplate restTemplate, RetryTemplate retryTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    public ResponseEntity<EndpointHitDto> postHit(EndpointHitDto endpointHitDto) {
        baseUrl = makeUri();
        String url = baseUrl + "/hit";
        return restTemplate.postForEntity(url, endpointHitDto, EndpointHitDto.class);
    }

    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String startStr = start.format(DATE_TIME_FORMATTER);
        String endStr = end.format(DATE_TIME_FORMATTER);

        baseUrl = makeUri();

        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append("/stats?start=").append(startStr)
                .append("&end=").append(endStr)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlBuilder.append("&uris=").append(uri);
            }
        }

        String url = urlBuilder.toString();
        ResponseEntity<ViewStatsDto[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                ViewStatsDto[].class
        );
        return Arrays.asList(response.getBody());
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances("STATS-SERVICE")
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable("Ошибка обнаружения адреса сервиса статистики");
        }
    }

    private String makeUri() {
        ServiceInstance instance = retryTemplate.execute(ctx -> getInstance());
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort()).toString();
    }
}