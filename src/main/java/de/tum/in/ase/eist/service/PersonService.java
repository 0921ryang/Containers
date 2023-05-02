package de.tum.in.ase.eist.service;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
        if (person==null||parent==null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set=person.getParents();
        if (set.size()>=2) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        set.add(parent);
        return save(person);
    }

    public Person addChild(Person person, Person child) {
        // TODO: Implement
        if (person==null||child==null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> childParents=child.getParents();
        if (childParents.size()>=2) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set1=person.getChildren();
        set1.add(child);
        return save(person);
    }

    public Person removeParent(Person person, Person parent) {
        // TODO: Implement
        if (person==null||parent==null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set=person.getParents();
        if (set.size()<=1) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        set.remove(parent);
        return save(person);
    }

    public Person removeChild(Person person, Person child) {
        // TODO: Implement
        if (person==null||child==null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        Set<Person> set=person.getChildren();
        if (set.size()<=1) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));
        }
        set.remove(child);
        return save(person);
    }
}
