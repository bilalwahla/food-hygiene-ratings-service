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
import com.df.fhrs.model.fhrs.response.Authority
import com.df.fhrs.model.fhrs.response.Rating
import com.df.fhrs.model.fhrs.response.Ratings
import com.df.fhrs.service.FhrsService
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK

/**
 * Unit test specification for FhrsController controller.
 *
 * @author bilalwahla
 */
class FhrsControllerSpec extends Specification {

  private FhrsController fhrsController = new FhrsController()
  private FhrsService fhrsService = Mock()

  def setup() {
    fhrsController.fhrsService = fhrsService
  }

  def "should be able to retrieve authorities"() {
    when:
    def response = fhrsController.retrieveAuthorities()

    then:
    1 * fhrsService.retrieveAllAuthorities() >> new ResponseEntity<Authorities>(
        [
            authorities: [
                [localAuthorityId: 1, name: 'A City'],
                [localAuthorityId: 2, name: 'B City']
            ]
        ] as Authorities,
        OK
    )
    response.body.authorities.size() == 2
    Authority authority = response.body.authorities.get(0)
    authority.localAuthorityId == 1
    authority.name == 'A City'
  }

  def "should be able to retrieve ratings"() {
    when:
    def response = fhrsController.retrieveRatings()

    then:
    1 * fhrsService.retrieveAllRatings() >> new ResponseEntity<Ratings>(
        [
            ratings: [
                [ratingId: 5, ratingName: '4', ratingKeyName: '4'],
                [ratingId: 6, ratingName: '5', ratingKeyName: '5']
            ]
        ] as Ratings,
        OK
    )
    response.body.ratings.size() == 2
    Rating rating = response.body.ratings.get(0)
    rating.ratingId == 5
    rating.ratingName == '4'
    rating.ratingKeyName == '4'
  }

  def "should be able to calculate food hygiene rating percentage for a FHIS authority"() {
    given:
    int localAuthorityId = 1

    when:
    def response = fhrsController.calculateFoodHygieneRatingPercentage localAuthorityId

    then:
    1 * fhrsService.calculateRatingPercentage(localAuthorityId) >> new ResponseEntity<Map<String, BigDecimal>>(
        [
            'Pass': 70.00, 'Improvement Required': 30.00
        ],
        OK
    )
    response.body.get('Pass') == 70.00
    response.body.get(('Improvement Required')) == 30.00
  }

  def "should be able to calculate food hygiene rating percentage for a FHRS authority"() {
    given:
    int localAuthorityId = 2

    when:
    def response = fhrsController.calculateFoodHygieneRatingPercentage localAuthorityId

    then:
    1 * fhrsService.calculateRatingPercentage(localAuthorityId) >> new ResponseEntity<Map<String, BigDecimal>>(
        [
            '5-star': 10.00,
            '4-star': 10.00,
            '3-star': 10.00,
            '2-star': 10.00,
            '1-star': 10.00,
            '0-star': 00.00,
            'AwaitingPublication': 00.00,
            'AwaitingInspection': 19.50,
            'Exempt': 30.50
        ],
        OK
    )
    response.body.get('5-star') == 10.00
    response.body.get(('AwaitingPublication')) == 00.00
    response.body.get(('AwaitingInspection')) == 19.50
    response.body.get(('Exempt')) == 30.50
  }
}