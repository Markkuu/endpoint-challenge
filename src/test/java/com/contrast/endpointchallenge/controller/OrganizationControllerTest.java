package com.contrast.endpointchallenge.controller;

import com.contrast.endpointchallenge.dto.ApplicationDTO;
import com.contrast.endpointchallenge.dto.OrganizationDTO;
import com.contrast.endpointchallenge.service.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.contrast.endpointchallenge.givens.ApplicationGivens.anyApplicationDTOList;
import static com.contrast.endpointchallenge.givens.OrganizationGivens.anyOrganizationDTO;
import static com.contrast.endpointchallenge.givens.OrganizationGivens.anyOrganizationDTOList;
import static com.contrast.endpointchallenge.givens.util.EndpointConstants.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(OrganizationController.class)
public class OrganizationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrganizationService service;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getOrganizations_returnsOrganizations() throws Exception {

        //given
        List<OrganizationDTO> expected = anyOrganizationDTOList();

        String expectedJson = objectMapper.writeValueAsString(expected);

        //when
        doAnswer(ans -> {
            Consumer<OrganizationDTO> consumer = ans.getArgument(0);
            expected.forEach(consumer);
            return null;
        }).when(service).getAllOrganizations(any());

        //then
        mvc.perform(get(ORGANIZATIONS)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedJson)));
    }

    @Test
    public void getOrganizationById_returnsCorrectOrganization() throws Exception {
        //given
        OrganizationDTO expected = anyOrganizationDTO();

        String expectedJson = objectMapper.writeValueAsString(expected);

        //when
        when(service.getOrganizationById(expected.getOrgId())).thenReturn(expected);

        //then
        mvc.perform(get(ORGANIZATION, expected.getOrgId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedJson)));
    }

    @Test
    public void getApplicationsByOrganizationId_returnsCorrectApplications() throws Exception {
        //given
        List<ApplicationDTO> expected = anyApplicationDTOList();

        String expectedJson = objectMapper.writeValueAsString(expected);

        UUID orgId = UUID.randomUUID();

        //when
        doAnswer(ans -> {
            Consumer<ApplicationDTO> consumer = ans.getArgument(0);
            expected.forEach(consumer);
            return null;
        }).when(service).getApplicationsByOrgId(any(), eq(orgId), any(), any());

        //then
        mvc.perform(get(ORGANIZATION_APPLICATIONS, orgId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedJson)));
    }
}
