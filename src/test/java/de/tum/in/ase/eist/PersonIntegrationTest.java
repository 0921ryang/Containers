package de.tum.in.ase.eist;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PersonIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonRepository personRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testAddPerson() throws Exception {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        var response = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(person))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDeletePerson() throws Exception {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        person = personRepository.save(person);

        var response = this.mvc.perform(
                delete("/persons/" + person.getId())
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertTrue(personRepository.findAll().isEmpty());
    }

    // TODO: Add more test cases here
    @Test
    void testAddParent() throws Exception{
        Person child=new Person();
        Person parent=new Person();
        child.setFirstName("A");
        child.setLastName("B");
        LocalDate localDate=LocalDate.now();
        child.setBirthday(localDate);
        parent.setFirstName("C");
        parent.setLastName("D");
        parent.setBirthday(localDate);

        var response = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(child))
                        .contentType("application/json")
        ).andReturn().getResponse();

        var response1 = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(parent))
                        .contentType("application/json")
        ).andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(HttpStatus.OK.value(), response1.getStatus());

        String responseBody = response.getContentAsString();
        JSONObject responseJson = new JSONObject(responseBody);
        Long id = responseJson.getLong("id");

        String responseBody1 = response1.getContentAsString();
        JSONObject responseJson1 = new JSONObject(responseBody1);
        Long id1 = responseJson1.getLong("id");
        var response2 = this.mvc.perform(
                put("/persons/" + id + "/parents")
                        .content(objectMapper.writeValueAsString(personRepository.findWithParentsById(id1).get()))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response2.getStatus());
        assertEquals(2, personRepository.findAll().size());
        assertTrue(personRepository.existsById(id));
        assertTrue(personRepository.existsById(id1));

        String responseBody2 = response2.getContentAsString();
        JSONObject responseJson2 = new JSONObject(responseBody2);
        JSONArray parentsJson = responseJson2.getJSONArray("parents");
        JSONObject parentJson = parentsJson.getJSONObject(0);
        String firstName = parentJson.getString("firstName");
        String lastName = parentJson.getString("lastName");
        LocalDate birthday = LocalDate.parse(parentJson.getString("birthday"));
        assertEquals("C", firstName);
        assertEquals("D", lastName);
        assertEquals(localDate, birthday);
    }

    @Test
    void testAddThreeParents() throws Exception{
        var child = new Person();
        var parent1 = new Person();
        var parent2 = new Person();
        var parent3 = new Person();
        LocalDate localDate = LocalDate.now();
        child.setFirstName("I");
        child.setLastName("J");
        child.setBirthday(localDate);
        parent1.setFirstName("K");
        parent1.setLastName("L");
        parent1.setBirthday(localDate);
        parent2.setFirstName("E");
        parent2.setLastName("F");
        parent2.setBirthday(localDate);
        parent3.setFirstName("G");
        parent3.setLastName("H");
        parent3.setBirthday(localDate);

        var response = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(child))
                        .contentType("application/json")
        ).andReturn().getResponse();

        var response1 = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(parent1))
                        .contentType("application/json")
        ).andReturn().getResponse();

        var response2 = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(parent2))
                        .contentType("application/json")
        ).andReturn().getResponse();

        var response3 = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(parent3))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(HttpStatus.OK.value(), response1.getStatus());
        assertEquals(HttpStatus.OK.value(), response2.getStatus());
        assertEquals(HttpStatus.OK.value(), response3.getStatus());

        assertEquals(4, personRepository.findAll().size());

        String responseBody = response.getContentAsString();
        JSONObject responseJson = new JSONObject(responseBody);
        Long id = responseJson.getLong("id");

        String responseBody1 = response1.getContentAsString();
        JSONObject responseJson1 = new JSONObject(responseBody1);
        Long id1 = responseJson1.getLong("id");

        String responseBody2 = response2.getContentAsString();
        JSONObject responseJson2 = new JSONObject(responseBody2);
        Long id2 = responseJson2.getLong("id");

        String responseBody3 = response3.getContentAsString();
        JSONObject responseJson3 = new JSONObject(responseBody3);
        Long id3 = responseJson3.getLong("id");

        var responsePut = this.mvc.perform(
                put("/persons/" + id + "/parents")
                        .content(objectMapper.writeValueAsString(personRepository.findWithParentsById(id1).get()))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), responsePut.getStatus());
        assertEquals(4, personRepository.findAll().size());

        assertTrue(personRepository.existsById(id));
        assertTrue(personRepository.existsById(id1));
        assertTrue(personRepository.existsById(id2));
        assertTrue(personRepository.existsById(id3));

        String responseBody0 = responsePut.getContentAsString();
        JSONObject responseJson0 = new JSONObject(responseBody0);
        JSONArray parentsJson = responseJson0.getJSONArray("parents");
        JSONObject parentJson = parentsJson.getJSONObject(0);
        String firstName = parentJson.getString("firstName");
        String lastName = parentJson.getString("lastName");
        LocalDate birthday = LocalDate.parse(parentJson.getString("birthday"));
        assertEquals("K", firstName);
        assertEquals("L", lastName);
        assertEquals(localDate, birthday);

        var responsePut1 = this.mvc.perform(
                put("/persons/" + id + "/parents")
                        .content(objectMapper.writeValueAsString(personRepository.findWithParentsById(id2).get()))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), responsePut1.getStatus());
        assertEquals(4, personRepository.findAll().size());
        assertTrue(personRepository.existsById(id));
        assertTrue(personRepository.existsById(id1));
        assertTrue(personRepository.existsById(id2));
        assertTrue(personRepository.existsById(id3));

        String responseBody00 = responsePut1.getContentAsString();
        JSONObject responseJson00 = new JSONObject(responseBody00);
        JSONArray parentsJson0 = responseJson00.getJSONArray("parents");
        JSONObject parentJson0 = parentsJson0.getJSONObject(1);
        String firstName0 = parentJson0.getString("firstName");
        String lastName0 = parentJson0.getString("lastName");
        LocalDate birthday0 = LocalDate.parse(parentJson0.getString("birthday"));
        assertEquals("E", firstName0);
        assertEquals("F", lastName0);
        assertEquals(localDate, birthday0);

        var responseFalse = this.mvc.perform(
                put("/persons/" + id + "/parents")
                        .content(objectMapper.writeValueAsString(personRepository.findWithParentsById(id3).get()))
                        .contentType("application/json")
        ).andReturn().getResponse();
        assertEquals(400, responseFalse.getStatus());
    }
}
