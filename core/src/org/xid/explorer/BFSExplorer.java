/*
 * Copyright 2015 to CloudModelExplorer
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

package org.xid.explorer;

import org.xid.explorer.dsl.DslInstance;
import org.xid.explorer.dsl.DslState;
import org.xid.explorer.model.ModelInstance;
import org.xid.explorer.model.ModelState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j5r on 19/11/2014.
 */
public class BFSExplorer extends AbstractExplorer {

    private List<ModelState> toSee = new ArrayList<>();

    public BFSExplorer(ModelInstance modelInstance) {
        super(modelInstance);
    }

    @Override
    protected void exploreFrom(ModelState initialState) {

        while (toSee.size() > 0) {
            ModelState toExplore = toSee.remove(0);

            DslInstance[] instances = modelInstance.getInstances();
            Mailboxes mailboxes = toExplore.getMailboxes();

            for (int i = 0; i < instances.length; i++) {
                DslState dslSource = toExplore.getState(i);
                DslState dslTarget = dslSource.copy();

                // computes next
                Mailboxes mailboxesCopy = mailboxes.copy();
                instances[i].next(dslTarget, mailboxesCopy);
                if (dslTarget.equals(dslSource) == false || mailboxesCopy.equals(mailboxes) == false) {
                    // transition changed state, checks if a new model state has been found
                    ModelState target = registerState(toExplore.copy(i, dslTarget, mailboxesCopy));
                    registerTransition(toExplore, target);
                }
            }
        }
    }

    @Override
    protected void newState(ModelState newState) {
        toSee.add(newState);
    }
}
