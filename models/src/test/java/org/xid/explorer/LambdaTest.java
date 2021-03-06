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

import org.junit.Test;
import org.xid.explorer.dsl.DslTransition;

public class LambdaTest {

    public static final DslTransition TRANSITION_TEST0_1 = (context, state, mailboxes) -> {
        int n = context.getModelDescription().getIntParameterValue("one", "n", 0);
        int count = state.getInt(0);
        int newCount = count < n ? count + 1 : 0;
        state.setInt(0, newCount);
    };

    public static final DslTransition TRANSITION_TEST1_1 = (context, state, mailboxes) -> {
        int count = state.getInt(0);
        int newCount = count < 10 ? count + 1 : 0;
        state.setInt(0, newCount);
    };

    private static int countMethod(int count, int max) {
        return count < max ? count + 1 : 0;
    }

    public static final DslTransition TRANSITION_TEST2_1 = (context, state, mailboxes) -> {
        int count = state.getInt(0);
        int newCount = countMethod(count, 10);
        state.setInt(0, newCount);
    };

    public static final DslTransition TRANSITION_TEST3_1 = (context, state, mailboxes) -> {
        int mailbox = context.getModelDescription().getMailboxId("mailbox");
        int current = state.getInt(0);
        if (current == 0) {
            mailboxes.addLast(mailbox, "start");
            state.setInt(0, 1);
        } else {
            String result = mailboxes.removeFirstIf(mailbox, (message) -> "end".equals(message));
            if (result != null) mailboxes.addLast(mailbox, "start");
        }
    };

    public static final DslTransition TRANSITION_TEST3_2 = (context, state, mailboxes) -> {
        int count = state.getInt(0);
        if (count == 0) {
            String result = mailboxes.removeFirstIf(0, (message) -> "start".equals(message));
            if (result != null) count = 1;
        } else if (count >= 10) {
            count = 0;
            mailboxes.addLast(0, "end");
        } else {
            count = count + 1;
        }
        state.setInt(0, count);
    };

    /**
     * Simple test with a basic lambda.
     */
    @Test
    public void test0() throws Exception {
        TestUtil.explore("lambda/test0/", 3, 3);
    }

    /**
     * Simple test with a basic lambda and several instances
     */
    @Test
    public void test1() throws Exception {
        TestUtil.explore("lambda/test1/", 1331, 3993);
    }

    /**
     * Test with an existing transition to apply from the instance.
     */
    @Test
    public void test2() throws Exception {
        TestUtil.explore("lambda/test2/", 1331, 3993);
    }

    /**
     * Simple test with a basic lambda and 2 instances that communicates using a mailbox.
     */
    @Test
    public void test3() throws Exception {
        TestUtil.explore("lambda/test3/", 13, 13);
    }

}
