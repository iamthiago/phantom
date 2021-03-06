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
package com.outworkers.phantom.builder.query.prepared

import com.outworkers.phantom.PhantomSuite
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.tables.Recipe
import com.outworkers.util.samplers._

class BatchablePreparedInsertQueryTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    val _ = database.recipes.createSchema()
  }

  it should "serialize an prepared batch query" in {
    val sample1 = gen[Recipe]
    val sample2 = gen[Recipe]

    val query = database.recipes.insert
      .p_value(_.url, ?)
      .p_value(_.description, ?)
      .p_value(_.ingredients, ?)
      .p_value(_.servings, ?)
      .p_value(_.lastcheckedat, ?)
      .p_value(_.props, ?)
      .p_value(_.uid, ?)
      .prepare()

    val exec1 = query.bind(sample1)
    val exec2 = query.bind(sample2)

    val chain = for {
      _ <- Batch.unlogged.add(exec1, exec2).future()
      get <- database.recipes.select.where(_.url in List(sample1.url, sample2.url)).fetch()
    } yield get

    whenReady(chain) { res =>
      res should contain theSameElementsAs List(sample1, sample2)
    }
  }
}
