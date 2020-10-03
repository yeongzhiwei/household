package com.yeongzhiwei.assessment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import com.yeongzhiwei.assessment.exception.FamilyMemberDoesNotExistException;
import com.yeongzhiwei.assessment.exception.HouseholdNotFoundException;
import com.yeongzhiwei.assessment.exception.SpouseDoesNotExistException;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;
import com.yeongzhiwei.assessment.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.yeongzhiwei.assessment.repository.HouseholdSpecs.*;

@Service
public class HouseholdService {
    
    private HouseholdRepository householdRepository;
    private PersonRepository personRepository;

    public HouseholdService(@Autowired HouseholdRepository householdRepository, @Autowired PersonRepository personRepository) {
        this.householdRepository = householdRepository;
        this.personRepository = personRepository;
    }

    public Household createHousehold(Household household) {
        return householdRepository.save(household);
    }

    public Household getHousehold(Long householdId) {
        return householdRepository.findById(householdId).orElseThrow(HouseholdNotFoundException::new);
    }

    public List<Household> getHouseholds(
            Integer incomeGt, Integer incomeLt,
            Integer ageGt, Integer ageLt,
            boolean hasCouple) {
        Specification<Household> spec = Specification.where(null);
        if (incomeGt != null) {
            spec = spec.and(householdIncomeGreaterThan(incomeGt));
        }
        if (incomeLt != null) {
            spec = spec.and(householdIncomeLessThan(incomeLt));
        }
        if (ageGt != null) {
            LocalDate date = LocalDate.now().minusYears(ageGt);
            spec = spec.and(hasPersonOlderThan(date));
        }
        if (ageLt != null) {
            LocalDate date = LocalDate.now().minusYears(ageLt);
            spec = spec.and(hasPersonYoungerThan(date));
        }
        if (hasCouple) {
            spec = spec.and(hasCouple());
        }
        return householdRepository.findAll(spec);
    }

	public Person addFamilyMember(Long householdId, Person familyMember, Long spouseId) {
        Household household = getHousehold(householdId);
        familyMember.setHousehold(household);

        Person spouse = null;
        if (spouseId != null) {
            spouse = personRepository.findById(spouseId).orElseThrow(SpouseDoesNotExistException::new);
            familyMember.setSpouse(spouse);
            spouse.setSpouse(familyMember);
        }

        Person savedFamilyMember = personRepository.save(familyMember);
        if (spouse != null) {
            personRepository.save(spouse);
        }
        
        return savedFamilyMember;
	}

	public void removeHousehold(Long householdId) {
        try {
            householdRepository.deleteById(householdId);
        } catch (EmptyResultDataAccessException ex) {
            // do nothing
        }
    }

    public void removeFamilyMember(Long householdId, Long familyMemberId) {
        try {
            Person familyMember = personRepository.findById(familyMemberId).get();
            if (!familyMember.getHousehold().getId().equals(householdId)) {
                throw new FamilyMemberDoesNotExistException();
            }
            if (familyMember.getSpouse() != null) {
                Person spouse = familyMember.getSpouse();
                spouse.setSpouse(null);
                personRepository.save(spouse);
            }
            personRepository.delete(familyMember);
        } catch (EmptyResultDataAccessException | NoSuchElementException ex) {
            // do nothing
        }
    }

}
