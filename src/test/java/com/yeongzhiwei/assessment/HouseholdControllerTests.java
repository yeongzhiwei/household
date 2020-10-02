package com.yeongzhiwei.assessment;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongzhiwei.assessment.dto.CreateFamilyMemberRequest;
import com.yeongzhiwei.assessment.dto.CreateHouseholdRequest;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.HousingType;
import com.yeongzhiwei.assessment.model.Person;
import com.yeongzhiwei.assessment.model.Person.Gender;
import com.yeongzhiwei.assessment.model.Person.MartialStatus;
import com.yeongzhiwei.assessment.model.Person.OccupationType;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;
import com.yeongzhiwei.assessment.repository.PersonRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HouseholdControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HouseholdRepository householdRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String[][] rawHouseholds = {{"LANDED"}, {"CONDOMINIUM"}, {"HDB"}};

    @BeforeEach
    void seedData() {
        for (String[] rawHousehold : rawHouseholds) {
            Household household = new Household();
            household.setHousingType(HousingType.valueOf(rawHousehold[0]));
            householdRepository.save(household);
        }
    }

    @AfterEach
    @Transactional
    void removeData() {
        householdRepository.deleteAll();
        personRepository.deleteAll();
    }
    
    @Test
    void getHouseholds() throws Exception {
        this.mockMvc.perform(get("/households"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(rawHouseholds.length));
    }
    
    @Test
    void createHousehold_Success() throws Exception {
        CreateHouseholdRequest request = new CreateHouseholdRequest();
        request.setHousingType(HousingType.CONDOMINIUM);

        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());

        assertEquals(rawHouseholds.length + 1, householdRepository.count());
    }

    @Test
    void createHousehold_EmptyBody() throws Exception {
        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        assertEquals(rawHouseholds.length, householdRepository.count());
    }

    @Test
    void createHousehold_NullHousingType() throws Exception {
        CreateHouseholdRequest request = new CreateHouseholdRequest();

        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertEquals(rawHouseholds.length, householdRepository.count());
    }

    @Test
    void getHousehold_Success() throws Exception {
        Long validHouseholdId = householdRepository.findAll().get(0).getId();

        this.mockMvc.perform(get("/households/" + validHouseholdId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.housingType").exists());
    }

    @Test
    void getHousehold_NotFound() throws Exception {
        List<Long> householdIds = householdRepository.findAll()
                                    .stream()
                                    .map(Household::getId)
                                    .collect(toList());
        Long invalidHouseholdId = 1L;
        while (householdIds.contains(invalidHouseholdId)) {
            invalidHouseholdId += 1;
        }  

        this.mockMvc.perform(get("/households/" + invalidHouseholdId))
            .andExpect(status().isNotFound());
    }

    @Test
    void addFamilyMember_Success() throws Exception {
        Household validHousehold = householdRepository.findAll().get(0);

        Person person = new Person();
        person.setName("Ah Lian");
        person.setGender(Gender.MALE);
        person.setMartialStatus(MartialStatus.MARRIED);
        person.setOccupationType(OccupationType.EMPLOYED);
        person.setAnnualIncome(100000);
        person.setDob(LocalDate.now());
        person.setHousehold(validHousehold);
        person = personRepository.save(person);

        CreateFamilyMemberRequest request = new CreateFamilyMemberRequest();
        request.setName("Ah Low");
        request.setGender(Gender.FEMALE);
        request.setMartialStatus(MartialStatus.MARRIED);
        request.setOccupationType(OccupationType.UNEMPLOYED);
        request.setDob(LocalDate.now());
        request.setSpouseId(person.getId());

        this.mockMvc.perform(post("/households/" + validHousehold.getId() + "/familymembers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.spouseId").value(person.getId()));

        assertEquals(2, personRepository.count());
    }

    @Test
    void addFamilyMember_NonExistantHouseholdId() throws Exception {
        List<Long> householdIds = householdRepository.findAll()
                                    .stream()
                                    .map(Household::getId)
                                    .collect(toList());
        Long invalidHouseholdId = 1L;
        while (householdIds.contains(invalidHouseholdId)) {
            invalidHouseholdId += 1;
        }
        CreateFamilyMemberRequest request = new CreateFamilyMemberRequest();
        request.setName("Ah Lian");
        request.setGender(Gender.MALE);
        request.setMartialStatus(MartialStatus.SINGLE);
        request.setOccupationType(OccupationType.EMPLOYED);
        request.setAnnualIncome(100000);
        request.setDob(LocalDate.now());

        this.mockMvc.perform(post("/households/" + invalidHouseholdId + "/familymembers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void addFamilyMember_NonExistantSpouseId() throws Exception {
        Long validHouseholdId = householdRepository.findAll().get(0).getId();
        CreateFamilyMemberRequest request = new CreateFamilyMemberRequest();
        request.setName("Ah Lian");
        request.setGender(Gender.MALE);
        request.setMartialStatus(MartialStatus.SINGLE);
        request.setSpouseId(999L);
        request.setOccupationType(OccupationType.EMPLOYED);
        request.setAnnualIncome(100000);
        request.setDob(LocalDate.now());

        this.mockMvc.perform(post("/households/" + validHouseholdId + "/familymembers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        assertEquals(0, personRepository.count());
    }
    
}
