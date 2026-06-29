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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/student";
    }

    @BeforeEach
    void clearDatabase() {
        avatarRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void testCreateStudent() {
        Student student = new Student(null, "Гарри Поттер", 11);

        ResponseEntity<Student> response = restTemplate.postForEntity(getBaseUrl(), student, Student.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Гарри Поттер");
    }

    @Test
    void testGetStudentInfo() {
        Student saved = studentRepository.save(new Student(null, "Гермиона Грейнджер", 12));

        ResponseEntity<Student> response = restTemplate.getForEntity(getBaseUrl() + "/" + saved.getId(), Student.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Гермиона Грейнджер");
    }

    @Test
    void testGetStudentInfoNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(getBaseUrl() + "/999", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditStudent() {
        Student saved = studentRepository.save(new Student(null, "Рон Уизли", 11));
        saved.setName("Рональд Уизли");

        ResponseEntity<Student> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.PUT,
                new HttpEntity<>(saved),
                Student.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo("Рональд Уизли");
    }

    @Test
    void testDeleteStudent() {
        Student saved = studentRepository.save(new Student(null, "Драко Малфой", 11));

        ResponseEntity<Void> response = restTemplate.exchange(
                getBaseUrl() + "/" + saved.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(studentRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindStudentsByAge() {
        studentRepository.save(new Student(null, "Полумна Лавгуд", 12));

        ResponseEntity<Student[]> response = restTemplate.getForEntity(getBaseUrl() + "?age=12", Student[].class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().length).isEqualTo(1);
        Assertions.assertThat(response.getBody()[0].getName()).isEqualTo("Полумна Лавгуд");
    }
}