package de.tum.in.ase.eist;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import de.tum.in.ase.eist.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PersonServiceTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testAddPerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        personService.save(person);

        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDeletePerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        person = personRepository.save(person);

        personService.delete(person);

        assertTrue(personRepository.findAll().isEmpty());
    }

    // TODO: Add more test cases here
    @Test
    void testAddParent() {
        Person child=new Person();
        Person parent=new Person();
        child.setFirstName("A");
        child.setLastName("B");
        child.setBirthday(LocalDate.MIN);
        parent.setFirstName("C");
        parent.setLastName("D");
        parent.setBirthday(LocalDate.now());
        child=personService.save(child);
        parent=personService.save(parent);
        assertEquals(child, personService.addParent(child, parent));
        assertEquals(2, personRepository.findAll().size());
        assertTrue(personRepository.existsById(child.getId()));
        assertTrue(personRepository.existsById(parent.getId()));
        assertTrue(child.getParents().contains(parent));
    }
    @Test
    void testAddThreeParents() {
        var child=new Person();
        var parent1=new Person();
        var parent2=new Person();
        var parent3=new Person();
        child.setFirstName("I");
        child.setLastName("J");
        child.setBirthday(LocalDate.MIN);
        parent1.setFirstName("K");
        parent1.setLastName("L");
        parent1.setBirthday(LocalDate.now());
        parent2.setFirstName("E");
        parent2.setLastName("F");
        parent2.setBirthday(LocalDate.now());
        parent3.setFirstName("G");
        parent3.setLastName("H");
        parent3.setBirthday(LocalDate.now());
        child=personService.save(child);
        parent1=personService.save(parent1);
        parent2=personService.save(parent2);
        parent3=personService.save(parent3);
        assertEquals(child, personService.addParent(child, parent1));
        assertEquals(child, personService.addParent(child, parent2));
        assertEquals(4, personRepository.findAll().size());
        assertTrue(personRepository.existsById(child.getId()));
        assertTrue(personRepository.existsById(parent1.getId()));
        assertTrue(personRepository.existsById(parent2.getId()));
        assertTrue(personRepository.existsById(parent3.getId()));
        assertTrue(child.getParents().contains(parent1));
        assertTrue(child.getParents().contains(parent2));
        Person[] people=new Person[2];
        people[0]=child;
        people[1]=parent3;
        ResponseStatusException exception=assertThrows(ResponseStatusException.class, () -> personService.addParent(people[0], people[1]));
        assertEquals(400, exception.getStatusCode().value());
    }
}
