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

import com.df.fhrs.config.FhrsProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

/**
 * Utility to create and populate FHRS URIs.
 *
 * @author bilalwahla
 */
@Component
class UriManipulation {

  private String fhrsBaseUri

  @Autowired
  FhrsProperties fhrsProperties

  URI createFhrsRequestUri(String path) {
    fhrsBaseUri = fhrsBaseUri ?: "$fhrsProperties.protocol://$fhrsProperties.host"
    UriComponentsBuilder.fromHttpUrl("$fhrsBaseUri$path").build().encode().toUri()
  }

  String populateSearchEstablishmentsByAuthorityPath(final int localAuthorityId) {
    fhrsProperties.searchEstablishmentsByAuthorityPath.replaceAll(
        /\{localAuthorityId}/, localAuthorityId.toString()
    )
  }
}
