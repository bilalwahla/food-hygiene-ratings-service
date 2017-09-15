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

package com.df.fhrs.service

import com.df.fhrs.component.EstablishmentsManipulation
import com.df.fhrs.component.UriManipulation
import com.df.fhrs.config.FhrsProperties
import com.df.fhrs.exception.NoEstablishmentFoundException
import com.df.fhrs.model.fhrs.response.Authorities
import com.df.fhrs.model.fhrs.response.Establishments
import com.df.fhrs.component.FhrsHeadersFactory
import com.df.fhrs.model.fhrs.response.Ratings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import static org.springframework.http.HttpMethod.*
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

/**
 * Service to communicate with FHRS API.
 *
 * @author bilalwahla
 */
@Service
class FhrsService {

  @Autowired
  FhrsProperties fhrsProperties

  @Autowired
  FhrsHeadersFactory fhrsHeadersFactory

  @Autowired
  UriManipulation uriManipulation

  @Autowired
  RestTemplate restTemplate

  @Autowired
  EstablishmentsManipulation establishmentsManipulation

  /**
   * Makes a call to FHRS API to retrieve all authorities with basic authority information.
   */
  def retrieveAllAuthorities() {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(fhrsHeadersFactory.headers)
    URI requestUri = uriManipulation.createFhrsRequestUri fhrsProperties.basicAuthoritiesPath
    ResponseEntity<Authorities> fhrsResponse = restTemplate.exchange requestUri, GET, requestEntity, Authorities
    fhrsResponse
  }

  /**
   * Makes a call to FHRS API to retrieve all ratings.
   */
  def retrieveAllRatings() {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(fhrsHeadersFactory.headers)
    URI requestUri = uriManipulation.createFhrsRequestUri fhrsProperties.ratingsPath
    ResponseEntity<Ratings> fhrsResponse = restTemplate.exchange requestUri, GET, requestEntity, Ratings
    fhrsResponse
  }

  /**
   * Makes a call to FHRS API to search for establishments by a given authority and calculates food
   * hygiene ratings percentage across that authority.
   */
  def calculateRatingPercentage(final int localAuthorityId) {
    HttpEntity<?> requestEntity = new HttpEntity<Object>(fhrsHeadersFactory.headers)
    String searchPath = uriManipulation.populateSearchEstablishmentsByAuthorityPath localAuthorityId
    URI requestUri = uriManipulation.createFhrsRequestUri searchPath
    ResponseEntity<Establishments> fhrsResponse = restTemplate.exchange requestUri, GET, requestEntity, Establishments

    if (fhrsResponse?.statusCode != OK)
      return new ResponseEntity<Map<String, BigDecimal>>([:], INTERNAL_SERVER_ERROR)

    if (!fhrsResponse?.body?.establishments) throw new NoEstablishmentFoundException(localAuthorityId)

    establishmentsManipulation.calculateRatingPercentage fhrsResponse.body
  }
}
