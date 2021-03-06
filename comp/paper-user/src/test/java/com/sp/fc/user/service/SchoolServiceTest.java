package com.sp.fc.user.service;

import com.sp.fc.user.domain.School;
import com.sp.fc.user.repository.SchoolRepository;
import com.sp.fc.user.service.helper.SchoolTestHelper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest //db 데이터 소스를 h2 에 inmemory 방식으로 만든다.
@RequiredArgsConstructor
class SchoolTest {

    @Autowired
    private  SchoolRepository schoolRepository;

    @Autowired
    private  SchoolService schoolService;

    private SchoolTestHelper schoolTestHelper;
    School school;

    @BeforeEach
    void before() {
        this.schoolRepository.deleteAll();
//        this.schoolService = new SchoolService(schoolRepository);
        this.schoolTestHelper = new SchoolTestHelper(this.schoolService);
        school = this.schoolTestHelper.createSchool("테스트 학교", "서울");

    }

    @DisplayName("1. 학교를 생성한다.")
    @Test
    void test_1() {
        List<School> lists = schoolRepository.findAll();
        assertEquals(1, lists.size());
        schoolTestHelper.assertSchool(lists.get(0), "테스트 학교", "서울");

    }

    @DisplayName("2. 학교 이름을 수정한다")
    @Test
    void test_2() {
        schoolService.updateName(school.getSchoolId(), "테스트2 학교");
        assertEquals("테스트2 학교", schoolRepository.findAll().get(0).getName());

    }

    @DisplayName("3. 지역 목록을 가져온다.")
    @Test
    void test_3() {
        List<String> list = schoolService.cities();
        assertEquals(1, list.size());
        assertEquals("서울", list.get(0));

        schoolTestHelper.createSchool("부산 학교", "부산");
        list = schoolService.cities();
        assertEquals(2, list.size());
    }

    @DisplayName("4. 지역으로 학교 목록을 가져온다.")
    @Test
    void test_4() {
        List<School> list = schoolService.getSchoolList("서울");
        assertEquals(1, list.size());

        schoolTestHelper.createSchool("서울2 학교", "서울");
        list = schoolService.getSchoolList("서울");
        assertEquals(2, list.size());
    }

}