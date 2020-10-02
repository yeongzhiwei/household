package com.yeongzhiwei.assessment.service;

import java.util.List;

import com.yeongzhiwei.assessment.exception.HouseholdNotFoundException;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HouseholdService {
    
    private HouseholdRepository householdRepository;

    public HouseholdService(@Autowired HouseholdRepository householdRepository) {
        this.householdRepository = householdRepository;
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

}
