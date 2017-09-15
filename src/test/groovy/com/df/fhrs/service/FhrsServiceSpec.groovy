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
import com.df.fhrs.component.FhrsHeadersFactory
import com.df.fhrs.component.UriManipulation
import com.df.fhrs.config.FhrsProperties
import com.df.fhrs.exception.NoEstablishmentFoundException
import com.df.fhrs.model.fhrs.response.Authorities
import com.df.fhrs.model.fhrs.response.Authority
import com.df.fhrs.model.fhrs.response.Establishments
import com.df.fhrs.model.fhrs.response.Rating
import com.df.fhrs.model.fhrs.response.Ratings
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

/**
 * Unit test specification for FhrsService service.
 *
 * @author bilalwahla
 */
class FhrsServiceSpec extends Specification {

  private FhrsService fhrsService = new FhrsService()
  private FhrsProperties fhrsProperties = Mock()
  private FhrsHeadersFactory fhrsHeadersFactory = Mock()
  private UriManipulation uriManipulation = Mock()
  private RestTemplate restTemplate = Mock()
  private EstablishmentsManipulation establishmentsManipulation = Mock()

  def setup() {
    fhrsService.fhrsProperties = fhrsProperties
    fhrsService.fhrsHeadersFactory = fhrsHeadersFactory
    fhrsService.uriManipulation = uriManipulation
    fhrsService.restTemplate = restTemplate
    fhrsService.establishmentsManipulation = establishmentsManipulation
  }

  def "should be able to retrieve all authorities"() {
    when:
    def response = fhrsService.retrieveAllAuthorities()

    then:
    1 * restTemplate.exchange(*_) >> new ResponseEntity<Authorities>(
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

  def "should be able to retrieve all ratings"() {
    when:
    def response = fhrsService.retrieveAllRatings()

    then:
    1 * restTemplate.exchange(*_) >> new ResponseEntity<Ratings>(
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

  def "should be able to calculate rating percentage for a FHIS authority"() {
    given:
    int localAuthorityId = 1

    when:
    def response = fhrsService.calculateRatingPercentage localAuthorityId

    then:
    1 * restTemplate.exchange(*_) >> new ResponseEntity<Establishments>(
        [
            establishments: [
                [ratingValue: 'Pass', schemeType: 'FHIS']
            ]
        ] as Establishments,
        OK
    )
    1 * establishmentsManipulation.calculateRatingPercentage(_) >> new ResponseEntity<Map<String, BigDecimal>>(
        [
            'Pass': 75.00, 'Improvement Required': 25.00
        ],
        OK
    )
    response.body.get('Pass') == 75.00
    response.body.get(('Improvement Required')) == 25.00
  }

  def "should get an internal server error when there was a problem retrieving establishments from FHRS API"() {
    given:
    int localAuthorityId = 2

    when:
    def response = fhrsService.calculateRatingPercentage localAuthorityId

    then:
    1 * restTemplate.exchange(*_) >> new ResponseEntity<Establishments>(
        [
            establishments: []
        ] as Establishments,
        INTERNAL_SERVER_ERROR
    )
    response.statusCode == INTERNAL_SERVER_ERROR
  }

  def "should be able to calculate rating percentage for a FHRS authority"() {
    given:
    int localAuthorityId = 2

    when:
    def response = fhrsService.calculateRatingPercentage localAuthorityId

    then:
    1 * restTemplate.exchange(*_) >> new ResponseEntity<Establishments>(
        [
            establishments: [
                [ratingValue: '0', schemeType: 'FHRS']
            ]
        ] as Establishments,
        OK
    )
    1 * establishmentsManipulation.calculateRatingPercentage(_) >> new ResponseEntity<Map<String, BigDecimal>>(
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

  def "should throw NoEstablishmentFoundException given an unknown authority"() {
    given:
    int unknownAuthorityId = -1

    when:
    fhrsService.calculateRatingPercentage unknownAuthorityId

    then:
    1 * restTemplate.exchange(*_) >> new ResponseEntity<Establishments>(
        [
            establishments: []
        ] as Establishments,
        OK
    )
    NoEstablishmentFoundException noEstablishmentFoundException = thrown()
    noEstablishmentFoundException.message.contains(unknownAuthorityId.toString())
  }
}
