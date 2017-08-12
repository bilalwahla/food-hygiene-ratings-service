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
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * This class is used to setup generic HTTP headers for all FHRS requests e.g. X-Api-Version.
 *
 * This class might seem trivial right now but could be useful when if the API starts requiring a
 * lot more custom request headers and also when we need to setup security headers using e.g. our
 * unique API key.
 *
 * @author bilalwahla
 */
@Component
@Scope('prototype')
class FhrsHeadersFactory {
  @Autowired
  FhrsProperties fhrsProperties

  static final String ACCEPT = 'Accept'
  static final String X_API_VERSION = 'X-Api-Version'

  HttpHeaders headers

  @PostConstruct
  private void setup() {
    headers = new HttpHeaders()
    headers.add ACCEPT, MediaType.APPLICATION_JSON_VALUE
    headers.add X_API_VERSION, fhrsProperties.version
  }
}
