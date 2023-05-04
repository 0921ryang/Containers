package de.tum.in.ase.eist.service;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person save(Person person) {
        if (person.getBirthday().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday may not be in the future");
        }
        return personRepository.save(person);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    public Optional<Person> getById(Long id) {
        return personRepository.findWithParentsAndChildrenById(id);
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Person addParent(Person person, Person parent) {
        // TODO: Implement
        //eliminate null object
        if (person == null || parent ==null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        //get the right to access parents
        Set<Person> set=person.getParents();
        if (set.size() >= 2) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set1=new HashSet<>(set);
        set1.add(parent);
        person.setParents(set1);
        return save(person);
    }

    public Person addChild(Person person, Person child) {
        // TODO: Implement
        if (person == null || child == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set=child.getParents();
        if (set.size() >= 2) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set1=person.getChildren();
        Set<Person> set2= new HashSet<>(set1);
        set2.add(child);
        person.setChildren(set2);
        return save(person);
    }

    public Person removeParent(Person person, Person parent) {
        // TODO: Implement
        if (person == null || parent == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set=person.getParents();
        if (set.size() != 2) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set1 = new HashSet<>(set);
        set1.remove(parent);
        person.setParents(set1);
        return save(person);
    }

    public Person removeChild(Person person, Person child) {
        // TODO: Implement
        if (person == null || child == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set=person.getChildren();
        if (set.size() < 1) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set2=child.getParents();
        if (set2.size() != 2) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set1 = new HashSet<>(set);
        set1.remove(child);
        person.setChildren(set1);
        return save(person);
    }
}
