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

package org.xid.explorer.cdl;

import obp.cc.ConcreteContext;
import obp.cc.State;
import obp.cc.Transition;
import obp.cdl.EventReference;
import obp.event.Event;
import obp.event.Informal;
import obp.event.Input;
import obp.event.Output;
import obp.util.CDLUtil;
import org.xid.explorer.dsl.BinaryDslState;
import org.xid.explorer.dsl.DslState;
import org.xid.explorer.dsl.DslTransition;
import org.xid.explorer.environment.EnvironmentInstance;
import org.xid.explorer.observation.Evaluator;
import org.xid.explorer.observation.Matcher;

import java.util.List;

/**
 * Created by j5r on 31/12/2014.
 */
public class CcInstance implements EnvironmentInstance {

    public static final String CONTEXT_MAILBOX = "context";

    private final ConcreteContext concreteContext;
    private final List<State> concreteContextStateList;

    private final DslTransition[] transitions;

    public CcInstance(ConcreteContext concreteContext) {
        // TODO handle predicates.
        this.concreteContext = concreteContext;
        this.concreteContextStateList = concreteContext.getStateList();

        transitions = new DslTransition[concreteContext.getTransitionCount()];
        for (int i = 0; i < transitions.length; i++) {
            transitions[i] = createTransition(concreteContext.getTransition(i));
        }
    }

    private boolean isInputTransition(final Transition ccTransition) {
        EventReference action = ccTransition.getAction();
        return action != null && action.getReference().getIs() instanceof Input;
    }

    private DslTransition createTransition(/*final ExplorationContext context, */ final Transition ccTransition) {

        // stores source and target information
        final int source = getStateId(ccTransition.getSource());
        final int target = getStateId(ccTransition.getTarget());

        if ( ccTransition.getAction() != null ) {
            Event event = ccTransition.getAction().getReference().getIs();
            if ( event instanceof Output) {
                // action is a message send
                Output output = (Output) event;

                // FIXME construct message value
                Object value = "message"; //getMessageValue(output.getMessage(), context);
                final String mailboxName = CDLUtil.toQueueName(output.getTo());
                return createOutputObjectSignalTransition(source, target, mailboxName, value);

            } else if ( event instanceof Input) {
                // FIXME Create event matcher
                Matcher matcher = Matcher.TRUE;
                return createInputTransition(source, target, matcher);

            } else if ( event instanceof Informal) {
                // action is informal
                final String tag = ((Informal) event).getTag();
                short informalId = -1; //getOrCreateInformalId(tag, symbols);
                // TODO add support for informal
                return createInformalTransition(source, target, informalId);

            } else {
                throw new IllegalArgumentException("Event named '" + event.getName() + "' isn't a valid CDL expression.");
            }
        } else {
            // action is null
            return createEmptyTransition(source, target);
        }
    }

    private DslTransition createOutputObjectSignalTransition(int source, int target, String mailboxName, Object message) {
        return (context, state, mailboxes) -> {
            final int mailboxId = context.getModelDescription().getMailboxId(mailboxName);
            if (state.getInt(0) == source) {
                mailboxes.addLast(mailboxId, message.toString());
                state.setInt(0, target);
            }
        };
    }

    private DslTransition createInformalTransition(int source, int target, short informalId) {
        return (context, state, mailboxes) -> {
            if (state.getInt(0) == source) {
                // TODO add support for informal
                state.setInt(0, target);
            }
        };
    }

    private DslTransition createEmptyTransition(int source, int target) {
        return (context, state, mailboxes) -> {
            if (state.getInt(0) == source) state.setInt(0, target);
        };
    }

    private DslTransition createInputTransition(int source, int target, Matcher eventMatcher) {
        return (context, state, mailboxes) -> {
            if (state.getInt(0) == source) {
                final int mailboxId = context.getModelDescription().getMailboxId(CONTEXT_MAILBOX);
                mailboxes.removeFirstIf(mailboxId, (value) -> eventMatcher.match(context, value));
                state.setInt(0, target);
            }
        };
    }

    @Override
    public String getName() {
        return concreteContext.getName();
    }

    @Override
    public Evaluator[] getInvariants() {
        // TODO implements invariants
        return new Evaluator[0];
    }

    private int getStateId(State state) {
        return concreteContextStateList.indexOf(state);
    }

    @Override
    public DslState createInitialState() {
        int size = 4;
        // TODO optimize size if number of state can be store in a byte, a short or an int.
        BinaryDslState state = new BinaryDslState(size);
        state.setInt(0, getStateId(concreteContext.getStartState()));
        return state;
    }

    @Override
    public DslTransition[] getTransitions() {
        return transitions;
    }
}