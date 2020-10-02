package com.yeongzhiwei.assessment.service;

import java.util.List;

import com.yeongzhiwei.assessment.exception.HouseholdNotFoundException;
import com.yeongzhiwei.assessment.exception.SpouseDoesNotExistException;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;
import com.yeongzhiwei.assessment.repository.PersonRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Household> getHouseholds() {
        return householdRepository.findAll();
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

}
