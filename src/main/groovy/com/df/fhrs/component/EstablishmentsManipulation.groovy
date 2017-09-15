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
import com.df.fhrs.model.fhrs.response.Rating
import com.df.fhrs.model.fhrs.response.Ratings
import com.df.fhrs.service.FhrsService
import groovyx.gpars.GParsPool
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

import static java.math.RoundingMode.FLOOR
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

/**
 * This class contains methods to manipulate establishments data retrieved by applying different
 * search queries.
 *
 * @author bilalwahla
 */
@Component
class EstablishmentsManipulation {

  /**
   * The minimum recommended size is being set to number of CPUs * 2. Please be considerate of the
   * fact that configuring thread pool size to be too large may max out the CPU and degrade the
   * performance rather than improve it.
   */
  private static final int threadPoolSize = Runtime.runtime.availableProcessors() * 2

  private static final String DIGIT_RATING_POSTFIX = '-star'

  /*
  Following two variables are for a hack and do not let the solution be a generic one because FHRS
  API is inconsistent in how it defines and uses SchemeType with different regions in the UK.
   */
  private static final String FHRS = 'FHRS'
  private static final List<String> scottishRatingNames = ['Pass', 'Improvement Required']

  @Autowired
  FhrsService fhrsService

  def calculateRatingPercentage(Establishments establishments) {
    def ratingPercentage = [:]

    def ratingsResponse = fhrsService.retrieveAllRatings()
    if (ratingsResponse.statusCode != OK)
      return new ResponseEntity<Map<String, BigDecimal>>(ratingPercentage, INTERNAL_SERVER_ERROR)

    Ratings ratings = ratingsResponse.body
    GParsPool.withPool threadPoolSize, {
      boolean fhrsScheme = isFhrsScheme establishments.establishments.first().schemeType
      int totalEstablishments = establishments.establishments.size()

      filteredRatings(ratings.ratings, fhrsScheme).each { r ->
        int establishmentsFound = establishments.establishments.findAll { e ->
          fhrsScheme ? e.ratingValue == r.ratingKeyName : e.ratingValue.startsWith(r.ratingName)
        }.size()

        ratingPercentage.put(
            createKey(r.ratingKeyName),
            calculatePercentage(establishmentsFound, totalEstablishments).setScale(2, FLOOR)
        )
      }
    }

    new ResponseEntity<Map<String, BigDecimal>>(ratingPercentage, OK)
  }

  private static BigDecimal calculatePercentage(int found, int total) {
    found / total * 100
  }

  private static String createKey(String ratingKeyName) {
    ratingKeyName.matches('([012345]).*') ? "$ratingKeyName$DIGIT_RATING_POSTFIX" : ratingKeyName
  }

  private static List<Rating> filteredRatings(List<Rating> ratings, boolean fhrsScheme) {
    if (fhrsScheme) ratings.findAll { !(it.ratingName in scottishRatingNames) }
    else ratings.findAll { it.ratingName in scottishRatingNames }
  }

  private static boolean isFhrsScheme(String schemeType) {
    schemeType == FHRS
  }
}
