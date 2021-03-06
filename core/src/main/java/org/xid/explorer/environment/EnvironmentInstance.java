/*
 * Copyright 2015 to CloudModelExplorer authors
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

package org.xid.explorer.environment;

import org.xid.explorer.dsl.DslInstance;
import org.xid.explorer.observation.Evaluator;

/**
 * Created by j5r on 08/01/2015.
 */
public interface EnvironmentInstance extends DslInstance {

    Evaluator[] getInvariants();
}
