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

import com.df.fhrs.model.fhrs.response.Authorities
import com.df.fhrs.model.fhrs.response.Ratings
import com.df.fhrs.service.FhrsService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoints to retrieve authorities' details.
 *
 * @author bilalwahla
 */
@Slf4j
@RestController
class FhrsController {

  @Autowired
  FhrsService fhrsService

  /**
   * Retrieves all authorities with basic authority information.
   * @return a collection of authorities
   */
  @RequestMapping('/authorities')
  def retrieveAuthorities() {
    log.debug 'Retrieving authorities'
    fhrsService.retrieveAllAuthorities()
  }

  /**
   * Retrieves all ratings.
   * @return a collection of ratings
   */
  @RequestMapping('/ratings')
  def retrieveRatings() {
    log.debug 'Retrieving ratings'
    fhrsService.retrieveAllRatings()
  }

  /**
   * Calculates food hygiene ratings percentage across a given Local Authority.
   * @param localAuthorityId to retrieve food hygiene ratings for
   */
  @RequestMapping('/authorities/{localAuthorityId}/ratingsPercentage')
  def calculateFoodHygieneRatingPercentage(@PathVariable final int localAuthorityId) {
    log.debug "calculating food hygiene rating percentage for authority $localAuthorityId"
    fhrsService.calculateRatingPercentage localAuthorityId
  }
}
