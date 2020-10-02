package com.yeongzhiwei.assessment;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongzhiwei.assessment.model.Household;
import com.yeongzhiwei.assessment.model.HousingType;
import com.yeongzhiwei.assessment.repository.HouseholdRepository;

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
    void removeData() {
        householdRepository.deleteAll();
    }
    
    @Test
    void getHouseholds() throws Exception {
        this.mockMvc.perform(get("/households"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(rawHouseholds.length));
    }
    
    @Test
    void createHousehold_Success() throws Exception {
        Household household = new Household();
        household.setHousingType(HousingType.CONDOMINIUM);

        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(household)))
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
        Household household = new Household();

        this.mockMvc.perform(post("/households")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(household)))
            .andExpect(status().isBadRequest());

        assertEquals(rawHouseholds.length, householdRepository.count());
    }

    @Test
    void getHousehold_Success() throws Exception {
        Long validHouseholdId = householdRepository.findAll()
                                    .stream()
                                    .map(Household::getId)
                                    .reduce((id1, id2) -> id1)
                                    .get();

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

}
