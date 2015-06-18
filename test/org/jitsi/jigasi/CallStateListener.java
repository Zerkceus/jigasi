/*
 * Jigasi, the JItsi GAteway to SIP.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
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
package org.jitsi.jigasi;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.event.*;

import static org.junit.Assert.assertEquals;

/**
 * Class used to encapsulate the process of waiting for some <tt>CallState</tt>
 * set on given <tt>Call</tt> instance.
 *
 * @author Pawel domas
 */
public class CallStateListener
    extends CallChangeAdapter
{
    private CallState targetState;

    @Override
    public void callStateChanged(CallChangeEvent evt)
    {
        Call call = evt.getSourceCall();
        if (targetState.equals(call.getCallState()))
        {
            synchronized (this)
            {
                this.notifyAll();
            }
        }
    }

    public void waitForState(Call      watchedCall,
                             CallState targetState,
                             long      timeout)
        throws InterruptedException
    {
        this.targetState = targetState;

        // FIXME: we can miss call state anyway ?(but timeout will release)
        if (!targetState.equals(watchedCall.getCallState()))
        {
            synchronized (this)
            {
                watchedCall.addCallChangeListener(this);

                this.wait(timeout);
            }
        }

        watchedCall.removeCallChangeListener(this);

        assertEquals(targetState, watchedCall.getCallState());
    }
}