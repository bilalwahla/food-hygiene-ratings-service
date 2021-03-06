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

package com.df.fhrs.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * All externalised properties are read here.
 *
 * @author bilalwahla
 */
@Component
class FhrsProperties {

  @Value('${fhrs.api.protocol}')
  String protocol

  @Value('${fhrs.api.host}')
  String host

  @Value('${fhrs.api.version}')
  String version

  @Value('${fhrs.api.path.authorities.basic}')
  String basicAuthoritiesPath

  @Value('${fhrs.api.path.ratings}')
  String ratingsPath

  @Value('${fhrs.api.path.establishments.search.authority}')
  String searchEstablishmentsByAuthorityPath
}
