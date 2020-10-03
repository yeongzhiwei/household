package com.yeongzhiwei.assessment;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.Valid;

import com.yeongzhiwei.assessment.dto.CreateFamilyMemberRequest;
import com.yeongzhiwei.assessment.dto.CreateHouseholdRequest;
import com.yeongzhiwei.assessment.dto.FamilyMemberResponse;
import com.yeongzhiwei.assessment.dto.HouseholdResponse;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.service.HouseholdService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/households")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public HouseholdResponse createHousehold(@RequestBody @Valid final CreateHouseholdRequest request) {
        Household household = modelMapper.map(request, Household.class);
        household = householdService.createHousehold(household);
        return modelMapper.map(household, HouseholdResponse.class);
    }

    @PostMapping(path = "/{householdId}/familymembers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public FamilyMemberResponse addFamilyMember(
            @PathVariable final Long householdId,
            @RequestBody @Valid final CreateFamilyMemberRequest request) {
        Person familyMember = modelMapper.map(request, Person.class);
        familyMember = householdService.addFamilyMember(householdId, familyMember, request.getSpouseId());
        return modelMapper.map(familyMember, FamilyMemberResponse.class);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<HouseholdResponse> getHouseholds(
            @RequestParam(name = "income_gt", required = false) Integer incomeGt,
            @RequestParam(name = "income_lt", required = false) Integer incomeLt,
            @RequestParam(name = "age_gt", required = false) Integer ageGt,
            @RequestParam(name = "age_lt", required = false) Integer ageLt,
            @RequestParam(name = "couple", required = false) String couple) {
        List<Household> households = householdService.getHouseholds(incomeGt, incomeLt, ageGt, ageLt, couple != null);
        return households.stream()
            .map(household -> modelMapper.map(household, HouseholdResponse.class))
            .collect(toList());
    }
    
    @GetMapping("/{householdId}")
    @ResponseStatus(code = HttpStatus.OK)
    public HouseholdResponse getHousehold(@PathVariable Long householdId) {
        Household household = householdService.getHousehold(householdId);
        return modelMapper.map(household, HouseholdResponse.class);
    }

    /* OPTIONAL END-POINTS */

    @DeleteMapping("/{householdId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteHousehold(@PathVariable Long householdId) {
        householdService.removeHousehold(householdId);
    }

    @DeleteMapping("/{householdId}/familymembers/{familyMemberId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteFamilyMember(@PathVariable Long householdId, @PathVariable Long familyMemberId) {
        householdService.removeFamilyMember(householdId, familyMemberId);
    }

}
