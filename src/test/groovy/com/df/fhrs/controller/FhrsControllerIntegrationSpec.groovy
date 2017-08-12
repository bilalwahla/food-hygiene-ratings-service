/*
 * Copyright (c) [2017] [dragonfly Technologies]
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License.
 */

package com.df.fhrs.controller

import com.df.fhrs.FoodHygieneRatingsServiceApplication
import net.minidev.json.JSONValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.junit.Assert.assertNotNull
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * Integration test specification for FhrsController controller.
 *
 * @author bilalwahla
 */
@SpringBootTest(classes = FoodHygieneRatingsServiceApplication)
@WebAppConfiguration
class FhrsControllerIntegrationSpec extends Specification {

  static final String RETRIEVE_AUTHORITIES = '/authorities'
  static final String RETRIEVE_RATINGS = '/ratings'
  static final String CALCULATE_FOOD_HYGIENE_RATING_PERCENTAGE = '/authorities/{localAuthorityId}/ratingsPercentage'

  private MockMvc mockMvc

  @Autowired
  private RestTemplate restTemplate

  @Autowired
  private WebApplicationContext webApplicationContext

  def setup() {
    this.mockMvc = webAppContextSetup(webApplicationContext).build()
  }

  def "should be able to retrieve authorities"() {
    when:
    String response = mockMvc.perform(get(RETRIEVE_AUTHORITIES))
    .andExpect(status().is(HttpStatus.OK.value()))
    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    .andReturn().response.contentAsString

    then:
    def parsedResponse = JSONValue.parse response
    assert parsedResponse.authorities.size() > 0
  }

  def "should be able to retrieve ratings"() {
    when:
    String response = mockMvc.perform(get(RETRIEVE_RATINGS))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andReturn().response.contentAsString

    then:
    def parsedResponse = JSONValue.parse response
    assert parsedResponse.ratings.size() > 0
  }

  def "should be able to calculate food hygiene rating percentage for a FHIS authority"() {
    given:
    int localAuthorityId = 197
    String path = CALCULATE_FOOD_HYGIENE_RATING_PERCENTAGE.replaceAll(
        /\{localAuthorityId}/, localAuthorityId.toString()
    )

    when:
    String response = mockMvc.perform(get(path))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andReturn().response.contentAsString

    then:
    def parsedResponse = JSONValue.parse response
    assertNotNull parsedResponse
  }

  def "should be able to calculate food hygiene rating percentage for a FHRS authority"() {
    given:
    int localAuthorityId = 406
    String path = CALCULATE_FOOD_HYGIENE_RATING_PERCENTAGE.replaceAll(
        /\{localAuthorityId}/, localAuthorityId.toString()
    )

    when:
    String response = mockMvc.perform(get(path))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andReturn().response.contentAsString

    then:
    def parsedResponse = JSONValue.parse response
    assertNotNull parsedResponse
  }

  def "should throw NoEstablishmentFoundException given an unknown authority to calculate food hygiene rating percentage"() {
    given:
    int localAuthorityId = -1
    String path = CALCULATE_FOOD_HYGIENE_RATING_PERCENTAGE.replaceAll(
        /\{localAuthorityId}/, localAuthorityId.toString()
    )

    when:
    String response = mockMvc.perform(get(path))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andReturn().response.contentAsString

    then:
    def parsedResponse = JSONValue.parse response
    assert parsedResponse.errorMessage.contains(localAuthorityId.toString())
  }
}
