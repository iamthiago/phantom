/*
 * Copyright 2013 - 2019 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.phantom.database

import com.datastax.driver.core.{ProtocolVersion, Session, VersionNumber}
import com.outworkers.phantom.connectors.{KeySpace, SessionAugmenterImplicits}

trait DatabaseProvider[T <: Database[T]] extends SessionAugmenterImplicits {

  def cassandraVersion: Option[VersionNumber] = database.connector.cassandraVersion

  def cassandraVersions: Set[VersionNumber] = database.connector.cassandraVersions

  implicit def session: Session = database.session

  implicit lazy val protocolVersion: ProtocolVersion = session.protocolVersion

  implicit def space: KeySpace = database.space

  def database: T

  /**
    * A helper shorthand method.
    * @return A reference to the database object.
    */
  def db: T = database
}
