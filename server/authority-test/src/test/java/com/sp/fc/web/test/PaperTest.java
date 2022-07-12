package com.sp.fc.web.test;


import com.sp.fc.web.service.Paper;
import com.sp.fc.web.service.PaperService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;

import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaperTest extends MyWebIntegrationTest {

    @Autowired
    private PaperService paperService;

    TestRestTemplate client = new TestRestTemplate();

    private Paper paper1 = Paper.builder()
            .paperId(1L)
            .title("시험지1")
            .tutorId("tutor1")
            .studentIds(List.of("user1"))
            .state(Paper.State.PREPARE)
            .build();

    private Paper paper2 = Paper.builder()
            .paperId(2L)
            .title("시험지2")
            .tutorId("tutor1")
            .studentIds(List.of("user2"))
            .state(Paper.State.PREPARE)
            .build();

    private Paper paper3 = Paper.builder()
            .paperId(3L)
            .title("시험지3")
            .tutorId("tutor1")
            .studentIds(List.of("user1"))
            .state(Paper.State.READY)
            .build();


    @DisplayName("1. greeting 메시지를 불러온다.")
    @Test
    void test_1() {
        paperService.setPaper(paper1);
        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<List<Paper>> response = client.exchange(uri("/paper/mypapers"),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Paper>>() {
                });

        assertEquals(200, response.getStatusCodeValue());
        System.out.println(response.getBody());
    }

    @DisplayName("1. user1 이 user2의 시험지는 볼 수 없다.")
    @Test
    void test_2() {
        paperService.setPaper(paper2);
        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<Paper> response = client.exchange(uri("/paper/get/2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertEquals(403, response.getStatusCodeValue());
    }

    @DisplayName("3. user2여도 출제중인(prepare)시험지는 볼 수 없다.")
    @Test
    void test_3() {
        paperService.setPaper(paper2);
        client = new TestRestTemplate("user2", "1111");
        ResponseEntity<Paper> response = client.exchange(uri("/paper/get/2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertEquals(403, response.getStatusCodeValue());
    }

    @DisplayName("4. postFilter 테스트")
    @Test
    void test_4() {
        paperService.setPaper(paper1);
        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<List<Paper>> response = client.exchange(uri("/paper/mypapers2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Paper>>() {
                });

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        System.out.println(response.getBody());
    }

    @DisplayName("5. postFilter 테스트2")
    @Test
    void test_5() {
        paperService.setPaper(paper1);
        paperService.setPaper(paper2);
        paperService.setPaper(paper3);

        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<List<Paper>> response = client.exchange(uri("/paper/mypapers2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Paper>>() {
                });

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        System.out.println(response.getBody());
    }

    @DisplayName("6. postAuthorize 테스트")
    @Test
    void test_6() {
        paperService.setPaper(paper2);
        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<Paper> response = client.exchange(uri("/paper/get2/2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        assertEquals(403, response.getStatusCodeValue());
    }

    @DisplayName("7. 교장선생님은 모든 시험지 열람이 가능하다.")
    @Test
    void test_7() {
        paperService.setPaper(paper1);
        paperService.setPaper(paper2);
        paperService.setPaper(paper3);

        client = new TestRestTemplate("primary", "1111");
        ResponseEntity<List<Paper>> response = client.exchange(uri("/paper/papersByPrimary2"),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Paper>>() {
                });

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().size());
        System.out.println(response.getBody());
    }

    @DisplayName("8. 사용자가 임시로 교장권한을 얻어서 시험지를 본다.")
    @Test
    void test_8() {
        paperService.setPaper(paper1);
        paperService.setPaper(paper2);
        paperService.setPaper(paper3);

        client = new TestRestTemplate("user1", "1111");
        ResponseEntity<List<Paper>> response = client.exchange(uri("/paper/allpapers"),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Paper>>() {
                });

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().size());
        System.out.println(response.getBody());
    }
}
