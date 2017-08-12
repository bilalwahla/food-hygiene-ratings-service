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

package com.df.fhrs.component

import com.df.fhrs.model.fhrs.response.Establishments
import com.df.fhrs.service.FhrsService
import spock.lang.Specification

/**
 * Unit test specification for EstablishmentsManipulation component.
 *
 * @author bilalwahla
 */
class EstablishmentsManipulationSpec extends Specification {

  private EstablishmentsManipulation establishmentsManipulation = new EstablishmentsManipulation()
  private FhrsService fhrsService = Mock()

  def setup() {
    establishmentsManipulation.fhrsService = fhrsService
  }

  def "should be able to calculate rating percentage for FHIS authority"() {
    given:
    Establishments establishments = [
        establishments: [
            [ratingValue: 'Pass', schemeType: 'FHIS'],
            [ratingValue: 'Pass', schemeType: 'FHIS'],
            [ratingValue: 'Pass', schemeType: 'FHIS'],
            [ratingValue: 'Pass', schemeType: 'FHIS'],
            [ratingValue: 'Improvement Required', schemeType: 'FHIS']
        ]
    ]

    when:
    def ratingPercentage = establishmentsManipulation.calculateRatingPercentage establishments

    then:
    1 * fhrsService.retrieveAllRatings() >> [
        ratings: [
            [ratingId: 8, ratingName: 'Pass', ratingKeyName: 'Pass'],
            [ratingId: 9, ratingName: 'Improvement Required', ratingKeyName: 'ImprovementRequired']
        ]
    ]
    ratingPercentage
    ratingPercentage.get('Pass') == 80.00
    ratingPercentage.get('ImprovementRequired') == 20.00
  }

  def "should be able to calculate rating percentage for FHRS authority"() {
    given:
    Establishments establishments = [
        establishments: [
            [ratingValue: '0', schemeType: 'FHRS'],
            [ratingValue: '1', schemeType: 'FHRS'],
            [ratingValue: '2', schemeType: 'FHRS'],
            [ratingValue: '3', schemeType: 'FHRS'],
            [ratingValue: '4', schemeType: 'FHRS'],
            [ratingValue: '4', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: '5', schemeType: 'FHRS'],
            [ratingValue: 'AwaitingPublication', schemeType: 'FHRS'],
            [ratingValue: 'AwaitingInspection', schemeType: 'FHRS'],
            [ratingValue: 'Exempt', schemeType: 'FHRS'],
            [ratingValue: 'Exempt', schemeType: 'FHRS']
        ]
    ]

    when:
    def ratingPercentage = establishmentsManipulation.calculateRatingPercentage establishments

    then:
    1 * fhrsService.retrieveAllRatings() >> [
        ratings: [
            [ratingId: 0, ratingName: '0', ratingKeyName: '0'],
            [ratingId: 1, ratingName: '1', ratingKeyName: '1'],
            [ratingId: 2, ratingName: '2', ratingKeyName: '2'],
            [ratingId: 3, ratingName: '3', ratingKeyName: '3'],
            [ratingId: 4, ratingName: '4', ratingKeyName: '4'],
            [ratingId: 5, ratingName: '5', ratingKeyName: '5'],
            [ratingId: 6, ratingName: 'Awaiting Publication', ratingKeyName: 'AwaitingPublication'],
            [ratingId: 7, ratingName: 'Awaiting Inspection', ratingKeyName: 'AwaitingInspection'],
            [ratingId: 8, ratingName: 'Exempt', ratingKeyName: 'Exempt']
        ]
    ]
    ratingPercentage
    ratingPercentage.get('5-star') == 50.00
    ratingPercentage.get('Exempt') == 10.00
  }
}
