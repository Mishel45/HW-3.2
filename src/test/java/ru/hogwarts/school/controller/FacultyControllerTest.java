package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @BeforeEach
    void clearDatabase() {
        avatarRepository.deleteAll();
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void testCreateFaculty() {
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(getBaseUrl(), faculty, Faculty.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Гриффиндор");
    }

    @Test
    void testGetFacultyInfo() {
        Faculty saved = facultyRepository.save(new Faculty(null, "Слизерин", "Зеленый"));

        ResponseEntity<Faculty> response = restTemplate.getForEntity(getBaseUrl() + "/" + saved.getId(), Faculty.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getColor()).isEqualTo("Зеленый");
    }

    @Test
    void testGetFacultyInfoNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/999", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditFaculty() {
        Faculty saved = facultyRepository.save(new Faculty(null, "Когтевран", "Синий"));
        saved.setColor("Сине-бронзовый");

        ResponseEntity<Faculty> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(saved),
                Faculty.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getColor()).isEqualTo("Сине-бронзовый");
    }

    @Test
    void testDeleteFaculty() {
        Faculty saved = facultyRepository.save(new Faculty(null, "Пуффендуй", "Желтый"));

        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(facultyRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindByNameOrColor() {
        facultyRepository.save(new Faculty(null, "Гриффиндор", "Красный"));

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(getBaseUrl() + "/search?search=грифф", Faculty[].class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().length).isEqualTo(1);
        Assertions.assertThat(response.getBody()[0].getName()).isEqualTo("Гриффиндор");
    }
}