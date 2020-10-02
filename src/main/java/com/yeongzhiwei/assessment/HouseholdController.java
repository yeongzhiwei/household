package com.yeongzhiwei.assessment;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.Valid;

import com.yeongzhiwei.assessment.dto.CreateHouseholdRequest;
import com.yeongzhiwei.assessment.dto.HouseholdResponse;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.service.HouseholdService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<HouseholdResponse> getHouseholds() {
        List<Household> households = householdService.getHouseholds();
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

}
